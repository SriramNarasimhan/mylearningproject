package com.suntrust.dotcom.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.jsoup.helper.StringUtil;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.Externalizer;
import com.google.common.collect.ImmutableList;
import com.suntrust.dotcom.config.ContentExpiryConfigService;
import com.suntrust.dotcom.services.EmailService;

/**
 * Content expiry owner notification process
 *
 */
@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Content expiry notification process"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Content expiry notification process") })
public class ContentExpiryNotificationProcess implements WorkflowProcess {
	private static final String NO_OF_WEEK_ADVANCE = "noOfWeekAdvance";
	private static final String ATTESTATION_DAY = "attestationDays";
	private static final String NO_OF_WEEK_DUE = "noOfWeekDue";
	private static final String NO_OF_WEEK_PAST = "noOfWeekPast";
	private static final String NO_OF_WEEK_OVERDUE = "noOfWeekOverdue";

	public static final ImmutableList<String> CONSTANTS = ImmutableList.of(
			NO_OF_WEEK_ADVANCE, ATTESTATION_DAY, NO_OF_WEEK_DUE,
			NO_OF_WEEK_PAST, NO_OF_WEEK_OVERDUE);
	private static final String SERVICE = "dotcomreadservice";

	private static final String PREFIX = "/editor.html";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ContentExpiryNotificationProcess.class);

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Reference
	private EmailService emailService;

	@Reference
	private ContentExpiryConfigService configService;

	private ResourceResolver resourceResolver;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap metaDataMap) throws WorkflowException {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, SERVICE);
			resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);
			CONSTANTS
					.forEach(emailType -> {
						Map<String, String> params = new HashMap<String, String>();
						params.put(
								"subject",
								configService.getPropertyValue(emailType
										+ ".email.subject"));
						params.put(
								"attestationEmail",
								configService.getPropertyValue(emailType
										+ ".email.id"));
						params.put("attestationWeek",
								configService.getPropertyValue(emailType));
						params.put(
								"description",
								configService.getPropertyValue(emailType
										+ ".email.description"));
						params.put(
								"note",
								configService.getPropertyValue(emailType
										+ ".email.note"));

						updatePagePath(getWorkFlowData(workItem, emailType),
								params, emailType);
						param.clear();
					});

		} catch (LoginException e) {
			LOGGER.error("Login Exception{} TRACE: {}", e.getMessage(), e);
		} finally {
			if (resourceResolver != null) {
				resourceResolver.close();
			}
		}
	}

	private void updatePagePath(Map<String, Set<String>> resourceMap,
			Map<String, String> params, String emailType) {
		if (resourceMap != null && !resourceMap.isEmpty()) {

			resourceMap.forEach((userID, LisiOfResource) -> {
				StringBuilder absolutePages = new StringBuilder();
				Set<String> managerList = new HashSet<String>();
				LisiOfResource.forEach(value -> {
					managerList.add(getEmailId(getAttestationReviewer(value)));
					absolutePages.append("<li><p ><span>"
							+ getAbsolutePagePath(value) + "</span></p></li>");
				});

				setRecipients(managerList, userID, absolutePages, params,
						emailType

				);
			});
		}
	}

	private String getAttestationReviewer(String path) {

		Resource resource = resourceResolver.getResource(path + "/jcr:content");
		if (resource != null) {
			Node node = resource.adaptTo(Node.class);

			try {
				javax.jcr.Property prop = node
						.getProperty("attestationReviewer");
				if (prop != null) {
					return prop.getString();
				}
			} catch (RepositoryException e) {
				LOGGER.error(
						"EMAIL WORKFLOW -- EXCEPTION THROWN IN GET ATTESTATION MANAGER DETAILS::. MESSAGE: {} TRACE: {}",
						e.getMessage(), e);
			}

		}
		return "someone@SunTrust.com";
	}

	private String getEmailId(String racfId) {
		try {
			UserManager userManager = resourceResolver
					.adaptTo(UserManager.class);
			Authorizable userAuthorizable;

			userAuthorizable = userManager.getAuthorizable(racfId);

			return userAuthorizable.getProperty("./profile/email") != null ? userAuthorizable
					.getProperty("./profile/email")[0].getString()
					: "someone@SunTrust.com";
		} catch (RepositoryException e) {
			LOGGER.error(
					"EMAIL WORKFLOW -- EXCEPTION THROWN IN GET EMAIL ID. MESSAGE: {} TRACE: {}",
					e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Populate user details
	 * 
	 * @param UserId
	 * @param absolutePages
	 * @param emailDescription
	 */
	private void setRecipients(Set<String> managerList, String UserId,
			StringBuilder absolutePages, Map<String, String> map,
			String emailType) {
		List<String> ccRecipients = new ArrayList<String>();
		List<String> emailRecipients = new ArrayList<>();
		try {
			if (emailType.contains(NO_OF_WEEK_PAST)
					|| emailType.contains(NO_OF_WEEK_OVERDUE)) {
				emailRecipients.addAll(managerList);
				ccRecipients.add(getEmailId(UserId));
			} else {
				emailRecipients.add(getEmailId(UserId));
			}

			ccRecipients.add(configService
					.getPropertyValue("suntrust.cc.email.dl"));
			sendEmail(absolutePages, map, emailRecipients, ccRecipients);
		} catch (Exception e) {
			LOGGER.error(
					"EMAIL WORKFLOW -- EXCEPTION THROWN IN SET RECIPIENTS METHOD. MESSAGE: {} TRACE: {}",
					e.getMessage(), e);
		}

	}

	/**
	 * Get absolute page path
	 * 
	 * @param path
	 * @return absolute Page Path
	 */
	private String getAbsolutePagePath(String path) {
		Externalizer externalizer = resourceResolver
				.adaptTo(Externalizer.class);
		if (path.toLowerCase().contains("content/suntrust")) {
			return externalizer.authorLink(resourceResolver, PREFIX + path
					+ ".html");
		} else {
			return externalizer.authorLink(resourceResolver, path);
		}

	}

	/**
	 * Send email to given distribution list using specified template
	 * 
	 * @param template
	 * @param absolutePages
	 */
	private void sendEmail(StringBuilder absolutePages,
			Map<String, String> map, List<String> emailRecipients,
			List<String> ccRecipients) {
		Map<String, String> emailParams = new HashMap<>();
		String template = configService
				.getPropertyValue("attestation.email.template");
		emailParams.put("senderEmailAddress",
				configService.getPropertyValue("suntrust.sender.emailid"));
		emailParams.put("senderName",
				configService.getPropertyValue("suntrust.sender.displayName"));
		emailParams.put("pageLink", absolutePages.toString());
		emailParams.put("digitalAttestation",
				"suntrust.digital.attestation.emailid");
		emailParams.putAll(map);
		if (!StringUtil.isBlank(template)) {
			emailService.sendEmail(template, emailParams, emailRecipients,
					ccRecipients);
		}
	}

	private Map<String, Set<String>> getWorkFlowData(WorkItem workItem,
			String pageType) {
		return workItem.getWorkflow().getWorkflowData().getMetaDataMap()
				.get(pageType, Map.class);
	}
}
