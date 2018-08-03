package com.suntrust.dotcom.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.suntrust.dotcom.beans.DynamicListItemsBean;
import com.suntrust.dotcom.beans.DynamicListMultiBean;
import com.suntrust.dotcom.services.DynamicListService;
import com.suntrust.dotcom.services.PageThumbnailService;
import com.suntrust.dotcom.utils.RecentPagesSort;
import com.suntrust.dotcom.utils.TitleAscendingOrderSort;
import com.suntrust.dotcom.utils.TitleDescendingOrderSort;
import com.suntrust.dotcom.utils.Utils;

/**
 * This DynamicListComponent is used render dynamiclist component.
 * 
 * @author Cognizant
 * @version 1.0
 * @since 10 July 2017
 * 
 */
@Component
@Service
public class DynamicListComponent extends BaseUsePojo {
	/** Logger variable to log program state * */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DynamicListComponent.class);
	/** To hold each tab data as list * */
	private List<DynamicListItemsBean> pinnedBeanList = new ArrayList<DynamicListItemsBean>();
	/** To hold each tab data as list * */
	private final List<DynamicListItemsBean> unPinnedItemsBeanListRaw = new ArrayList<DynamicListItemsBean>();
	/** To hold each tab data as list * */
	private List<DynamicListItemsBean> lBeanList = new ArrayList<DynamicListItemsBean>();
	/** To hold final object as list* */
	private final List<DynamicListMultiBean> multiList = new ArrayList();
	/** To hold result dynamicPageUrlsList as list * */
	private List<String> dynamicPageUrlsList = new ArrayList<String>();
	/** To hold result dynamicPageUrlsSet as list * */
	private Set<String> dynamicPageUrlsSet = new LinkedHashSet<String>();
	/** To hold result staticPageUrlsList as list * */
	private List<String> staticPageUrlsList = new ArrayList<String>();
	/** To hold result pageUrlsListToBedisplayed as list * */
	private final List<String> pageUrlsListToBedisplayed = new ArrayList<String>();
	/** To hold result pageUrlsListToBedisplayed as list * */
	private final List<String> unPinnedPageUrlsList = new ArrayList<String>();
	/** To hold result pageUrls as set * */
	private final Set<String> pageUrlsSet = new LinkedHashSet<String>();
	/** Service variable to access PageThumbnailService service* */
	private PageThumbnailService pageThumbnailService = null;
	/** To hold result limit * */
	private int resultLimit = 0;
	/** To hold authorTagsArray * */
	private final List<String> authorTagsArray = new ArrayList<String>();
	/** To hold authorLogicArray * */
	private final List<String> authorLogicArray = new ArrayList<String>();
	/** To hold isDynamic flag * */
	private String isdynamic = "false";
	/** To hold style value * */
	private String style = "summarylist";
	/** Constant variable to store LINKURL * */
	public static final String LINKURL = "linkURL";
	/** Constant variable to store DUPLICATE * */
	public static final String DUPLICATE = "duplicate";
	/** Constant variable to store SUNTRUSTARTICLE * */
	public static final String SUNTRUSTARTICLE = "suntrust-article";
	/** Constant variable to store HTMLEXTENSION * */
	public static final String HTMLEXTENSION = ".html";
	/** Constant variable to store JCRCONTENTPATH * */
	public static final String JCRCONTENTPATH = "/jcr:content";
	/** Constant variable to store DOTCOMREADSERVICE * */
	public static final String DOTCOMREADSERVICENAME = "dotcomreadservice";
	/** Constant variable to store PAGEURLSINREQUEST * */
	public static final String PAGEURLSINREQUEST = "pageUrlsInRequest";
	/** Constant variable to store PRIMARYTAG * */
	public static final String PRIMARYTAG = "primarytag";
	/** Constant variable to store CONTENTTYPE * */
	public static final String CONTENTTYPE = "contenttype";

	/**
	 * To handle component submit
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void activate() {
		List<DynamicListItemsBean> unPinnedBeanListRaw = new ArrayList<DynamicListItemsBean>();
		List<DynamicListItemsBean> pinnedBeanListRaw = new ArrayList<DynamicListItemsBean>();
		Boolean poolTags;
		String resultCount = null;
		String sortByValue = get("sortby", String.class);
		String contextUrl = get("contextPath", String.class);
		resultCount = get("resultCount", String.class);
		poolTags = getProperties().get("poolTags", false);
		if (StringUtils.isNotBlank(resultCount))
			resultLimit = Integer.parseInt(resultCount);
		String criteriaForRule = get("criteriaForRule", String.class);
		Node currentNode = (Node) getResource().adaptTo(Node.class);
		try {
			if (currentNode.hasProperty("isdynamic"))
				isdynamic = currentNode.getProperty("isdynamic").getString();

			if (currentNode.hasProperty("style"))
				style = currentNode.getProperty("style").getString();

			DynamicListService dynamicListService = getSlingScriptHelper()
					.getService(DynamicListService.class);
			pageThumbnailService = getSlingScriptHelper().getService(
					PageThumbnailService.class);
			DynamicListMultiBean mBean = new DynamicListMultiBean();
			pinnedBeanListRaw = populateBeanList("iItems",
					DynamicListItemsBean.class);
			unPinnedBeanListRaw = populateBeanList("uItems",
					DynamicListItemsBean.class);
			for (DynamicListItemsBean pinnedBeanList : pinnedBeanListRaw) {
				staticPageUrlsList.add(pinnedBeanList.getLinkURL());
			}
			if (getRequest().getAttribute(PAGEURLSINREQUEST) != null
					&& !staticPageUrlsList.isEmpty()) {
				staticPageUrlsList = ListUtils.subtract(
						staticPageUrlsList,
						(List<String>) getRequest().getAttribute(
								PAGEURLSINREQUEST));
				for (String staticPageUrl : staticPageUrlsList) {
					for (DynamicListItemsBean dynamicListItemsBean : pinnedBeanListRaw) {
						if (dynamicListItemsBean.getLinkURL().equalsIgnoreCase(
								staticPageUrl))
							pinnedBeanList.add(dynamicListItemsBean);
					}
				}
			} else {
				pinnedBeanList = pinnedBeanListRaw;
			}

			if (staticPageUrlsList.isEmpty() && !pinnedBeanList.isEmpty())
				pinnedBeanList.clear();

			if (unPinnedBeanListRaw.isEmpty()) {
				if ("true".equalsIgnoreCase(isdynamic)) {
					String articletag = (String) getRequest().getAttribute(
							"articleTag");
					if (StringUtils.isNotBlank(articletag))
						dynamicPageUrlsList.addAll(dynamicListService
								.getResultsFromQuery(contextUrl, articletag,
										"and", criteriaForRule,
										getCurrentPagePath()));
					else
						dynamicPageUrlsList.addAll(dynamicListService
								.getResultsFromQuery(contextUrl, null, null,
										criteriaForRule, getCurrentPagePath()));
				} else
					dynamicPageUrlsList.addAll(dynamicListService
							.getResultsFromQuery(contextUrl, null, null,
									criteriaForRule, getCurrentPagePath()));

			} else {
				for (int i = 0; i < unPinnedBeanListRaw.size(); i++) {
					String authorTagsTemp = null;
					DynamicListItemsBean dynamicListItemsBean = unPinnedBeanListRaw
							.get(i);
					if (StringUtils.isNotBlank(dynamicListItemsBean
							.getAuthorTags())
							&& StringUtils.isNotBlank(dynamicListItemsBean
									.getAuthorLogic())) {

						if (dynamicListItemsBean.getAuthorTags()
								.equalsIgnoreCase("String[]"))
							authorTagsTemp = null;
						else
							authorTagsTemp = dynamicListItemsBean
									.getAuthorTags();
						if ("true".equalsIgnoreCase(isdynamic)) {
							String articletag = (String) getRequest()
									.getAttribute("articleTag");
							if (StringUtils.isNotBlank(articletag)) {
								if (StringUtils.isNotBlank(authorTagsTemp))
									authorTagsTemp = authorTagsTemp + ","
											+ articletag;
								else
									authorTagsTemp = articletag;
							}
						}
						dynamicListItemsBean.setAuthorTags(authorTagsTemp);
						authorTagsArray.add(authorTagsTemp);
						authorLogicArray.add(dynamicListItemsBean
								.getAuthorLogic());
						if (poolTags == null || !poolTags) {
							dynamicPageUrlsList.addAll(dynamicListService
									.getResultsFromQuery(contextUrl,
											authorTagsTemp,
											dynamicListItemsBean
													.getAuthorLogic(),
											criteriaForRule,
											getCurrentPagePath()));
						} else {
							if (i == unPinnedBeanListRaw.size() - 1) {
								dynamicPageUrlsList.addAll(dynamicListService
										.getResultsFromQueryForPooledTags(
												contextUrl, authorTagsArray,
												authorLogicArray,
												criteriaForRule,
												getCurrentPagePath()));
							}
						}
					}
				}

			}
			if (!dynamicPageUrlsList.isEmpty()) {
				dynamicPageUrlsSet.addAll(dynamicPageUrlsList);
				dynamicPageUrlsList.clear();
				dynamicPageUrlsList.addAll(dynamicPageUrlsSet);
			}

			dynamicPageUrlsList = ListUtils.subtract(dynamicPageUrlsList,
					staticPageUrlsList);
			if (getRequest().getAttribute(PAGEURLSINREQUEST) != null
					&& !dynamicPageUrlsList.isEmpty()) {
				dynamicPageUrlsList = ListUtils.subtract(
						dynamicPageUrlsList,
						(List<String>) getRequest().getAttribute(
								PAGEURLSINREQUEST));
			}
			if (!dynamicPageUrlsList.isEmpty()) {
				for (int i = 0; i < dynamicPageUrlsList.size(); i++) {
					DynamicListItemsBean dynamicListItemsBean = new DynamicListItemsBean();
					String pageUrl = dynamicPageUrlsList.get(i);
					if (StringUtils.isNotBlank(pageUrl)) {
						dynamicListItemsBean.setLinkURL(pageUrl);
						dynamicListItemsBean.setValueType("UnPinnedItem");
						unPinnedItemsBeanListRaw.add(dynamicListItemsBean);
					}
				}
			}
			lBeanList.addAll(setValues(pinnedBeanList));
			lBeanList.addAll(setValues(unPinnedItemsBeanListRaw));
			lBeanList = sortBeanList(lBeanList, sortByValue);
			if (!lBeanList.isEmpty()) {
				mBean.setItems(lBeanList);
				this.multiList.add(mBean);
			}
		} catch (RepositoryException | IllegalStateException | IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * To sort the bean list
	 * 
	 * @param BeanList
	 * @param sortByValue
	 * @return sortedBeanList
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<DynamicListItemsBean> sortBeanList(
			List<DynamicListItemsBean> beanList, String sortByValue) {
		List<DynamicListItemsBean> sortedBeanList = new ArrayList<DynamicListItemsBean>();
		sortedBeanList = beanList;
		if (StringUtils.isNotBlank(sortByValue)) {
			if ("recent".equals(sortByValue)) {
				RecentPagesSort sortRecentlyPublished = new RecentPagesSort();
				Collections.sort(sortedBeanList, sortRecentlyPublished);
			} else if ("a-z".equals(sortByValue)) {
				TitleAscendingOrderSort sortAscending = new TitleAscendingOrderSort();
				Collections.sort(sortedBeanList, sortAscending);
			} else if ("z-a".equals(sortByValue)) {
				TitleDescendingOrderSort sortDecending = new TitleDescendingOrderSort();
				Collections.sort(sortedBeanList, sortDecending);
			} else if ("random".equals(sortByValue)) {
				List<DynamicListItemsBean> listOfPinnedItems = new ArrayList();
				List<DynamicListItemsBean> listOfUnPinnedItems = new ArrayList();
				for (DynamicListItemsBean dynamicListItemsBean : sortedBeanList) {
					if ("PinnedItem".equalsIgnoreCase(dynamicListItemsBean
							.getValueType()))
						listOfPinnedItems.add(dynamicListItemsBean);
					else
						listOfUnPinnedItems.add(dynamicListItemsBean);
				}
				Collections.shuffle(listOfUnPinnedItems);
				listOfPinnedItems.addAll(listOfUnPinnedItems);
				sortedBeanList.clear();
				sortedBeanList.addAll(listOfPinnedItems);
			}
		}
		return sortedBeanList;
	}

	/**
	 * To Set values to Bean
	 * 
	 * @param BeanList
	 * @return populatedBeanList
	 */
	@SuppressWarnings("unchecked")
	public List<DynamicListItemsBean> setValues(
			List<DynamicListItemsBean> beanList) {
		List<DynamicListItemsBean> populatedBeanList = new ArrayList<DynamicListItemsBean>();
		String fullUrl = null;
		Resource resource = null;
		ResourceResolver resourceResolver = null;
		try {
			resourceResolver = getRequest().getResourceResolver();
			for (DynamicListItemsBean dynamicListItemsBean : beanList) {
				String pageUrlTemp = null;
				if (StringUtils.isNotBlank(dynamicListItemsBean.getLinkURL())
						&& !(dynamicListItemsBean.getLinkURL() + HTMLEXTENSION)
								.equalsIgnoreCase(getRequest()
										.getRequestPathInfo().getSuffix())) {
					String pageUrl = dynamicListItemsBean.getLinkURL().trim();
					fullUrl = pageUrl + JCRCONTENTPATH;
					resource = resourceResolver.getResource(fullUrl);
					if (resource != null) {
						ValueMap properties = resource.getValueMap();
						dynamicListItemsBean.setReplicationDate(properties.get(
								"cq:lastModified", Date.class));
						dynamicListItemsBean
								.setThumbnailPath(pageThumbnailService
										.getPageThumbnail(dynamicListItemsBean
												.getLinkURL().trim()));
						dynamicListItemsBean.setPageDescription(properties.get(
								"jcr:description", String.class));
						dynamicListItemsBean.setPageTitle(properties.get(
								"jcr:title", String.class));
						dynamicListItemsBean.setTags(getTagsList(properties,
								resourceResolver));
					}
					if (StringUtils
							.isBlank(dynamicListItemsBean.getValueType()))
						dynamicListItemsBean.setValueType("PinnedItem");
					pageUrlTemp = getArticleUrl(dynamicListItemsBean
							.getLinkURL());
					if (!pageUrl.contains("wrapper")) {
						if (containsContentTypeTag(pageUrl,
								dynamicListItemsBean)) {
							String wrapperurl = getWrapperUrl(
									dynamicListItemsBean.getAuthorTags(),
									pageUrl);
							if (StringUtils.isNotBlank(wrapperurl)) {
								pageUrlTemp = wrapperurl + HTMLEXTENSION
										+ pageUrl + HTMLEXTENSION;
							} else
								pageUrlTemp = pageUrl + HTMLEXTENSION;
						} else {
							pageUrlTemp = Utils.urlCheck(pageUrl);
							dynamicListItemsBean
									.setThumbnailIconClassName(SUNTRUSTARTICLE);
						}
					}
					dynamicListItemsBean.setLinkURL(Utils.getModifyURL(
							pageUrlTemp, dynamicListItemsBean.getUrlParams(),
							dynamicListItemsBean.getAnchorTag()));
					if ("thumbnail".equalsIgnoreCase(style)) {
						if (StringUtils.isNotBlank(dynamicListItemsBean
								.getThumbnailPath())) {
							if (dynamicListItemsBean.getValueType()
									.equalsIgnoreCase("UnPinnedItem")) {
								if (unPinnedPageUrlsList.size() < resultLimit) {
									pageUrlsListToBedisplayed.add(pageUrl);
									unPinnedPageUrlsList.add(pageUrl);
									populatedBeanList.add(dynamicListItemsBean);
								} else {
									break;
								}
							} else if (dynamicListItemsBean.getValueType()
									.equalsIgnoreCase("PinnedItem")) {
								pageUrlsListToBedisplayed.add(pageUrl);
								populatedBeanList.add(dynamicListItemsBean);
							}
						}
					} else {
						if (dynamicListItemsBean.getValueType()
								.equalsIgnoreCase("UnPinnedItem")) {
							if (unPinnedPageUrlsList.size() < resultLimit) {
								pageUrlsListToBedisplayed.add(pageUrl);
								unPinnedPageUrlsList.add(pageUrl);
								populatedBeanList.add(dynamicListItemsBean);
							} else {
								break;
							}
						} else if (dynamicListItemsBean.getValueType()
								.equalsIgnoreCase("PinnedItem")) {
							pageUrlsListToBedisplayed.add(pageUrl);
							populatedBeanList.add(dynamicListItemsBean);
						}
					}
				}
			}
			if (getRequest().getAttribute(PAGEURLSINREQUEST) != null) {
				pageUrlsSet.addAll(ListUtils.union(
						pageUrlsListToBedisplayed,
						(List<String>) getRequest().getAttribute(
								PAGEURLSINREQUEST)));
				pageUrlsListToBedisplayed.clear();
				pageUrlsListToBedisplayed.addAll(pageUrlsSet);
				getRequest().setAttribute(PAGEURLSINREQUEST,
						pageUrlsListToBedisplayed);
			} else {
				getRequest().setAttribute(PAGEURLSINREQUEST,
						pageUrlsListToBedisplayed);
			}
		} catch (RepositoryException e) {
			LOGGER.error("Exception in setValues" + e);
		}
		return populatedBeanList;
	}

	/**
	 * To get the tags list
	 * 
	 * @param properties
	 * @param resourceResolver
	 * @return tagList
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> getTagsList(ValueMap properties,
			ResourceResolver resourceResolver) {
		String tagsValues[] = null;
		List<String> tagList = new ArrayList();
		HashSet<String> tagSet = new HashSet<>();
		TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
		if (properties.containsKey(PRIMARYTAG)) {
			tagsValues = properties.get(PRIMARYTAG, String[].class);
			setTagsList(tagsValues, tagSet, tagManager);
		}
		if (properties.containsKey(CONTENTTYPE)) {
			tagsValues = properties.get(CONTENTTYPE, String[].class);
			setTagsList(tagsValues, tagSet, tagManager);
		}
		if (properties.containsKey("cq:tags")) {
			tagsValues = properties.get("cq:tags", String[].class);

			setTagsList(tagsValues, tagSet, tagManager);
		}
		tagList.addAll(tagSet);
		return tagList;
	}

	/**
	 * To set the tags list
	 * 
	 * @param tagsValues
	 * @param tagList
	 * @param tagManager
	 * 
	 */
	private void setTagsList(String[] tagsValues, HashSet<String> tagList,
			TagManager tagManager) {
		if (tagsValues != null) {
			for (String tagValue : tagsValues) {
				String tagID = tagValue;
				Tag tag = tagManager.resolve(tagID);
				if (tag != null) {
					tagList.add(tag.getTitle());
				}
			}
		}
	}

	/**
	 * To get CurrentPagePath
	 * 
	 * @return suffix
	 */
	public String getCurrentPagePath() {
		String suffix = getRequest().getRequestPathInfo().getSuffix();
		if (StringUtils.isBlank(suffix)) {
			return getCurrentPage().getPath();
		}
		return suffix;
	}

	/**
	 * To get article url
	 * 
	 * @param pageUrl
	 * @return pageUrlNew
	 */
	private String getArticleUrl(String pageUrl) {
		String pageUrlNew = pageUrl;
		if (pageUrl.contains("wrapper.html")) {
			int index = pageUrlNew.indexOf("wrapper.html");
			pageUrlNew = pageUrl.substring(index + 12);
		}
		if (!pageUrlNew.endsWith(HTMLEXTENSION))
			pageUrlNew = pageUrlNew + HTMLEXTENSION;
		return pageUrlNew;
	}

	/**
	 * To get wrapper url
	 * 
	 * @param dynamiclisttag
	 * @param pageUrl
	 * @return getResourceResolver
	 */
	private String getWrapperUrl(String dynamiclisttag, String pageUrl) {

		Resource res = getResourceResolver().getResource(pageUrl);
		// find pages by tags
		if (res != null) {
			Page resPage = res.adaptTo(Page.class);
			if (resPage != null) {
				String primaryArticleTag = resPage.getProperties().containsKey(
						PRIMARYTAG) ? (String) resPage.getProperties().get(
						PRIMARYTAG) : "";
				if (StringUtils.isNotBlank(dynamiclisttag)) {
					boolean checkIfPrimaryMatch = checkIfTagMatchesDynamicListRule(
							primaryArticleTag, dynamiclisttag);
					if (checkIfPrimaryMatch)
						return Utils.getWrapperPath(primaryArticleTag,
								getResourceResolver());
					else {
						// check if secondary tags are going to match
						Tag[] cqTags = resPage.getTags();
						for (int len = 0; len < cqTags.length; len++) {
							Tag secTag = cqTags[len];
							boolean checkIfSecondaryMatch = checkIfTagMatchesDynamicListRule(
									secTag.getTagID(), dynamiclisttag);
							if (checkIfSecondaryMatch)
								return Utils.getWrapperPath(secTag.getTagID(),
										getResourceResolver());
						}
					}
				}
				// no tag rules set in dynamic list rule
				else
					return Utils.getWrapperPath(primaryArticleTag,
							getResourceResolver());
			}
		}
		return "";
	}

	/**
	 * To check if tag matches dynamic list rules
	 * 
	 * @param articleTagId
	 * @param dynamicListRule
	 * @return true/false
	 */
	private boolean checkIfTagMatchesDynamicListRule(String articleTagId,
			String dynamicListRule) {
		if (StringUtils.isNotBlank(dynamicListRule)) {
			String[] tagList = dynamicListRule.split(",");
			for (int i = 0; i < tagList.length; i++) {
				String tag = tagList[i];
				// check if primary tag matches
				if (articleTagId.contains(tag))
					return true;
			}
		}
		return false;
	}

	/**
	 * To set content Type Tag
	 * 
	 * @param pageUrl
	 * @param iBean
	 * @return true / false
	 */
	private boolean containsContentTypeTag(String pageUrl,
			DynamicListItemsBean iBean) {
		Resource res = getResourceResolver().getResource(pageUrl);
		if (res != null) {
			Page resPage = res.adaptTo(Page.class);
			String contenttype = resPage.getProperties().containsKey(
					CONTENTTYPE) ? (String) resPage.getProperties().get(
					CONTENTTYPE) : "";
			if (StringUtils.isNotBlank(contenttype)) {
				if (StringUtils.lowerCase(contenttype).contains("article"))
					iBean.setThumbnailIconClassName(SUNTRUSTARTICLE);
				else if (StringUtils.lowerCase(contenttype).contains(
						"calculator"))
					iBean.setThumbnailIconClassName("suntrust-calculator");
				else if (StringUtils.lowerCase(contenttype).contains("video"))
					iBean.setThumbnailIconClassName("suntrust-video");
				else if (StringUtils.lowerCase(contenttype).contains("audio"))
					iBean.setThumbnailIconClassName("suntrust-podcast");
				else
					iBean.setThumbnailIconClassName(SUNTRUSTARTICLE);
				return true;
			} else
				iBean.setThumbnailIconClassName(SUNTRUSTARTICLE);
			return false;
		}
		return false;
	}

	/**
	 * @return list
	 */
	public List<DynamicListMultiBean> getMBean() {
		return this.multiList;
	}

}
