package com.suntrust.dotcom.components;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.config.SuntrustDotcomService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;

import org.apache.sling.api.SlingHttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.suntrust.dotcom.scheduler.DBConnectionManager;


public class RatesComponent extends WCMUsePojo {
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RatesComponent.class);

	/** String variable to store zip code provided in component */
	private String zipCode = null;
	
	/** String variable to store rates type based on component */
	private String ratesType = null;
	
	/** DBConnectionManager class reference variable */
	DBConnectionManager connectionManager = null;
	
	/** Connection class reference variable */
	Connection connection = null;
	
	/** Statement class reference variable */
	Statement statement = null;
	
	/** SlingHttpServletResponse class reference variable */
	SlingHttpServletResponse response = null;
	
	/** SuntrustDotcomService class reference variable */
	SuntrustDotcomService dotcomService = null;

	@Override
	public void activate(){
		try {
			response=getResponse();
			String[] selectors=getRequest().getRequestPathInfo().getSelectors();
			if(selectors != null && selectors.length == 2)
			{
				for (int i = 0; i < selectors.length; i++) {
					LOGGER.debug("seelctor" + selectors[i]);
					ratesType = selectors[0];
					zipCode = selectors[1];
				}
				DataSourcePool dataSourcePool= getSlingScriptHelper().getService(DataSourcePool.class);
				connectionManager=new DBConnectionManager(dataSourcePool);
				connection = connectionManager.getConnection();
				dotcomService = getSlingScriptHelper().getService(SuntrustDotcomService.class);
			}  else {
				ratesType = "error";
				zipCode = "error";
			}
		} catch (Exception e){
			LOGGER.error("Exception captured: ",e);
		}
	}

	/**
	 * Return rates for give zip code if present ijn table or returned response
	 * as error
	 * 
	 * @return
	 */
	public String getJSONAsResponse(){
		response.setContentType("application/json");
		JSONObject jsonResponse = new JSONObject();
		if("error".equalsIgnoreCase(ratesType) || "error".equalsIgnoreCase(zipCode))
		{
			jsonResponse.put("status", "failure");
			return jsonResponse.toJSONString();
		}
		int count = 0;
		ResultSet resultSet = null;
		String query = "";
		try {
			String tablename = dotcomService.getPropertyValue(ratesType.trim()+".tablename");
			//invalid selector
			if(StringUtils.isBlank(tablename))
			{
				jsonResponse.put("status", "failure");
				return jsonResponse.toJSONString();
			}
			statement = connection.createStatement();
			if("cdrates".equalsIgnoreCase(ratesType)) {
				query = "select * from " + tablename + " where zipcode=" + zipCode + " order by apr_apy_value desc";
			}
			else
			{
				query = "select * from " + tablename + " where product_eligibility=" + zipCode;
			}
			LOGGER.debug("Query: "+ query);
			resultSet = statement.executeQuery(query);
			JSONArray tablerow = new JSONArray();
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			while (resultSet.next()) {
				/*if(count == 0){
					jsonResponse.put("status", "success");
					count = count + 1;
				}*/
				count = count + 1;
				JSONArray jsonArray=new JSONArray();
				if("cdrates".equalsIgnoreCase(ratesType)) {
					jsonArray.add(resultSet.getString("terms"));
					jsonArray.add(decimalFormat.format(resultSet.getDouble("rate")));
					jsonArray.add(decimalFormat.format(resultSet.getDouble("apr_apy_value")));
				}
				else
				{
					jsonArray.add(String.valueOf(resultSet.getDouble("apr_apy_value")));
					jsonArray.add(resultSet.getString("terms"));
				}
				tablerow.add(jsonArray);
			}

			if(count==0){
				jsonResponse.put("status", "failure");
			} else {
				jsonResponse.put("status", "success");
				jsonResponse.put("tablerow",tablerow);
			}
		} catch (Exception e) {
			LOGGER.error("Error in sql operations", e);
		}
		finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
				if (connection != null) {
					connection.close();
				}
			}catch (SQLException sqle)
			{
				LOGGER.error("Error in sql operations", sqle);
			}
		}
		LOGGER.debug("Response returned: "+ jsonResponse.toString());
		return jsonResponse.toString();
	}

}