/**
 * 
 */
package com.first.myproject.servlet;

import java.io.IOException;

import javax.jcr.Node;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import com.day.cq.wcm.api.PageManager;

/**
 * @author Welcome
 *
 */
public class SearchServlet extends SlingSafeMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2668477239646279131L;

	/* (non-Javadoc)
	 * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
		JSONObject jsonObject = new JSONObject();
		JSONArray resultArray = new JSONArray();
		try {
			Node currentNode = request.getResource().adaptTo(Node.class);
			PageManager pageManager = request.getResource().getResourceResolver().adaptTo(PageManager.class);
			Node queryRoot = pageManager.getContainingPage(currentNode.getPath()).adaptTo(Node.class);
			
			String queryTerm = request.getParameter("q");
			if(StringUtils.isNotEmpty(queryTerm)){
				
			}
		}
		catch(Exception exception){
			exception.printStackTrace();
		}
	}
	
	

}
