package com.suntrust.dotcom.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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

import com.day.cq.replication.Replicator;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.collection.ResourceCollectionManager;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.Utils;

/**
 * This process step Java code is used to activate the referenced
 * modified or new assets in page.
 */
@Component(metatype = true, immediate = true, label = "Suntrust Asset Reference Process")
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust Asset Reference Process"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust Asset Reference Process") })
public class AssetReferenceProcess implements WorkflowProcess {
	/** ResourceResolverFactory object reference variable*/
	@Reference
	ResourceResolverFactory resourceResolverFactory;
	/** ResourceCollectionManager object reference variable*/
	@Reference
	private ResourceCollectionManager resourceCollectionManager;
	/** SuntrustDotcomService object reference variable*/
	@Reference
	private SuntrustDotcomService dotcomService;
	/** Replicator object reference variable*/
	@Reference
	private Replicator replicator;
	/** Logger object reference variable*/	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AssetReferenceProcess.class);
	
	/**
	 * Overrided method
	 */
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap metaDataMap) {
		Session session = workflowSession.getSession();
		try {			
			List<String> payloadList = new ArrayList<String>();
			List<String> assetList = new ArrayList<String>();
			ListIterator<String> payloadListIterator = null;
			ListIterator<String> assetListIterator = null;
			String pagePath = workItem.getWorkflowData().getPayload().toString();		
			if (workItem.getWorkflowData().getPayloadType().equals("JCR_PATH")) {				
				LOGGER.info("AssetReferenceProcess Payload packahe page path >"+pagePath);
				payloadList = Utils.getAbsolutePaths(pagePath, session, resourceCollectionManager);
			}
			payloadListIterator = payloadList.listIterator();
			while (payloadListIterator.hasNext()) {
				String eachPayloadPath=payloadListIterator.next();
				LOGGER.info("AssetReferenceProcess Payload page path >"+eachPayloadPath);
				assetList = Utils.getAssetsList(replicator, eachPayloadPath,
						resourceResolverFactory, session);
				assetListIterator = assetList.listIterator();
				while (assetListIterator.hasNext()) {
					String eachPayAssetPath=assetListIterator.next();
					LOGGER.info("AssetReferenceProcess Each asset page path for activation>"+eachPayAssetPath);
					Utils.activateAsset(replicator, session,
							eachPayAssetPath);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception captured: "+ e.getMessage());
		}
	}

}
