package com.suntrust.dotcom.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.config.SuntrustDotcomService;

@SuppressWarnings("serial")
@SlingServlet(
	    paths={"/dotcom/lightstreamservice"}
	)
@Properties({
@Property(name="service.description",value="This servlet returns the data from Lighstream API", propertyPrivate=false) 
})

/**
* The LightStreamService class extends the SlingSafeMethodsServlet object 
* which return the response from the Map Quest URL API.
*
* @author  Jagan Mohan Rao Y
* @version 1.0
* @since   2017-09-27 
*/
public class LightStreamService extends SlingAllMethodsServlet
{
	/**SuntrustDotcomService Class Reference*/
	@Reference
	private SuntrustDotcomService dotcomService;
	/** API URL*/
	private String apiurl = "https://testapi.lightstream.com/rates?APIKey=TEST"; 
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LightStreamService.class);   
	 /**
     *  doGet method gets called when the requests originates from
     *  Ajax call to get the minimum and maximum rates    
     *  @param  request object
     *  @param  response object
     *  @return Nothing. 
     */
	@Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException
    {
		BufferedReader reader = null;
		StringBuffer lsResponse = new StringBuffer();
    	try {
			String url = StringUtils.isNotBlank(dotcomService.getPropertyValue("lightstream.api.url")) ? dotcomService.getPropertyValue("lightstream.api.url") : apiurl;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// optional default is GET
			con.setRequestMethod("GET");
			reader = new BufferedReader(new InputStreamReader(con.getInputStream())); 
			String inputLine;
			while ((inputLine = reader.readLine()) != null) {
				lsResponse.append(inputLine);
			}
		}catch(Exception e){
			LOGGER.error("Exception in lightstream api service. Message: {}, Trace: {}",e.getMessage(),e);
		}
		finally {
			if(reader != null)
			{
				reader.close();
			}
		}
		response.getWriter().println(lsResponse);
	}
    
}
 
