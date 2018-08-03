package com.suntrust.dotcom.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.utils.InboxItemTypeDefinition;
import com.suntrust.dotcom.utils.InboxItemTypeProvider;

@SuppressWarnings("serial")
@SlingServlet(resourceTypes={"dotcom/components/workflow/inbox/gui/components/inbox/itemtype/list"}, extensions={"json"}, methods={"GET"})
public class InboxItemTypeProviderLister extends SlingSafeMethodsServlet{

	private static final Logger LOGGER = LoggerFactory.getLogger(InboxItemTypeProviderLister.class);
	  @Reference
	  InboxItemTypeProvider itemTypeProvider;
	  
	  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
	    throws ServletException, IOException
	  {
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    String type = request.getParameter("type");
	    boolean includeUntitled = Boolean.parseBoolean(request.getParameter("includeUntitled"));
	    
	    List<InboxItemTypeDefinition> inboxItemTypes = this.itemTypeProvider.getItemTypes(type, includeUntitled);
	    try
	    {
		JSONWriter w = new JSONWriter(response.getWriter());
	      if (Boolean.parseBoolean(request.getParameter("extendedInfo")))
	      {
	        w.array();
	        for (InboxItemTypeDefinition itemType : inboxItemTypes)
	        {
	          w.object();
	          for (Map.Entry<String, Object> entry : itemType.getProperties().entrySet())
	          {
	            Object entryValue = entry.getValue();
	            if (entryValue != null)
	            {
	              w.key((String)entry.getKey());
	              if ((entryValue instanceof String[]))
	              {
	                w.array();
	                String[] stringArray = (String[])entryValue;
	                for (String arrayEntry : stringArray) {
	                  w.value(arrayEntry);
	                }
	                w.endArray();
	              }
	              else
	              {
	                w.value(entryValue);
	              }
	            }
	          }
	          w.endObject();
	        }
	        w.endArray();
	      }
	      else
	      {
	        w.array();
	        for (InboxItemTypeDefinition itemType : inboxItemTypes)
	        {
	          String title = itemType.getTitle();
	          
	          w.object();
	          w.key("text").value(StringUtils.isNotBlank(title) ? title : itemType.getId());
	          w.key("value").value(itemType.getId());
	          w.endObject();
	        }
	        w.endArray();
	      }
	    }
	    catch (JSONException e)
	    {
	      throw new ServletException("error writing inbox item types results", e);
	    }
	  }
	  
}
