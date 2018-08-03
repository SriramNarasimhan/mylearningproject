package com.suntrust.dotcom.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentFilter;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;

@Component(metatype = true, immediate = true, label = "Suntrust Custom Workflow step for Activating/Deactivating a Page/Asset")
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust Custom Workflow step for Activating/Deactivating a  Page/Asset"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust Custom Page/Asset Activation/Deactivation")
})

/**
 * Custom Process Step class to get the replication agent servers passed as argument in process step 
 * and perform the action (Activating/Deactivating), which is also passed as argument to the agents.
 * 
 * @author Nandakumaran Kasinathan
 * Base Version
 */
public class CustomActivateStep implements WorkflowProcess {
	
	/**ResourceResolverFactory to hold resource object*/
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	/**ResourceResolver to hold resource object*/
	private ResourceResolver resourceResolver = null;

	/**Variable to hold resource object*/
	@Reference
	private Replicator replicator;
	
	/**Variable to hold user session*/
	private Session mySession = null; 

	/** JCR PATH Constant*/
	private static final String TYPE_JCR_PATH = "JCR_PATH";
	
	/** String constant used in between servers in argument*/
	private static final String SEPARATOR = ",";
	
	/** String constant used separating servers and action in argument*/
	private static final String ACTIONSEPARATOR = ":";
	
	/** Class Constant used to get argument passed from process step*/
	private static final String PROCESS_ARGUMENTS = "PROCESS_ARGS";
	
	/** Empty string constant */
	private static final String EMPTY = "";

	/**Logger variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomActivateStep.class);

	/**
	 * 
	 * Default method that executes on respective process step of the workflow.
	 */

	public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) throws WorkflowException {

		WorkflowData workflowData = item.getWorkflowData();

		if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
			String path = workflowData.getPayload().toString();
			List<String> instancesList = readArgument(args);
			String argument = args.get(PROCESS_ARGUMENTS, EMPTY);
			String process = argument.substring(argument.lastIndexOf(ACTIONSEPARATOR) + 1);
			LOGGER.info("Action to be performed: " + process);
			try {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
				resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
				mySession = resourceResolver.adaptTo(Session.class);
				Iterator<String> iter = instancesList.iterator();
				while (iter.hasNext()) {
					// final String agentId = instancesList.get(0);//To be
					
					final String agentId = iter.next();
					LOGGER.info("Publish agent passed: " + agentId);
					ReplicationOptions opts = new ReplicationOptions();
					opts.setFilter(new AgentFilter() {

						public boolean isIncluded(final Agent agent) {
							return (agentId).equals(agent.getId());
						}
					});

					//Activating page or asset
					if (process.equalsIgnoreCase("activate")) {
						replicator.replicate(mySession, ReplicationActionType.ACTIVATE, path, opts);
						LOGGER.info("Activate Replication Completed for:" + path);
					} else {
						replicator.replicate(mySession, ReplicationActionType.DEACTIVATE, path, opts);
						LOGGER.info("Deactivate Replication Completed for:" + path);
					}
				}

			} catch (Exception e) {
				LOGGER.error("Exception captured: " + e.getMessage());
			} finally {
	            if (mySession != null && mySession.isLive()) {
	            	mySession.logout();
	            }
	            if (resourceResolver != null && resourceResolver.isLive()) {
	            	resourceResolver.close();
	            }
			}
		}
	}

	/**
	 * 
	 * Method to read the different publish/preview instances specified in the
	 * workflow.
	 * 
	 * @param args
	 * 
	 * @return List<String> - instance
	 */
	private List<String> readArgument(MetaDataMap args) {
		String argument = args.get(PROCESS_ARGUMENTS, EMPTY);
		String agents = argument.substring(0, argument.indexOf(ACTIONSEPARATOR));
		List<String> argList = new ArrayList<String>();
		if (agents.equals(EMPTY) == false) {
			if (agents.contains(SEPARATOR)) {
				return (argList = Arrays.asList(agents.split(SEPARATOR)));
			}
			else {
				argList.add(agents);
			}
		}
		return argList;
	}
}