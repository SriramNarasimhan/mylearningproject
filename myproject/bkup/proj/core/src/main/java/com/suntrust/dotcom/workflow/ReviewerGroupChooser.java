package com.suntrust.dotcom.workflow;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.suntrust.dotcom.config.STELConfigService;

/**
 * This class is used to pull the group based on passed
 * process arguments
 */

@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust Reviewer Group Chooser"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "chooser.label", value = "Suntrust Reviewer Group Chooser") 
})

public class ReviewerGroupChooser implements ParticipantStepChooser {
	/** STELConfigService class reference variable */
	@Reference 
	private STELConfigService stelConfigService;
	/**Logger variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(ReviewerGroupChooser.class);	
	/**
	 * Override method which returns group based on process arguments
	 */
	@Override
	public String getParticipant(WorkItem workItem, WorkflowSession wfSession, MetaDataMap wfMetaDataMap)
			throws WorkflowException {
		String groupName=null;
		if(wfMetaDataMap.containsKey("PROCESS_ARGS") && stelConfigService != null){		  
			   String groupKey = wfMetaDataMap.get("PROCESS_ARGS","string").toString().trim();	
			   groupName = stelConfigService.getPropertyValue(groupKey);
			   LOGGER.debug("GroupName:"+groupName);
               if(groupName.isEmpty() == false){            	   
            	   return groupName;
               }else{
       			LOGGER.error("Step process argument returns null or empty group");       			
       		}         
		}else{
			LOGGER.error("Step process argument or config object is null");
		}		
		return groupName;		
	}
}