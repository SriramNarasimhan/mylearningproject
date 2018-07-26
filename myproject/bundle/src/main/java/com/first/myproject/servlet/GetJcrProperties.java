/**
 * 
 */
package com.first.myproject.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Repository;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@SlingServlet(resourceTypes="sling/servlet/default", selectors = "properties", extensions = "html", methods="GET")

/**
 * @author Welcome
 * 
 */
public class GetJcrProperties extends SlingSafeMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4332982978242100825L;
	
	@Reference
	private Repository repository;

	/* (non-Javadoc)
	 * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String[] jsonStr = repository.getDescriptorKeys();
		String json = "";
		Map<String, String> map = new HashMap<String, String>();
		PrintWriter out = response.getWriter();
		ObjectMapper mapper = new ObjectMapper();
		// to enable standard indentation ("pretty-printing"):
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		for(int i =0;i<jsonStr.length;i++){
			try {
				map.put(jsonStr[i], repository.getDescriptor(jsonStr[i]));
				//convert map to JSON string
				json = mapper.writeValueAsString(map);
				out.println(json.toString());
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
