package com.suntrust.dotcom.workflow;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.suntrust.dotcom.config.SuntrustDotcomService;



/**
 * @author uiam82
 *
 */
@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust Production Rates Update Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust Production Rates Update Service") })
public class RatesProdDeployment implements WorkflowProcess {
	@Reference
	ResourceResolverFactory resourceResolverFactory;
	@Reference
	SuntrustDotcomService suntrustDotcomService;
	@Reference
	DataSourcePool dataSourcePool;
	String rateParam="ratetype";
	String rateType=null;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RatesProdDeployment.class);
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap metaDataMap){		
		try {
			RatesProdDeploymentHelper helper=new RatesProdDeploymentHelper();
			String pagePath = workItem.getWorkflowData().getPayload().toString();				
			Session mySession=workflowSession.getSession();
			Node node = mySession.getNode(pagePath).getNode("jcr:content");			
			if(null != node && node.hasProperty(rateParam)){
				rateType = node.getProperty(rateParam).getValue().toString();
				// Removing attribute from payload and setting to worklfow metadata.
				node.getProperty(rateParam).remove();
				workItem.getWorkflowData().getMetaDataMap().put(rateParam,rateType);
			}
			LOGGER.debug("Rate type parameter prod:"+rateType);
			if(rateType!=null && rateType.isEmpty() == false){
				if(rateType.equalsIgnoreCase("cdrates")){
					helper.loadCDRates(workItem, workflowSession,dataSourcePool);
				}else if(rateType.equalsIgnoreCase("equityrates")){
					helper.loadEquityRates(workItem, workflowSession,dataSourcePool);
				}else{
					LOGGER.error("Rate type parameter is not passing properly");
				}
			}else{
				LOGGER.error("Rate type parameter should not be empty or null");
			}
			
		} catch (PathNotFoundException e) {
			LOGGER.error("PathNotFoundException in rates prod deployment"+e.getMessage());
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException in rates prod deployment"+e.getMessage());
		}	
		
	}

}
