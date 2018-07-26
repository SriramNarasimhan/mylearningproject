package com.first.myproject.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

/**
 * Sample workflow process that sends an email.
 */
@Component(immediate = true, metatype = true, label = "Sample workflow process-TestEmail")
@Service
public class TestMailProcess implements WorkflowProcess {

	@Property(value = "Sample workflow process implementation")
	static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
	@Property(value = "Sample Workflow Process-TestEmail")
	static final String LABEL = "process.label";

	private static final Logger LOG = LoggerFactory.getLogger(TestMailProcess.class);

	@Reference
	private MessageGatewayService messageGatewayService;

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	private static final String TYPE_JCR_PATH = "JCR_PATH";
	private static final String EMAIL_TEMPLATE_PATH = "/etc/designs/myproject/emails-templates/sample-reg.txt";

	public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {

		Map<String, String> mailTokens = new HashMap<String, String>();
		ResourceResolver resolver = getResourceResolver(session.adaptTo(Session.class));

		WorkflowData workflowData = item.getWorkflowData();
		if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
			String path = workflowData.getPayload().toString();
			try {
				LOG.info("@@@@ : " + path);
				Session jcrSession = session.adaptTo(Session.class);
				Node payloadNode = (Node) jcrSession.getItem(path);

				if (payloadNode != null) {
					if (payloadNode.getParent().hasNode("credentials")) {
						payloadNode = payloadNode.getParent().getNode("credentials");
					} else {
						String[] toIds = null;
						payloadNode = payloadNode.getParent().addNode("credentials", "nt:unstructured");
						payloadNode.setProperty("userId", "");
						payloadNode.setProperty("password", "");
						payloadNode.setProperty("recipientsEmail", toIds);
						jcrSession.save();
					}
					mailTokens.put("userId", payloadNode.getProperty("userId").getString());
					mailTokens.put("pwd", payloadNode.getProperty("password").getString());
					LOG.info("User******: " + payloadNode.getProperty("userId").getString());
					LOG.info("Password*****: " + payloadNode.getProperty("password").getString());
					sendMail(mailTokens, resolver, payloadNode.getProperty("recipientsEmail").getValues());
				}
			} catch (RepositoryException e) {
				throw new WorkflowException(e.getMessage(), e);
			} finally {
				if (resolver != null) {
					resolver.close();
				}
			}
		}
	}

	private boolean sendMail(Map<String, String> mailTokens, ResourceResolver resolver, Value[] values) {

		try {
			// path to the template
			Resource emailTemplateResource = resolver.getResource(EMAIL_TEMPLATE_PATH);
			if (emailTemplateResource.getChild("file") != null) {
				emailTemplateResource = emailTemplateResource.getChild("file");
			}
			if (emailTemplateResource != null) {

				final MailTemplate mailTemplate = MailTemplate.create(emailTemplateResource.getPath(),
				        emailTemplateResource.getResourceResolver().adaptTo(Session.class));

				final HtmlEmail email = mailTemplate.getEmail(StrLookup.mapLookup(mailTokens), HtmlEmail.class);

				email.setSubject("Sample Test Message");
				for (final Value value : values) {
					email.addTo(value.getString());
				}

				MessageGateway<HtmlEmail> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
				LOG.info("messageGateway111" + messageGateway);
				if (messageGateway == null) {
					LOG.error("Unable to retrieve message gateway for HTMLEmails, please configure Gateway in OSGi Console");
				}
				// use object to send email
				messageGateway.send(email);
				LOG.info("Message sent! check mail");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LOG.error("Error sending email: " + e.getMessage());
			LOG.error("Please ensure you have turned on access for less secure apps - https://www.google.com/settings/security/lesssecureapps");
			return false;
		}
	}

	protected ResourceResolver getResourceResolver(final Session session) {
		try {
			final Map<String, Object> authenticationMap = new HashMap<String, Object>();
			authenticationMap.put(JcrResourceConstants.AUTHENTICATION_INFO_SESSION, session);
			return resourceResolverFactory.getResourceResolver(authenticationMap);
		} catch (final Exception e) {
			LOG.error("Exception: " + e.getMessage());
			return null;
		}
	}
}