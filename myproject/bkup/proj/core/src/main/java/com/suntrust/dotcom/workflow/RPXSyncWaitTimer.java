package com.suntrust.dotcom.workflow;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.suntrust.dotcom.config.STELConfigService;

/**
 * Workflow process step class to update RPX rates 
 */
@Component
@Service 
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust RPX Sync Wait Timer"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust RPX Sync Wait Timer") })
public class RPXSyncWaitTimer implements WorkflowProcess {
	/** Logger variable */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RPXSyncWaitTimer.class);
	/** STEL config class reference variable */
	@Reference
	private STELConfigService stelConfigService;
	/**
	 * RPX Sync Process execute method to sync up RPX rates
	 */
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap metaDataMap) {
		try {			
			int retrydelayMinutes=Integer.parseInt(stelConfigService.getPropertyValue("rpx.prod.retry.delay.mins"));
			// 1 second = 1000 milli seconds, 1 minute = 60000 seconds
			Thread.sleep(60000*retrydelayMinutes);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
	}

}
