package com.suntrust.dotcom.workflow;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
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
import com.day.cq.workflow.collection.ResourceCollection;
import com.day.cq.workflow.collection.ResourceCollectionManager;
import com.day.cq.workflow.collection.ResourceCollectionUtil;
import com.suntrust.dotcom.services.RewriteMapService;


@Component(immediate=true, enabled=true, metatype=true)
@Service(value=WorkflowProcess.class)
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust RewriteMap Workflow Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust RewriteMap Workflow Service") 
})
public class RewriteMapWorkflowService implements WorkflowProcess{

	 private final Logger log = LoggerFactory.getLogger(RewriteMapWorkflowService.class);

	    @Reference
	    private ResourceResolverFactory resourceResolverFactory;

	    @Reference
	    private RewriteMapService rewriteMapService;
	    
	    @Reference
		private ResourceCollectionManager resourceCollectionManager;
	    
	    /** Session class reference variable */
		private Session session=null;
		private ResourceResolver serviceResolver = null;
		
		private final String[] DEFAULT_WF_PACKAGE_TYPES = {"cq:Page", "cq:PageContent"};

	    
	    private static final String VANITY_SERVICE_ACCOUNT_IDENTIFIER = "dotcomreadservice";

	
	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {

		log.debug(" Rewrite Map Workflow started");
		try {
        	// Create the Map to pass in the Service Account Identifier
            // "SERVICE_ACCOUNT_IDENTIFIER" is mapped  to the CRX User via a SEPARATE ServiceUserMapper Factory OSGi Config
            final Map<String, Object> authInfo = Collections.singletonMap(
                    ResourceResolverFactory.SUBSERVICE,
                    (Object) VANITY_SERVICE_ACCOUNT_IDENTIFIER);

            // Get the Service resource resolver
            serviceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo);
            session = serviceResolver.adaptTo(Session.class);


            //make sure the user has ACL permissions on the paths
            log.debug("service resolver session user {}", serviceResolver.getUserID());
            
            log.debug(" destination path='{}'", rewriteMapService.getDestinationPath());
            
            List<String> wfPagePaths = new ArrayList<>();
            
            String payloadPath = workItem.getWorkflowData().getPayload().toString();
            String workflowModelTitle=workItem.getWorkflow().getWorkflowModel().getTitle();
            boolean isUnPubWf =("STcom Unpublish Workflow".equals(workflowModelTitle) || "Quick Unpublish Workflow".equals(workflowModelTitle)) ? true : false;
            //boolean isUnPubWf =("Unpublish Workflow".equals(workflowModelTitle)) ? true : false;
            
            Node payLoadNode=session.getNode(payloadPath).getNode("jcr:content");
     	   
     	   NodeType[] nodeType=payLoadNode.getMixinNodeTypes();
     	   boolean iswfPkg=Arrays.asList(nodeType).stream().anyMatch(node -> "vlt:Package".equalsIgnoreCase(node.getName()));
     	   
     	   if(iswfPkg){
     		   wfPagePaths=getWorkflowPackagePaths(payLoadNode);     		  
     	   }
     	   else
     		   wfPagePaths.add(payloadPath);
     	   
           
           if(null == wfPagePaths || wfPagePaths.isEmpty() || null == rewriteMapService.getDestinationPath()) {
        	   log.error("The workflow payload is not valid for rewriteMapService or RewirteService has not been configured properly");
        	   return;
           }
           
           String[] multiPaths=rewriteMapService.getMultiPaths();
           String wfPageTree=wfPagePaths.get(0);
           String multiPathUsed=null;
           
           Optional<String> matchPaths = Stream.of(multiPaths).filter(path -> wfPageTree.startsWith(path)).findAny();
           
           if(!matchPaths.isPresent()){
        	   log.error("The workflow payload is not valid for multipaths configured");
        	   return;
           }
           else{
        	   multiPathUsed=matchPaths.get();
        	   for(String path :  wfPagePaths){
        		   log.info("invoking rewrite serice for path {}",path);
        		   rewriteMapService.getRewriteMap(serviceResolver, multiPathUsed, path,isUnPubWf);
        	   }
           }
        	   
		 } catch (LoginException | RepositoryException e) {
	            log.error("Error occured in Rewrite workflow service.", e);
	        } finally {
	            // ALWAYS close resolvers you open
	            if (serviceResolver != null) {
	            	serviceResolver.close();
	            }
	        }
	}
	
	private List<String> getWorkflowPackagePaths(Node payLoadNode) throws RepositoryException{
		final ResourceCollection resourceCollection = ResourceCollectionUtil.getResourceCollection(payLoadNode, resourceCollectionManager);
		if(null!=resourceCollection){
			final List<Node> members = resourceCollection.list(DEFAULT_WF_PACKAGE_TYPES);
			
			return members.stream()
					.map(node-> {
					try{
						return node.getPath();
					}
					catch(RepositoryException e){
						log.error("Error encountered while retrieving page paths", e);
					}
					return null;
					})
					.collect(Collectors.toList());
		}
		return null;
	}


}
