/**
 * 
 */
package com.suntrust.dotcom.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.GenericEnum;

/**
 * @author UGRK104
 *
 */

@SuppressWarnings("serial")
@SlingServlet(paths = "/dotcom/nac-rest-api-consumer")
public class NACRestServiceAPIConsumerServlet extends SlingAllMethodsServlet {
	
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NACRestServiceAPIConsumerServlet.class);	
	
	/** instance variable to hold NAC rest service API URL * */
	private String nacRestServiceAPIURL = null;
	
	/**	Service variable to read the run-mode configurations * */
	@Reference
	private SuntrustDotcomService suntrustDotcomService;
	
	/* (non-Javadoc)
     * @see org.apache.sling.api.servlets.SlingAllMethodsServlet#doPost(org.apache.sling.api.SlingHttpServletRequest, org.apache.sling.api.SlingHttpServletResponse)
     */
    protected void doGet(final SlingHttpServletRequest request,final SlingHttpServletResponse response)
    {
    	
    	LOGGER.debug("NACRestServiceAPIConsumerServlet - doPost method Called");
    	
    	try
    	{     	
    		this.nacRestServiceAPIURL = suntrustDotcomService.getPropertyValue(GenericEnum.NAC_REST_SERVICE_API_URL.getValue()).trim();
    		
    		LOGGER.debug("NAC Rate Rest API URL - " + this.nacRestServiceAPIURL);
    		
    		//URL url = new URL("https://serviceproxy-dev1-int.suntrust.com/RestfulServices/InvestmentServices/RateRetrievalService");
    		
    		URL url = new URL(this.nacRestServiceAPIURL);
    		
    		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    		conn.setDoOutput(true);
    		conn.setRequestMethod("POST");
    		conn.setRequestProperty("Content-Type", "application/json");
    		conn.setRequestProperty("Client_Id", "AER");
    		
    		String zipcode = request.getParameter("ZipCode");
    		
    		String channel = request.getParameter("channel");
    		
    		//String input = "{\"ZipCode\":30011,\"channel\":\"NAC\"}";
    		
    		String input = "{\"ZipCode\":" + zipcode + ",\"channel\":\"NAC\"}";
    		
    		LOGGER.debug("json input - " + input);
    		
    		OutputStream os = conn.getOutputStream();
    		os.write(input.getBytes());
    		os.flush();
    		
    		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
    		
    		String output;
    		
    		StringBuilder jsonDataBuilder = new StringBuilder();
    		
    		while ((output = br.readLine()) != null) {
    			jsonDataBuilder.append(output);
    		}
    		
    		response.getWriter().write(jsonDataBuilder.toString());
    		
    	}
     	catch (MalformedURLException malformedURLException) 
    	{
     		LOGGER.error("NAC Rate Rest API URL - " + this.nacRestServiceAPIURL);
    		LOGGER.error("MalformedURLException cought in NACRestServiceAPIConsumerServlet - doPost method", malformedURLException);
    	} 
    	catch (IOException ioException) 
    	{
    		LOGGER.error("NAC Rate Rest API URL - " + this.nacRestServiceAPIURL);
    		LOGGER.error("IOException cought in NACRestServiceAPIConsumerServlet - doPost method", ioException);
        }
    	catch(Exception exception)
    	{
    		LOGGER.error("NAC Rate Rest API URL - " + this.nacRestServiceAPIURL);
    		LOGGER.error("Exception cought in NACRestServiceAPIConsumerServlet - doPost method", exception);
    	}
    }

}
