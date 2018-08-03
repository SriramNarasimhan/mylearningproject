package com.suntrust.dotcom.workflow;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.suntrust.dotcom.config.SuntrustDotcomService;

/**
 * This ParticipantStepChooser class creates session with the custom service
 * user created and pulls reviewer value from payload based parameter passed
 * from participant set, which was selected in custom instantiation.
 * 
 * @author Nandakumaran Kasinathan
 * Base Version
 */

@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust Reviewer Chooser"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "chooser.label", value = "Suntrust Reviewer Chooser") 
})

public class GetDotComService implements ParticipantStepChooser {
	
	/** ResourceResolverFactory class reference variable */
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	/** SuntrustDotcomService class reference variable */
	@Reference 
	private SuntrustDotcomService dotcomServiceconfig;

	/**Logger variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(GetDotComService.class);
	
	/**Variable to hold selected reviewer*/
	private String reviewer = "";
	
	/**Variable to hold argument passed from task*/
	private String payloadParameter = "";
	
	/**Variable to hold resource object*/
	private ResourceResolver resourceResolver = null;
	
	/**Variable to hold user session*/
	private Session mySession = null; 
	
	/**
	 * Overrided method which returns selected reviewer in custom instantiation screen
	 */
	@Override
	public String getParticipant(WorkItem workItem, WorkflowSession wfSession, MetaDataMap wfMetaDataMap)
			throws WorkflowException {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
			mySession = resourceResolver.adaptTo(Session.class);
			String pagePath = workItem.getWorkflowData().getPayload().toString();
			Node node = mySession.getNode(pagePath).getNode("jcr:content");			
			if (dotcomServiceconfig != null) {         
			   String groupKey = wfMetaDataMap.get("PROCESS_ARGS","string").toString().trim();	
			   String adGroupValue = dotcomServiceconfig.getPropertyValue(groupKey);
               if(adGroupValue.isEmpty() == false){   
            	   LOGGER.debug("adGroupValue>>:"+adGroupValue);
            	   return adGroupValue;
               }
            }
			
			if(wfMetaDataMap.containsKey("PROCESS_ARGS")){
				payloadParameter = wfMetaDataMap.get("PROCESS_ARGS","string").toString().trim();
				LOGGER.debug("payloadParameter>>:"+payloadParameter);
			}			
			if(null != node && node.hasProperty(payloadParameter)){
				reviewer = node.getProperty(payloadParameter).getValue().toString();				
				workItem.getMetaDataMap().put(payloadParameter,reviewer);
				LOGGER.debug("set passed values as reviewer>>:"+reviewer);
			}else{
				reviewer =  workItem.getWorkflow().getInitiator();
				workItem.getMetaDataMap().put(payloadParameter,reviewer);
				LOGGER.debug("else set initiator as reviewer>>:"+reviewer);
			}
		} catch (Exception e) {
			LOGGER.error("Exception captured. Message: {}, Trace: {}",e.getMessage(),e);
		} finally {
            if (mySession != null && mySession.isLive()) {
            	mySession.logout();
            	LOGGER.debug("session is closed in finally >>:");
            }
            if (resourceResolver != null && resourceResolver.isLive()) {
            	resourceResolver.close();
            }
            LOGGER.debug("resourceResolver closed >>:");
		}		
		LOGGER.debug("reviewer >>:"+reviewer);
		return reviewer;		
	}
}