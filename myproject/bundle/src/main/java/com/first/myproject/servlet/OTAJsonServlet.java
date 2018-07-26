package com.first.myproject.servlet;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;


@SlingServlet(resourceTypes="/apps/wyndham-tablet/components/page/propertysummary", selectors = "otadata") //OTAJsonServlet with selector and html extension == replace .html with .otadata.html

//@SlingServlet(resourceTypes="sling/servlet/default", selectors = "otadata", extensions = "json", methods = "GET") //OTAJsonServlet with selector and json extension - uses default resourceType == replace .html with .otadata.json

/**
* @author Welcome
* 
*/
public class OTAJsonServlet extends SlingSafeMethodsServlet {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;
    

    /* (non-Javadoc)
    * @see org.apache.sling.api.servlets.SlingSafeMethodsServlet#doGet(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
    */
    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) throws ServletException,
    IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONWriter writer = new JSONWriter(response.getWriter());
        writer.setTidy(true);
        try {
			writer.object();
	        writer.key("PropertyId");
	        writer.value("bean.getPropertyId()");
	        writer.key("cityName");
	        writer.value("getCityName()");
	        writer.key("TelephoneNumber");
	        writer.value("bean.getTelephoneNumber()");
	        writer.key("BrandId");
	        writer.value("bean.getBrandId()");
	        writer.key("countryName");
	        writer.value("getCountryName()");
	        writer.key("latitude");
	        writer.value("getLatitude()");
	        writer.key("longitude");
	        writer.value("getLongitude()");
	        writer.endObject();
        } 
        catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
