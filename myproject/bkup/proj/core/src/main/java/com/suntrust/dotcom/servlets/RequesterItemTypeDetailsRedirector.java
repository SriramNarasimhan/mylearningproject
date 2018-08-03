package com.suntrust.dotcom.servlets;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.text.Text;
import com.suntrust.dotcom.utils.InboxItemTypeProvider;

@SuppressWarnings("serial")
@SlingServlet(resourceTypes={"dotcom/components/workflow/inbox/gui/components/inbox/itemtype/redirect"}, extensions={"html"}, methods={"GET"})
public class RequesterItemTypeDetailsRedirector extends SlingSafeMethodsServlet{

	private static final Logger LOGGER = LoggerFactory.getLogger(RequesterItemTypeDetailsRedirector.class);
	  @Reference
	  InboxItemTypeProvider itemTypeProvider;
	  
	  
	  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
	    throws ServletException, IOException
	  {
	    String itemId = request.getParameter("item");
	    String itemTypeName = request.getParameter("type");
	    String subTypeName = request.getParameter("subtype");
	    
	    LOGGER.debug("ItemTypeDetailsRedirector: request with item '{}', type '{}', and subtype '{}'", new Object[] { itemId, itemTypeName, subTypeName });
	    
	    String detailsUrl = this.itemTypeProvider.resolveDetailsURL(itemTypeName, subTypeName);
	    if (StringUtils.isBlank(detailsUrl))
	    {
	      LOGGER.info("Failed to resolve type '{}' with subtype '{}', attempting to resolve without subtype", itemTypeName, subTypeName);
	      detailsUrl = this.itemTypeProvider.resolveDetailsURL(itemTypeName, "default");
	    }
	    if (StringUtils.isNotBlank(detailsUrl))
	    {
	      if (!detailsUrl.contains("?")) {
	        detailsUrl = detailsUrl + "?item=" + Text.escape(itemId) + "&_charset_=utf-8";
	      }
	      detailsUrl = request.getContextPath() + detailsUrl;
	      response.sendRedirect(detailsUrl);
	    }
	    else
	    {
	      throw new ServletException("No details URL found for item type '" + itemTypeName + "' and subtype '" + subTypeName + "'");
	    }
	  }
}
