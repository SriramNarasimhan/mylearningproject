package com.suntrust.dotcom.utils;

import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.adobe.granite.workflow.collection.util.ResultSet;
import com.adobe.granite.workflow.exec.InboxItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InboxResourceProvider {

	private static final Logger LOG = LoggerFactory.getLogger(InboxResourceProvider.class);
	  
	  public static List<Resource> getResources(ResourceResolver resolver, InboxItemTypeProvider itemTypeProvider, ResultSet<InboxItem> inboxItems, String itemResourceType)
	  {
	    List<Resource> inboxResources = new ArrayList();
	    if (inboxItems != null) {
	      for (InboxItem inboxItem : (InboxItem[])inboxItems.getItems())
	      {
	        ValueMap inboxItemMap = new ValueMapDecorator(new HashMap());
	        inboxItemMap.put("inboxItem", inboxItem);
	        InboxItemTypeDefinition itemTypeDefinition = itemTypeProvider.getItemType(inboxItem.getItemType(), inboxItem.getItemSubType());
	        String[] subTypeActionRels = itemTypeDefinition.getActionsRels(inboxItem.getStatus());
	        inboxItemMap.put("subTypeActionRels", subTypeActionRels);
	        inboxResources.add(new ValueMapResource(resolver, inboxItem.getId(), itemResourceType, inboxItemMap));
	      }
	    }
	    return inboxResources;
	  }
}
