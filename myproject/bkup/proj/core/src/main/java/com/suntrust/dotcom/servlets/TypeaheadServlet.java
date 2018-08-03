/**
 * 
 */
package com.suntrust.dotcom.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.TypeAheadSearchService;

@SuppressWarnings("serial")
@SlingServlet(metatype = false, paths = { "/dotcom/search/typeahead" }, methods = { "GET" })
public class TypeaheadServlet extends SlingSafeMethodsServlet {

	/** Default log. */
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	/** Service variable to read the run-mode configurations * */
	@Reference
	private SuntrustDotcomService dotcomService;
	/** Service variable to resolve resources * */
	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private TypeAheadSearchService typeAheadSearchService;

	private final String LOCATION_TEMPLATE = "dotcom/templates/locationdetailstemplate";
	private final String PEOPLE_TEMPLATE = "dotcom/templates/peopleprofiletemplate";
	private final String FAQ_TEMPLATE = "dotcom/templates/faqtemplate";
	private final String RESOURCE_CENTER_TEMPLATE = "/conf/dotcom-project/settings/wcm/templates/rc-article-template";
	private final String PRODUCT_TEMPLATE = "/conf/dotcom-project/settings/wcm/templates/product-page";
	private final String ROOT_PAGE = "/content/suntrust/dotcom/us/en";
	private final String RC_CALCULATOR = "/conf/dotcom-project/settings/wcm/templates/rc-calculator-template";
	private final String RC_ARTICLE = "/conf/dotcom-project/settings/wcm/templates/rc-article-template";
	private final String RC_AUDIO = "/conf/dotcom-project/settings/wcm/templates/rc-audio-template";
	private final String RC_VIDEO = "/conf/dotcom-project/settings/wcm/templates/rc-video-template";
	

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {

		try {
			LOGGER.info("inside TypeaheadServlet");
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(param);

			Session session = resourceResolver.adaptTo(Session.class);
			LOGGER.info("session" + session.getUserID());
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

			/*
			 * String dictFilePath =
			 * StringUtils.isNotBlank(request.getParameter("searchpath")) ?
			 * request.getParameter("searchpath") :
			 * "/content/dam/suntrust/us/en/internal-applications/search-keywords";
			 */

			String dictFilePath = typeAheadSearchService.getDictDamPath();
			String searchPath = typeAheadSearchService.getDictContentPath();

			searchPath = StringUtils.isNotBlank(searchPath) ? searchPath : ROOT_PAGE;

			String[] damFileUpdate = typeAheadSearchService.getFilenameUpdate();
			String[] damFileRemove = typeAheadSearchService.getFilenameRemove();

			QueryManager queryManager = session.getWorkspace().getQueryManager();

			QueryResult result = getSearchResult(queryManager, searchPath, "keywords");

			int count = 0;
			LinkedHashSet<String> allResultKeys = new LinkedHashSet<String>();
			LinkedHashSet<String> locationResultKeys = new LinkedHashSet<String>();
			LinkedHashSet<String> peopleResultKeys = new LinkedHashSet<String>();
			LinkedHashSet<String> resourceResultKeys = new LinkedHashSet<String>();
			LinkedHashSet<String> faqResultKeys = new LinkedHashSet<String>();
			LinkedHashSet<String> productResultKeys = new LinkedHashSet<String>();

			JSONObject keywordsJson = new JSONObject();

			JSONArray allResultsJsonObject = new JSONArray();
			JSONArray locationJsonObject = new JSONArray();
			JSONArray faqJsonObject = new JSONArray();
			JSONArray peopleJsonObject = new JSONArray();
			JSONArray resourceCenterJsonObject = new JSONArray();
			JSONArray productCenterJsonObject = new JSONArray();

			NodeIterator nodeIter = result.getNodes();
			StringBuilder keywords = new StringBuilder();
			while (nodeIter.hasNext()) {

				Node node = nodeIter.nextNode();

				Page page = pageManager.getPage(node.getPath());

				ValueMap props = page.getProperties();

				keywords.setLength(0);

				if (props.containsKey("keywords")) {
					keywords.append(props.get("keywords").toString());
					addUniqueKey(keywords.toString(), allResultKeys);
					/*LOGGER.info("keywords[" + count + "]::" + keywords);*/
					
					if (page.getProperties().containsKey("cq:template")) {
						if ((page.getProperties().get("cq:template").toString()).contains(LOCATION_TEMPLATE)) {
							addUniqueKey(keywords.toString(), locationResultKeys);
						}
						if ((page.getProperties().get("cq:template").toString()).contains(PEOPLE_TEMPLATE)) {
							addUniqueKey(keywords.toString(), peopleResultKeys);
						}
						if (RC_CALCULATOR.equals(page.getProperties().get("cq:template").toString()) ||
								RESOURCE_CENTER_TEMPLATE.equals(page.getProperties().get("cq:template").toString()) || 
								RC_ARTICLE.equals(page.getProperties().get("cq:template").toString()) ||
								RC_AUDIO.equals(page.getProperties().get("cq:template").toString()) ||
								RC_VIDEO.equals(page.getProperties().get("cq:template").toString())) {
							addUniqueKey(keywords.toString(), resourceResultKeys);
						}
						if ((page.getProperties().get("cq:template").toString()).contains(FAQ_TEMPLATE)) {
							addUniqueKey(keywords.toString(), faqResultKeys);
						}
						if (PRODUCT_TEMPLATE.equals(page.getProperties().get("cq:template").toString())) {
							addUniqueKey(keywords.toString(), productResultKeys);
						}
					}

				}
				count++;
			}

			LOGGER.info("Total result count: " + count);
			LOGGER.info("dictFilePath: " + dictFilePath);

			if (StringUtils.isNotBlank(dictFilePath)) {
				Resource dictFile = resourceResolver.getResource(dictFilePath);
				if (null != dictFile) {

					LOGGER.info("dictFile: " + dictFile.getPath());

					if (damFileUpdate.length > 0) {

						Arrays.asList(damFileUpdate).stream().forEach(file -> {
							String[] fileCategorySplitter = file.split(":");
							LOGGER.info("Adding keywords read from dam file");

							if (fileCategorySplitter[0].equalsIgnoreCase("allresults")) {
								addToSet(dictFile, fileCategorySplitter[1], allResultKeys);
							}
							if (fileCategorySplitter[0].equalsIgnoreCase("location")) {
								addToSet(dictFile, fileCategorySplitter[1], locationResultKeys);
								addToSet(dictFile, fileCategorySplitter[1], allResultKeys);
							}
							if (fileCategorySplitter[0].equalsIgnoreCase("people")) {
								addToSet(dictFile, fileCategorySplitter[1], peopleResultKeys);
								addToSet(dictFile, fileCategorySplitter[1], allResultKeys);
							}
							if (fileCategorySplitter[0].equalsIgnoreCase("faq")) {
								addToSet(dictFile, fileCategorySplitter[1], faqResultKeys);
								addToSet(dictFile, fileCategorySplitter[1], allResultKeys);
							}
							if (fileCategorySplitter[0].equalsIgnoreCase("resource")) {
								addToSet(dictFile, fileCategorySplitter[1], resourceResultKeys);
								addToSet(dictFile, fileCategorySplitter[1], allResultKeys);
							}
							if (fileCategorySplitter[0].equalsIgnoreCase("product")) {
								addToSet(dictFile, fileCategorySplitter[1], productResultKeys);
								addToSet(dictFile, fileCategorySplitter[1], allResultKeys);
							}
						});
					}

					if (damFileRemove.length > 0) {

						Arrays.asList(damFileRemove).stream().forEach(file -> {
							String[] fileCategorySplitter = file.split(":");
							LOGGER.info("Removing keywords read from dam file");

							if (fileCategorySplitter[0].equalsIgnoreCase("allresults")) {
								removeFromSet(dictFile, fileCategorySplitter[1], allResultKeys);
							}
							if (fileCategorySplitter[0].equalsIgnoreCase("location")) {
								removeFromSet(dictFile, fileCategorySplitter[1], locationResultKeys);
								removeFromSet(dictFile, fileCategorySplitter[1], allResultKeys);
							}
							if (fileCategorySplitter[0].equalsIgnoreCase("people")) {
								removeFromSet(dictFile, fileCategorySplitter[1], peopleResultKeys);
								removeFromSet(dictFile, fileCategorySplitter[1], allResultKeys);
							}
							if (fileCategorySplitter[0].equalsIgnoreCase("faq")) {
								removeFromSet(dictFile, fileCategorySplitter[1], faqResultKeys);
								removeFromSet(dictFile, fileCategorySplitter[1], allResultKeys);
							}
							if (fileCategorySplitter[0].equalsIgnoreCase("resource")) {
								removeFromSet(dictFile, fileCategorySplitter[1], resourceResultKeys);
								removeFromSet(dictFile, fileCategorySplitter[1], allResultKeys);
							}
							if (fileCategorySplitter[0].equalsIgnoreCase("product")) {
								removeFromSet(dictFile, fileCategorySplitter[1], productResultKeys);
								removeFromSet(dictFile, fileCategorySplitter[1], allResultKeys);
							}
						});
					}
				}
			}

			allResultsJsonObject.put(allResultKeys.stream().distinct().collect(Collectors.joining(",")));
			locationJsonObject.put(locationResultKeys.stream().distinct().collect(Collectors.joining(",")));
			faqJsonObject.put(faqResultKeys.stream().distinct().collect(Collectors.joining(",")));
			peopleJsonObject.put(peopleResultKeys.stream().distinct().collect(Collectors.joining(",")));
			resourceCenterJsonObject.put(resourceResultKeys.stream().distinct().collect(Collectors.joining(",")));
			productCenterJsonObject.put(productResultKeys.stream().distinct().collect(Collectors.joining(",")));

			keywordsJson.put("all_results", allResultsJsonObject);
			keywordsJson.put("location_results", locationJsonObject);
			keywordsJson.put("people_results", peopleJsonObject);
			keywordsJson.put("faq_results", faqJsonObject);
			keywordsJson.put("resource_results", resourceCenterJsonObject);
			keywordsJson.put("product_results", productCenterJsonObject);

			response.getWriter().write(keywordsJson.toString(4));

		} catch (Exception e) {
			LOGGER.error("Exception::", e);
		}
	}

	/**
	 * Returns the script file content
	 *
	 * @return scriptValue
	 */
	protected List<String> getScriptContent(Resource dictFile) {
		try {
			if (dictFile != null) {
				Asset asset = dictFile.adaptTo(Asset.class);
				Rendition rendition = asset.getOriginal();
				InputStream inputStream = rendition.getStream();
				StringWriter writer = new StringWriter();
				IOUtils.copy(inputStream, writer, "UTF-8");
				List<String> lines = new BufferedReader(new StringReader(writer.toString())).lines().distinct()
						.collect(Collectors.toList());
				lines.replaceAll(String::trim);

				return lines;
			}
		} catch (Exception e) {
			LOGGER.error("Error reading script value", e.getMessage(), e);
		}
		return null;
	}

	protected void addUniqueKey(String keywords, LinkedHashSet<String> resultKeys) {
		if(StringUtils.isNotBlank(keywords)) {
			if(StringUtils.contains(keywords, ",")) {
				String[] keywordsAll = keywords.split(",");
				for(String keyword : keywordsAll) {
					resultKeys.add(keyword.trim());
				}
			} else {
				resultKeys.add(keywords.trim());
			}
		}
	}
	
	protected void addToSet(Resource dictFile, String fileName, LinkedHashSet<String> resultKeys) {

		if (null != dictFile.getChild(fileName)) {
			resultKeys.addAll(getScriptContent(dictFile.getChild(fileName)));
		}
	}

	protected void removeFromSet(Resource dictFile, String fileName, LinkedHashSet<String> resultKeys) {

		if (null != dictFile.getChild(fileName)) {
			resultKeys.removeAll(getScriptContent(dictFile.getChild(fileName)));
		}
	}

	protected QueryResult getSearchResult(QueryManager queryManager, String searchPath, String constraint)
			throws InvalidQueryException, RepositoryException {

		final StringBuilder queryString = new StringBuilder("SELECT * FROM [cq:Page] AS s WHERE ISDESCENDANTNODE(s, '"
				+ searchPath + "') AND s.[jcr:content/" + constraint + "] IS NOT NULL");

		Query query = queryManager.createQuery(queryString.toString(), "JCR-SQL2");
		QueryResult result = query.execute();

		LOGGER.info("queryString" + queryString.toString());

		return result;

	}

}