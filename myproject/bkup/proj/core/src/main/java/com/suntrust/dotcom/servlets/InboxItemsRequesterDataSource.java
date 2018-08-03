package com.suntrust.dotcom.servlets;


import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.workflow.collection.util.ResultSet;
import com.adobe.granite.workflow.exec.InboxItem;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.suntrust.dotcom.utils.InboxItemsUtils;
import com.suntrust.dotcom.utils.ResultSetImpl;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.suntrust.dotcom.utils.InboxResourceProvider;
import com.suntrust.dotcom.utils.InboxItemTypeProvider;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(methods={"GET"}, 
			  resourceTypes={"dotcom/components/workflow/inbox/gui/components/inbox/datasource/itemsdatasource"}
			  )
public class InboxItemsRequesterDataSource extends SlingAllMethodsServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(InboxItemsRequesterDataSource.class);
	  @Reference
	  private QueryBuilder queryBuilder;
	  @Reference
	  private InboxItemTypeProvider itemTypeProvider;
	  
	  
	 // private AdapterManager adapterManager;
	  
	  @Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		  
		ResourceResolver resourceResolver = request.getResourceResolver();
		Resource resource = request.getResource();
		
		String suffix = request.getRequestPathInfo().getSuffix();
		
		Config dsCfg = new Config(resource.getChild(Config.DATASOURCE));
		Integer offset = InboxItemsUtils.getSelectorAsInteger(request, 0);
		Integer limit = InboxItemsUtils.getSelectorAsInteger(request, 1);
		String itemResourceType = dsCfg.get("itemResourceType");
		
		offset = Integer.valueOf(offset == null ? 0 : offset.intValue());
		limit = Integer.valueOf(limit == null ? 40 : limit.intValue());
		
		limit = Integer.valueOf(limit.intValue() + 1);
		handleDefault(request, resourceResolver, itemResourceType, offset.intValue(), limit.intValue(), suffix);
	}
	  
	  private void handleDefault(SlingHttpServletRequest request, ResourceResolver resourceResolver, String itemResourceType, int offset, int limit, String basePath)
	  {
	    try
	    {
	      List<Resource> inboxItemResources = Collections.emptyList();
	      
	      Session session = resourceResolver.adaptTo(Session.class);
	      if (session != null)
	      {
	        long start = 0L;
	        if (LOG.isDebugEnabled()) {
	          start = System.currentTimeMillis();
	        }
	        
	        ResultSet<InboxItem> inboxItems = createRequesterInboxQuery(session, resourceResolver);
	        
	        inboxItemResources =InboxResourceProvider.getResources(resourceResolver, this.itemTypeProvider, inboxItems, itemResourceType);
	        if (LOG.isDebugEnabled())
	        {
	          long afterQuery = System.currentTimeMillis();
	          LOG.debug("it took {}ms to execute the query for the inbox", Long.valueOf(afterQuery - start));
	        }
	      }
	      else
	      {
	        LOG.warn("Unable to get InboxItems because ResourceResolver is not adatpable to WorkflowSession.");
	      }
	      DataSource ds = new SimpleDataSource(inboxItemResources.iterator());
	      request.setAttribute(DataSource.class.getName(), ds);
	    }
	    catch (Exception e)
	    {
	      LOG.error("Unable to get inbox items", e);
	    }
	  }
	  
	  private ResultSet<InboxItem> createRequesterInboxQuery(Session session, ResourceResolver resourceResolver){
		  
		  try{
			  String loggedInUser = session.getUserID();
		  Map<String,String> predicateMap = new HashMap<>();
		  List<InboxItem> inboxItems=new ArrayList<>();
		  predicateMap.put("type", "granite:InboxItem");
		  predicateMap.put("path", "/etc/workflow/instances");
		  predicateMap.put("1_property", "assignee");
		  predicateMap.put("2_property", "status");
		  predicateMap.put("1_property.operation", "exists");
		  predicateMap.put("2_property.value", "ACTIVE");
		  predicateMap.put("p.limit", "-1");
		  predicateMap.put("orderby", "@startTime");
		  predicateMap.put("orderby.sort", "desc");
		  		  
		  Query queryObj = this.queryBuilder.createQuery(PredicateGroup.create(predicateMap), session);
		  SearchResult result = queryObj.getResult();
		 // Iterator<Node> nodes = result.getNodes();
		  Iterator<Resource> resources = result.getResources();
		  LOG.info("Logged in user ::"+ loggedInUser);
		  LOG.info("Total no. of granite items::"+result.getTotalMatches());
		  long total=0L;
		  Node payLoadNode;
		  Node inboxItemNode;
		  Resource inboxItemRes;
		  Resource res;
		  InboxItem item;
		  while(resources.hasNext()){
			  res= resources.next();
			  inboxItemNode = res.adaptTo(Node.class);
			  item = res.adaptTo(InboxItem.class);
			  inboxItemRes = resourceResolver.getResource(item.getContentPath());
				if (inboxItemNode.hasProperty("wfModelId")) {
					if (inboxItemNode.getProperty("wfModelId").getString().startsWith("/etc/workflow/models/dotcom/")
							&& null != inboxItemRes && session.getNode(item.getContentPath()).hasNode("jcr:content")) {
						payLoadNode = session.getNode(item.getContentPath()).getNode("jcr:content");
						if (null != item && null != loggedInUser && null != payLoadNode
								&& payLoadNode.hasProperty("requesterreviewer") && payLoadNode
										.getProperty("requesterreviewer").getString().equalsIgnoreCase(loggedInUser)) {
							LOG.info("Filtered inbox item ::" + payLoadNode.getPath());
							inboxItems.add(item);
							total += 1L;
						}
					}
				}
		  }
		  InboxItem[] items = (InboxItem[])inboxItems.toArray(new InboxItem[inboxItems.size()]);
		  ResultSet<InboxItem> resultSet = new ResultSetImpl<>(items, total);
		  return resultSet;
		  }
		  catch(Exception e){
			  LOG.error("Error in retrieving inbox Items",e);
			  return null;
		  }
  }
}
