package com.suntrust.dotcom.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.services.DynamicListService;

/**
 * This DynamicListServiceImpl is used search pages with tags.
 * 
 * @author Cognizant
 * @version 1.0
 * @since 10 July 2017
 * 
 */
@Component
@Service
public class DynamicListServiceImpl implements DynamicListService {
	/** Logger variable to log program state * */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DynamicListService.class);
	/** Constant variable to store JCRCONTENTPATH * */
	public static final String JCRCONTENTPATH = "/jcr:content";
	/** Constant variable to store DOTCOMREADSERVICE * */
	public static final String DOTCOMREADSERVICENAME = "dotcomreadservice";

	public static final String QUERYSELECTSTATEMENT = "SELECT p.* FROM [cq:Page] AS p WHERE ISDESCENDANTNODE(p, [";

	public static final String PRIMARY_TAG = " (p.[jcr:content/primarytag] = '";
	public static final String CONTENT_TYPE_TAG = " p.[jcr:content/contenttype] = '";
	public static final String CQ_TAG = " p.[jcr:content/cq:tags] = '";
	public static final String AND = " and ";
	public static final String OR = "' or ";

	/** Service variable to access ResourceResolverFactory* */
	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	/**
	 * To get Results From Query
	 * 
	 * @param contextUrl
	 * @param authorTags
	 * @param authorLogic
	 * @param criteriaForRule
	 * @param currentPagePath
	 * @return resultPages
	 */
	@Override
	public List<String> getResultsFromQuery(String contextUrl,
			String authorTags, String authorLogic, String criteriaForRule,
			String currentPagePath) {
		Session session = null;
		String sqlStatement = "";
		ResourceResolver resourceResolver = null;
		List<String> resultPages = new ArrayList<String>();
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, DOTCOMREADSERVICENAME);
			resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);
			session = resourceResolver.adaptTo(Session.class);
			QueryManager queryManager = session.getWorkspace()
					.getQueryManager();

			if (StringUtils.isBlank(authorLogic)
					|| StringUtils.isBlank(authorTags)) {
				sqlStatement = QUERYSELECTSTATEMENT + contextUrl + "])";
			} else if (authorTags.contains(",")) {
				String queryCondition = "";
				String splitedTags[] = authorTags.split(",");
				for (String tags : splitedTags) {
					if (StringUtils.isNotBlank(tags)) {
						if (StringUtils.isBlank(queryCondition))
							queryCondition = queryCondition + AND + PRIMARY_TAG
									+ tags + OR + CONTENT_TYPE_TAG + tags + OR
									+ CQ_TAG + tags + "')";
						else
							queryCondition = queryCondition + " " + authorLogic
									+ PRIMARY_TAG + tags + OR
									+ CONTENT_TYPE_TAG + tags + OR + CQ_TAG
									+ tags + "')";
					}
				}
				sqlStatement = QUERYSELECTSTATEMENT + contextUrl + "])"
						+ queryCondition;
			} else {
				sqlStatement = QUERYSELECTSTATEMENT + contextUrl + "])" + AND
						+ PRIMARY_TAG + authorTags + OR + CONTENT_TYPE_TAG
						+ authorTags + OR + CQ_TAG + authorTags + "')";
			}
			if (StringUtils.isNotBlank(criteriaForRule)
					&& "most-recently-published".equals(criteriaForRule)) {
				sqlStatement = sqlStatement
						+ " ORDER BY [jcr:content/cq:lastModified] desc";
			}
			Query query = queryManager.createQuery(sqlStatement, "JCR-SQL2");
			QueryResult result = query.execute();
			NodeIterator iterator = result.getNodes();
			while (iterator.hasNext()) {
				String path = ((Node) iterator.next()).getPath();
				if (!currentPagePath.equalsIgnoreCase(path)) {
					resultPages.add(path);
				}
			}
			if (StringUtils.isNotBlank(criteriaForRule)
					&& "random".equals(criteriaForRule)) {
				Collections.shuffle(resultPages);
			}
			return resultPages;
		} catch (RepositoryException | LoginException e) {
			LOGGER.error("Exception getResultsFromQuery, {}", e.getMessage());
		} finally {
			if (resourceResolver != null) {
				resourceResolver.close();
			}
			if (session != null && session.isLive()) {
				session.logout();
			}
		}
		return resultPages;
	}

	/**
	 * To get Results From Query for pooled tags
	 * 
	 * @param contextUrl
	 * @param authorTags
	 * @param authorLogic
	 * @param criteriaForRule
	 * @param currentPagePath
	 * @return resultPages
	 */
	public List<String> getResultsFromQueryForPooledTags(String contextUrl,
			List<String> authorTagsArray, List<String> authorLogicArray,
			String criteriaForRule, String currentPagePath) {
		Session session = null;
		String sqlStatement = "";
		ResourceResolver resourceResolver = null;
		List<String> resultPages = new ArrayList<String>();
		int counter = 0;
		String queryCondition = "";
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, DOTCOMREADSERVICENAME);
			resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);
			session = resourceResolver.adaptTo(Session.class);
			QueryManager queryManager = session.getWorkspace()
					.getQueryManager();
			if (authorLogicArray.isEmpty() || authorTagsArray.isEmpty()) {
				sqlStatement = QUERYSELECTSTATEMENT + contextUrl + "])";
			} else {
				for (String authorTags : authorTagsArray) {
					if (StringUtils.isNotBlank(authorTags)) {
						String condition = "";
						if (StringUtils.isBlank(queryCondition))
							queryCondition = queryCondition + " and ((";
						else
							queryCondition = queryCondition + " or (";
						if (authorTags.contains(",")) {
							String splitedTags[] = authorTags.split(",");

							for (String tags : splitedTags) {
								if (StringUtils.isNotBlank(tags)) {
									if (condition.contains("contains")) {
										condition = condition + " "
												+ authorLogicArray.get(counter)
												+ PRIMARY_TAG + tags + OR
												+ CONTENT_TYPE_TAG + tags + OR
												+ CQ_TAG + tags + "')";

									} else
										condition = condition + PRIMARY_TAG
												+ tags + OR + CONTENT_TYPE_TAG
												+ tags + OR + CQ_TAG + tags
												+ "')";
								}
							}
						} else {
							condition = condition + PRIMARY_TAG + authorTags
									+ OR + CONTENT_TYPE_TAG + authorTags + OR
									+ CQ_TAG + authorTags + "')";
						}
						++counter;
						queryCondition = queryCondition + condition + ")";
					}
					if (StringUtils.isNotBlank(queryCondition))
						sqlStatement = QUERYSELECTSTATEMENT + contextUrl + "])"
								+ queryCondition + ")";
					else
						sqlStatement = QUERYSELECTSTATEMENT + contextUrl + "])";
				}
			}
			if (StringUtils.isNotBlank(criteriaForRule)
					&& "most-recently-published".equals(criteriaForRule)) {
				sqlStatement = sqlStatement
						+ " ORDER BY [jcr:content/cq:lastModified] desc";
			}

			Query query = queryManager.createQuery(sqlStatement, "JCR-SQL2");
			QueryResult result = query.execute();
			NodeIterator iterator = result.getNodes();
			while (iterator.hasNext()) {
				String path = ((Node) iterator.next()).getPath();
				if (!currentPagePath.equalsIgnoreCase(path)) {
					resultPages.add(path);
				}
			}
			if (StringUtils.isNotBlank(criteriaForRule)
					&& "random".equals(criteriaForRule)) {
				Collections.shuffle(resultPages);
			}

			return resultPages;
		} catch (RepositoryException | LoginException e) {
			LOGGER.error("Exception getResultsFromQuery, {}", e.getMessage());
		} finally {
			if (resourceResolver != null) {
				resourceResolver.close();
			}
			if (session != null && session.isLive()) {
				session.logout();
			}
		}
		return resultPages;
	}

}
