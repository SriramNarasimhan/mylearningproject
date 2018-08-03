package com.suntrust.dotcom.workflow;

import static org.jsoup.helper.StringUtil.isBlank;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.Page;
import com.suntrust.dotcom.config.ContentExpiryConfigService;
import com.suntrust.dotcom.services.SearchService;

/**
 * Content expiry page finder process
 */
@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Content expiry page finder process"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Content expiry page finder process") })
public class ContentExpiryFinderProcess implements WorkflowProcess {

	private static final String ASSATATION_OWNER_PROPERTY = "@jcr:content/attestationowner";

	private static final String ASSATATION_DATE_PROPERTY = "@jcr:content/attestationdate";

	private static final String NO_OF_WEEK_ADVANCE = "noOfWeekAdvance";
	private static final String ATTESTATION_DAY = "attestationDays";
	private static final String NO_OF_WEEK_DUE = "noOfWeekDue";
	private static final String NO_OF_WEEK_PAST = "noOfWeekPast";
	private static final String NO_OF_WEEK_OVERDUE = "noOfWeekOverdue";

	private static final String ATTESTATION_OWNER = "attestationowner";
	private static final String ATTESTATION_REVIEWER = "@jcr:content/attestationReviewer";

	private static final String SERVICE = "dotcomreadservice";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ContentExpiryFinderProcess.class);

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private SearchService searchService;

	@Reference
	private ContentExpiryConfigService configService;

	private ResourceResolver resourceResolver;

	private int rootPathList;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap metadataMap) throws WorkflowException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, SERVICE);
		try {
			resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);
			updatePageOwner(resourceResolver, workItem, workflowSession);
		} catch (LoginException e) {
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
		} finally {

			if (resourceResolver != null) {
				resourceResolver.close();
			}
		}
	}

	private String getSearchDate(ZonedDateTime searchDate) {
		try {

			return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(searchDate);
		} catch (Exception e) {
			LOGGER.error(
					" Date Parse Exception occurred {} TRACE: {}"
							+ e.getMessage(), e);
			return null;
		}
	}

	private void addToList(List<Resource> commonList, ZonedDateTime searchDate,
			String siteName) {
		if (searchDate != null) {
			List<Resource> resourceList = getResults(getPageExpiryPredicate(
					getSearchDate(searchDate), getPropertyList(siteName)));
			if (resourceList != null && !resourceList.isEmpty()) {
				commonList.addAll(resourceList);
			}
		}
	}

	private ZonedDateTime getOwnerDate(ZonedDateTime currentDate,
			String noOfWeekAdvance) {

		if (StringUtils.isNumeric(noOfWeekAdvance)) {
			return currentDate.plusDays(Integer.parseInt(noOfWeekAdvance) * 7);
		}
		return null;

	}

	private ZonedDateTime getReviewerDate(ZonedDateTime currentDate,
			String noOfWeekDue) {

		if (StringUtils.isNumeric(noOfWeekDue)) {
			return currentDate.minusDays(Integer.parseInt(noOfWeekDue) * 7);
		}
		return null;

	}

	/**
	 * Fetch all attested page details
	 * 
	 * @param resourceResolver
	 * @param workItem
	 * @param workflowSession
	 */
	private void updatePageOwner(ResourceResolver resolver, WorkItem workItem,
			WorkflowSession workflowSession) {
		try {
			ZonedDateTime currentDate = ZonedDateTime.now();
			List<Resource> noOfWeekAdvance = new ArrayList<Resource>();
			List<Resource> attestationDays = new ArrayList<Resource>();
			List<Resource> noOfWeekDue = new ArrayList<Resource>();
			List<Resource> noOfWeekPast = new ArrayList<Resource>();
			List<Resource> noOfWeekOverdue = new ArrayList<Resource>();

			List<String> siteList = configService
					.getPropertyArray("sites.list");
			siteList.forEach(siteName -> {
				addToList(
						noOfWeekAdvance,
						getOwnerDate(currentDate, getValue(NO_OF_WEEK_ADVANCE)),
						siteName);
				addToList(noOfWeekDue,
						getReviewerDate(currentDate, getValue(NO_OF_WEEK_DUE)),
						siteName);

				addToList(
						noOfWeekPast,
						getReviewerDate(currentDate, getValue(NO_OF_WEEK_PAST)),
						siteName);

				addToList(
						noOfWeekOverdue,
						getReviewerDate(currentDate,
								getValue(NO_OF_WEEK_OVERDUE)), siteName);
				addToList(attestationDays, getOwnerDate(currentDate, "0"),
						siteName);

			});

			if (noOfWeekAdvance.isEmpty() && attestationDays.isEmpty()
					&& noOfWeekDue.isEmpty() && noOfWeekPast.isEmpty()
					&& noOfWeekOverdue.isEmpty()) {
				LOGGER.debug("There is no pending request to process:>AttestationSchedulerProcess.updatePageOwner()");

				workflowSession.terminateWorkflow(workItem.getWorkflow());

			} else {

				updatePage(workItem, noOfWeekAdvance, NO_OF_WEEK_ADVANCE);
				updatePage(workItem, attestationDays, ATTESTATION_DAY);
				updatePage(workItem, noOfWeekDue, NO_OF_WEEK_DUE);
				updatePage(workItem, noOfWeekPast, NO_OF_WEEK_PAST);
				updatePage(workItem, noOfWeekOverdue, NO_OF_WEEK_OVERDUE);

			}
		} catch (WorkflowException e) {
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
		} catch (NumberFormatException e) {
			LOGGER.error(
					"Number format Exception occurred {} TRACE: {}"
							+ e.getMessage(), e);
		}

	}

	private void updatePage(WorkItem workItem, List<Resource> pageResultsMap,
			String type) {
		if (pageResultsMap != null) {
			Map<String, Set<String>> genericMap = new HashMap<String, Set<String>>();
			pageResultsMap.stream().forEach(
					resourceObject -> {
						Page page = resourceObject.adaptTo(Page.class);
						if (page != null) {
							String attestationowner = page.getProperties().get(
									ATTESTATION_OWNER, String.class);

							if (genericMap.containsKey(attestationowner)) {
								genericMap.get(attestationowner).add(
										page.getPath());
							} else {
								Set<String> listOfPath = new HashSet<String>();
								listOfPath.add(page.getPath());
								genericMap.put(attestationowner, listOfPath);
							}

						}

					});
			if (!genericMap.isEmpty()) {
				workItem.getWorkflowData().getMetaDataMap()
						.put(type, genericMap);
			}
		}

	}

	/**
	 * Expiry page predicate
	 * 
	 * @param currentDate
	 * @param rootPath
	 * @return map
	 */
	private Map<String, String> getPageExpiryPredicate(String currentDate,
			List<String> rootPath) {
		Map<String, String> expiryMap = new HashMap<String, String>();
		if (isEmptyPath(rootPath)) {
			return expiryMap;
		}
		rootPathList = rootPath.size();
		rootPath.stream()
				.filter(contentPath -> !isBlank(contentPath))
				.forEach(
						contentPath -> {
							expiryMap.put("group." + rootPathList
									+ "_group.path", contentPath);
							expiryMap.put("group." + rootPathList
									+ "_group.1_property",
									ASSATATION_OWNER_PROPERTY);
							expiryMap.put("group." + rootPathList
									+ "_group.2_property",
									ASSATATION_DATE_PROPERTY);
							expiryMap
									.put("group." + rootPathList
											+ "_group.3_property",
											ATTESTATION_REVIEWER);
							expiryMap.put("group." + rootPathList
									+ "_group.1_property.value", "true");
							expiryMap.put("group." + rootPathList
									+ "_group.3_property.value", "true");
							expiryMap.put("group." + rootPathList
									+ "_group.2_property.value", currentDate
									+ "%");
							expiryMap.put("group." + rootPathList
									+ "_group.1_property.operation", "exists");

							expiryMap.put("group." + rootPathList
									+ "_group.3_property.operation", "exists");
							expiryMap.put("group." + rootPathList
									+ "_group.2_property.operation", "like");
							expiryMap.put("group." + rootPathList
									+ "_group.property.and", "true");
							rootPathList--;
						});
		if (!expiryMap.isEmpty()) {
			expiryMap.putAll(getCommonPredicate());
		}
		return expiryMap;
	}

	/**
	 * Return list of resource
	 * 
	 * @param predicates
	 * @return list of resource
	 */
	private List<Resource> getResults(Map<String, String> predicates) {

		if (resourceResolver != null && searchService != null
				&& predicates != null) {
			return searchService.getSearchResultsAsResources(resourceResolver,
					predicates);
		}
		return null;
	}

	/**
	 * Validate Search path list
	 * 
	 * @param rootPath
	 * 
	 * @return true if empty list
	 */
	private boolean isEmptyPath(List<String> rootPath) {
		return rootPath == null || rootPath.isEmpty();
	}

	/**
	 * Get list of search path by site name
	 * 
	 * @param name
	 * 
	 * @return list of path
	 */
	private List<String> getPropertyList(String name) {

		if (name.toLowerCase().contains("suntrustrh")) {
			return configService.getPropertyArray("suntrustrh.root.path");
		} else if (name.toLowerCase().contains("onup")) {
			return configService.getPropertyArray("onUp.root.path");
		} else if (name.toLowerCase().contains("suntrust")) {
			return configService.getPropertyArray("suntrust.root.path");
		} else
			return configService.getPropertyArray(name + ".root.path");

	}

	/**
	 * return OSGI property value based on property name
	 * 
	 * @param name
	 * 
	 * @return property values
	 */
	private String getValue(String name) {
		return configService.getPropertyValue(name);
	}

	private Map<String, String> getCommonPredicate() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("group.p.or", "true");
		map.put("type", "cq:Page");
		map.put("p.limit", "-1");
		map.put("p.guessTotal", "true");
		return map;
	}
}
