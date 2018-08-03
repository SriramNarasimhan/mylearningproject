package com.suntrust.dotcom.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.suntrust.dotcom.config.STELConfigService;
import com.suntrust.dotcom.scheduler.DBConnectionManager;


/**
* This PaymentsTableImpl
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
*/
@SuppressWarnings("serial")
@SlingServlet(resourceTypes = "/apps/dotcom/components/page/paymentstabletemplate" ,extensions="json")
public class PaymentsTableImpl extends SlingSafeMethodsServlet {
	/**	Service variable to resolve resources * */
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	@Reference 
	DataSourcePool dataSourcePool;
	@Reference
	private STELConfigService stelService;
	/** DBConnectionManager class reference variable */
	DBConnectionManager connectionManager = null;	
	/** Connection class reference variable */
	Connection connection = null;	
	/** Statement class reference variable */
	Statement statement = null;
	
	/**	Logger variable to log program state * */ 
	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentsTableImpl.class);
	/** 
     * To handle HTTP GET request
    */
    @Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response) {
    	
    	JSONObject json = new JSONObject();
		response.setContentType("json"); 
		response.setCharacterEncoding("UTF-8");
		int count = 0;
		ResultSet resultSet = null;
		String query = "";
		try {
			LOGGER.info("servlet start");
			connectionManager=new DBConnectionManager(dataSourcePool);			
			connection = connectionManager.getConnection();			
			statement = connection.createStatement();
			String tablename = stelService.getPropertyValue("rpx.payments.rates.tablename");
			//query = "select * from "+tablename ;	
			query = "select product_id,product_code,repay_code,term,loan_type,cast(lowest_interest_rate as numeric(5,3)) as lowest_interest_rate,cast(highest_interest_rate as numeric(5,3)) highest_interest_rate,amount_requested_min,amount_requested_max,cast(apr_min as numeric(5,3)) as apr_min,cast(apr_max as numeric(5,3)) as apr_max,cast(monthly_payment_in_school_min as numeric(5,2)) as monthly_payment_in_school_min,cast(monthly_payment_in_school_max as numeric(5,2)) as monthly_payment_in_school_max,cast(monthly_repayment_min as numeric(5,2)) as monthly_repayment_min,cast(monthly_repayment_max as numeric(5,2)) as monthly_repayment_max,cast(monthly_payment_post_grad_min as numeric(5,2)) as monthly_payment_post_grad_min,cast(monthly_payment_post_grad_max as numeric(5,2)) as monthly_payment_post_grad_max,deferment_period_min,deferment_period_max,repayment_period_min,repayment_period_max,cast(total_payment_min as numeric(12,2)) as total_payment_min,cast(total_payment_max as numeric(12,2)) as total_payment_max,cast(base_rate as numeric(5,3)) as base_rate,base_rate_start_date,intrate_effective_date_for_fixed,intrate_effective_date_for_variable,rate_effective_date_time from "+tablename;
			
			//query = "select * from tbl_prod_rpx_rates";	
			LOGGER.info("Query: "+ query);
			resultSet = statement.executeQuery(query);			
			JSONObject jsonObjectP1 = new JSONObject();
			JSONObject jsonObjectP2 = new JSONObject();
			JSONObject jsonObjectP3 = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			List<String> productCode = new ArrayList<String>();
			List<String> loanType = new ArrayList<String>();
			Map<String,String> prodLoanMap = new HashMap<String,String>();
			SimpleDateFormat df = new SimpleDateFormat(stelService.getPropertyValue("rpx.prod.import.date.pattern"));
			Date tabledate = null;			
			while (resultSet.next()) {				
				count = count + 1;				
				JSONObject jsonObjecttemp = new JSONObject();
				JSONObject jsonObjectcommons = new JSONObject();
				if(!(productCode.contains(resultSet.getString("product_code")))){
					productCode.add(resultSet.getString("product_code"));
				}
				if(!(loanType.contains(resultSet.getString("loan_type")))){
					loanType.add(resultSet.getString("loan_type"));
				}
				jsonArray= new JSONArray();
				jsonArray.put(resultSet.getString("amount_requested_min"));
				jsonArray.put(resultSet.getString("amount_requested_max"));				
				jsonObjecttemp.put("loan_amount",jsonArray);
				jsonArray= new JSONArray();
				jsonArray.put(resultSet.getString("lowest_interest_rate"));
				jsonArray.put(resultSet.getString("highest_interest_rate"));				
				jsonObjecttemp.put("current_interest_rate",jsonArray);
				jsonArray= new JSONArray();
				jsonArray.put(resultSet.getString("apr_min"));
				jsonArray.put(resultSet.getString("apr_max"));				
				jsonObjecttemp.put("apr",jsonArray);
				jsonArray= new JSONArray();
				jsonArray.put(resultSet.getString("monthly_payment_in_school_min"));
				jsonArray.put(resultSet.getString("monthly_payment_in_school_max"));				
				jsonObjecttemp.put("monthly_payment_while_in_school",jsonArray);
				jsonArray= new JSONArray();
				jsonArray.put(resultSet.getString("monthly_repayment_min"));
				jsonArray.put(resultSet.getString("monthly_repayment_max"));				
				jsonObjecttemp.put("monthly_payment_during_payment",jsonArray);
				jsonArray= new JSONArray();
				jsonArray.put(resultSet.getString("deferment_period_min"));
				jsonArray.put(resultSet.getString("deferment_period_max"));				
				jsonObjecttemp.put("deferment_period",jsonArray);
				jsonArray= new JSONArray();
				jsonArray.put(resultSet.getString("repayment_period_min"));
				jsonArray.put(resultSet.getString("repayment_period_max"));				
				jsonObjecttemp.put("repayment_period",jsonArray);
				jsonArray= new JSONArray();
				jsonArray.put(resultSet.getString("total_payment_min"));
				jsonArray.put(resultSet.getString("total_payment_max"));				
				jsonObjecttemp.put("total_repayment_amount",jsonArray);	
				jsonArray= new JSONArray();
				jsonArray.put(resultSet.getString("base_rate"));								
				jsonObjectcommons.put("base_rate",jsonArray);
				jsonArray= new JSONArray();
				tabledate=new SimpleDateFormat(stelService.getPropertyValue("rpx.rate.prod.import.date.pattern")).parse(resultSet.getString("base_rate_start_date"));		
				jsonArray.put(df.format(tabledate));								
				jsonObjectcommons.put("base_rate_start_date",jsonArray);
				jsonArray= new JSONArray();
				tabledate=new SimpleDateFormat(stelService.getPropertyValue("rpx.rate.prod.import.date.pattern")).parse(resultSet.getString("intrate_effective_date_for_fixed"));
				jsonArray.put(df.format(tabledate));								
				jsonObjectcommons.put("intrate_effective_date_for_fixed",jsonArray);
				jsonArray= new JSONArray();
				tabledate=new SimpleDateFormat(stelService.getPropertyValue("rpx.rate.prod.import.date.pattern")).parse(resultSet.getString("intrate_effective_date_for_variable"));
				jsonArray.put(df.format(tabledate));												
				jsonObjectcommons.put("intrate_effective_date_for_variable",jsonArray);
				jsonArray= new JSONArray();
				jsonArray.put(resultSet.getString("rate_effective_date_time"));								
				jsonObjectcommons.put("rate_effective_date_time",jsonArray);
				String jsonObjKey=resultSet.getString("loan_type")+"_"+resultSet.getString("term")+"_"+resultSet.getString("repay_code");
				if(productCode.indexOf(resultSet.getString("product_code"))==0){
					jsonObjectP1.put("commons",jsonObjectcommons);
					findDuplicateRows(jsonObjKey,jsonObjectP1,jsonObjecttemp);
					json.put(resultSet.getString("product_code"), jsonObjectP1);
				}else if(productCode.indexOf(resultSet.getString("product_code"))==1){
					jsonObjectP2.put("commons",jsonObjectcommons);
					findDuplicateRows(jsonObjKey,jsonObjectP2,jsonObjecttemp);
					json.put(resultSet.getString("product_code"), jsonObjectP2);
				}else{
					jsonObjectP3.put("commons",jsonObjectcommons);
					findDuplicateRows(jsonObjKey,jsonObjectP3,jsonObjecttemp);
					json.put(resultSet.getString("product_code"), jsonObjectP3);
				}
				prodLoanMap.put(jsonObjKey,resultSet.getString("product_code"));
				
			}
			
			getLoanRates(json, prodLoanMap, productCode);
			
			response.getWriter().write(json.toString());
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(), unsupportedEncodingException);
		} catch (IOException iOException) {
			LOGGER.error(iOException.getMessage(), iOException);
		} catch (JSONException jsonException) {
			LOGGER.error(jsonException.getMessage(), jsonException);
		}catch (Exception e) {
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
    } 
    
    private void getLoanRates(JSONObject json, Map<String, String> prodLoanMap, List<String> productCode) throws JSONException {
    	
		JSONArray jsonArray = new JSONArray();
		double minVal_apr_fixed_all = 1000.000, minVal_apr_var_all = 1000.000;
		double maxVal_apr_fixed_all = 0.000, maxVal_apr_var_all = 0.000;
		double minVal_interest_rate_fixed_all = 1000.000, minVal_interest_rate_var_all = 1000.000;
		double maxVal_interest_rate_fixed_all = 0.000, maxVal_interest_rate_var_all = 0.000;
		for(String product : productCode) {
			double minVal_apr_fixed = 1000.000,minVal_apr_var=1000.000;
			double maxVal_apr_fixed=0.000,maxVal_apr_var=0.000;
			double minVal_interest_rate_fixed = 1000.000,minVal_interest_rate_var=1000.000;
			double maxVal_interest_rate_fixed=0.000,maxVal_interest_rate_var=0.000;
			JSONObject productArr = json.getJSONObject(product);
			for (Map.Entry<String, String> prodLoan : prodLoanMap.entrySet())
			{
				if (productArr.has(prodLoan.getKey()) && productArr.get(prodLoan.getKey()) instanceof JSONObject) {
					JSONObject loanInfo = (JSONObject) productArr.get(prodLoan.getKey());
					String apr = "apr";
					String current_interest_rate = "current_interest_rate";
					JSONObject result = productArr.getJSONObject("commons");
					result.put("libor_date", getFirstDateOfCurrentMonth());
					if(StringUtils.containsIgnoreCase(prodLoan.getKey(),"fixed")) {
						if(loanInfo.has(current_interest_rate)) {
				    		double intRateMin = loanInfo.getJSONArray(current_interest_rate).getDouble(0);
				    		double intRateMax = loanInfo.getJSONArray(current_interest_rate).getDouble(1);
				    		if(intRateMin<minVal_interest_rate_fixed)
				    			minVal_interest_rate_fixed = intRateMin;
				    		if(intRateMax>maxVal_interest_rate_fixed)
				    			maxVal_interest_rate_fixed = intRateMax;
				    		jsonArray= new JSONArray();
							jsonArray.put(formatDecimalPattern(minVal_interest_rate_fixed));
							jsonArray.put(formatDecimalPattern(maxVal_interest_rate_fixed));				
							result.put("current_interest_rate_fixed_range", jsonArray);
							if(minVal_interest_rate_fixed < minVal_interest_rate_fixed_all)
								minVal_interest_rate_fixed_all = minVal_interest_rate_fixed;
							if(maxVal_interest_rate_fixed > maxVal_interest_rate_fixed_all)
								maxVal_interest_rate_fixed_all = maxVal_interest_rate_fixed;
							jsonArray= new JSONArray();
							jsonArray.put(formatDecimalPattern(minVal_interest_rate_fixed_all));
							jsonArray.put(formatDecimalPattern(maxVal_interest_rate_fixed_all));
							result.put("current_interest_rate_fixed_range_all", jsonArray);
				    	}
						if(loanInfo.has(apr)) {
				    		double aprMin = loanInfo.getJSONArray(apr).getDouble(0);
				    		double aprMax = loanInfo.getJSONArray(apr).getDouble(1);
				    		if(aprMin<minVal_apr_fixed)
				    			minVal_apr_fixed = aprMin;
				    		if(aprMax>maxVal_apr_fixed)
				    			maxVal_apr_fixed = aprMax;
				    		jsonArray= new JSONArray();
							jsonArray.put(formatDecimalPattern(minVal_apr_fixed));
							jsonArray.put(formatDecimalPattern(maxVal_apr_fixed));				
							result.put("apr_fixed_range", jsonArray);
							if(minVal_apr_fixed < minVal_apr_fixed_all)
								minVal_apr_fixed_all = minVal_apr_fixed;
							if(maxVal_apr_fixed > maxVal_apr_fixed_all)
								maxVal_apr_fixed_all = maxVal_apr_fixed;
							jsonArray= new JSONArray();
							jsonArray.put(formatDecimalPattern(minVal_apr_fixed_all));
							jsonArray.put(formatDecimalPattern(maxVal_apr_fixed_all));
							result.put("apr_fixed_range_all", jsonArray);
				    	}
					}
					if(StringUtils.containsIgnoreCase(prodLoan.getKey(),"libor")) {
						if(loanInfo.has(current_interest_rate)) {
							double intRateMin = loanInfo.getJSONArray(current_interest_rate).getDouble(0);
				    		double intRateMax = loanInfo.getJSONArray(current_interest_rate).getDouble(1);
				    		if(intRateMin<minVal_interest_rate_var)
				    			minVal_interest_rate_var = intRateMin;
				    		if(intRateMax>maxVal_interest_rate_var)
				    			maxVal_interest_rate_var = intRateMax;
				    		jsonArray= new JSONArray();
							jsonArray.put(formatDecimalPattern(minVal_interest_rate_var));
							jsonArray.put(formatDecimalPattern(maxVal_interest_rate_var));				
							result.put("current_interest_rate_variable_range", jsonArray);
							if(minVal_interest_rate_var < minVal_interest_rate_var_all)
								minVal_interest_rate_var_all = minVal_interest_rate_var;
							if(maxVal_interest_rate_var > maxVal_interest_rate_var_all)
								maxVal_interest_rate_var_all = maxVal_interest_rate_var;
							jsonArray= new JSONArray();
							jsonArray.put(formatDecimalPattern(minVal_interest_rate_var_all));
							jsonArray.put(formatDecimalPattern(maxVal_interest_rate_var_all));
							result.put("current_interest_rate_variable_range_all", jsonArray);
				    	}
						if(loanInfo.has(apr)) {
				    		double aprMin = loanInfo.getJSONArray(apr).getDouble(0);
				    		double aprMax = loanInfo.getJSONArray(apr).getDouble(1);
				    		if(aprMin<minVal_apr_var)
				    			minVal_apr_var = aprMin;
				    		if(aprMax>maxVal_apr_var)
				    			maxVal_apr_var = aprMax;
				    		jsonArray= new JSONArray();
							jsonArray.put(formatDecimalPattern(minVal_apr_var));
							jsonArray.put(formatDecimalPattern(maxVal_apr_var));				
							result.put("apr_variable_range", jsonArray);
							if(minVal_apr_var < minVal_apr_var_all)
								minVal_apr_var_all = minVal_apr_var;
							if(maxVal_apr_var > maxVal_apr_var_all)
								maxVal_apr_var_all = maxVal_apr_var;
							jsonArray= new JSONArray();
							jsonArray.put(formatDecimalPattern(minVal_apr_var_all));
							jsonArray.put(formatDecimalPattern(maxVal_apr_var_all));
							result.put("apr_variable_range_all", jsonArray);
				    	}
					}
				}
			}
		}
	}
    
    private String getFirstDateOfCurrentMonth() {
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
    	return new SimpleDateFormat(stelService.getPropertyValue("rpx.prod.import.date.pattern")).format(cal.getTime());
	}

	/**
     * @param jsonObjKey
     * @param jsonObject
     * @param jsonObjecttemp
     * @return void
     */
    public void findDuplicateRows(String jsonObjKey, JSONObject jsonObject, JSONObject jsonObjecttemp){
    	try {
	    	if(jsonObject.has(jsonObjKey)){
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("loan_amount").get(0))>Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("loan_amount").get(0))){
	    			jsonObjecttemp.getJSONArray("loan_amount").put(0, jsonObject.getJSONObject(jsonObjKey).getJSONArray("loan_amount").get(0));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("loan_amount").get(1))<Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("loan_amount").get(1))){
	    			jsonObjecttemp.getJSONArray("loan_amount").put(1, jsonObject.getJSONObject(jsonObjKey).getJSONArray("loan_amount").get(1));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("current_interest_rate").get(0))>Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("current_interest_rate").get(0))){
	    			jsonObjecttemp.getJSONArray("current_interest_rate").put(0, jsonObject.getJSONObject(jsonObjKey).getJSONArray("current_interest_rate").get(0));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("current_interest_rate").get(1))<Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("current_interest_rate").get(1))){
	    			jsonObjecttemp.getJSONArray("current_interest_rate").put(1, jsonObject.getJSONObject(jsonObjKey).getJSONArray("current_interest_rate").get(1));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("apr").get(0))>Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("apr").get(0))){
	    			jsonObjecttemp.getJSONArray("apr").put(0, jsonObject.getJSONObject(jsonObjKey).getJSONArray("apr").get(0));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("apr").get(1))<Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("apr").get(1))){
	    			jsonObjecttemp.getJSONArray("apr").put(1, jsonObject.getJSONObject(jsonObjKey).getJSONArray("apr").get(1));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("monthly_payment_while_in_school").get(0))>Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("monthly_payment_while_in_school").get(0))){
	    			jsonObjecttemp.getJSONArray("monthly_payment_while_in_school").put(0, jsonObject.getJSONObject(jsonObjKey).getJSONArray("monthly_payment_while_in_school").get(0));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("monthly_payment_while_in_school").get(1))<Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("monthly_payment_while_in_school").get(1))){
	    			jsonObjecttemp.getJSONArray("monthly_payment_while_in_school").put(1, jsonObject.getJSONObject(jsonObjKey).getJSONArray("monthly_payment_while_in_school").get(1));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("monthly_payment_during_payment").get(0))>Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("monthly_payment_during_payment").get(0))){
	    			jsonObjecttemp.getJSONArray("monthly_payment_during_payment").put(0, jsonObject.getJSONObject(jsonObjKey).getJSONArray("monthly_payment_during_payment").get(0));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("monthly_payment_during_payment").get(1))<Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("monthly_payment_during_payment").get(1))){
	    			jsonObjecttemp.getJSONArray("monthly_payment_during_payment").put(1, jsonObject.getJSONObject(jsonObjKey).getJSONArray("monthly_payment_during_payment").get(1));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("deferment_period").get(0))>Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("deferment_period").get(0))){
	    			jsonObjecttemp.getJSONArray("deferment_period").put(0, jsonObject.getJSONObject(jsonObjKey).getJSONArray("deferment_period").get(0));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("deferment_period").get(1))<Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("deferment_period").get(1))){
	    			jsonObjecttemp.getJSONArray("deferment_period").put(1, jsonObject.getJSONObject(jsonObjKey).getJSONArray("deferment_period").get(1));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("repayment_period").get(0))>Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("repayment_period").get(0))){
	    			jsonObjecttemp.getJSONArray("repayment_period").put(0, jsonObject.getJSONObject(jsonObjKey).getJSONArray("repayment_period").get(0));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("repayment_period").get(1))<Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("repayment_period").get(1))){
	    			jsonObjecttemp.getJSONArray("repayment_period").put(1, jsonObject.getJSONObject(jsonObjKey).getJSONArray("repayment_period").get(1));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("total_repayment_amount").get(0))>Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("total_repayment_amount").get(0))){
	    			jsonObjecttemp.getJSONArray("total_repayment_amount").put(0, jsonObject.getJSONObject(jsonObjKey).getJSONArray("total_repayment_amount").get(0));
	    		}
	    		if(Float.valueOf((String) jsonObjecttemp.getJSONArray("total_repayment_amount").get(1))<Float.valueOf((String) jsonObject.getJSONObject(jsonObjKey).getJSONArray("total_repayment_amount").get(1))){
	    			jsonObjecttemp.getJSONArray("total_repayment_amount").put(1, jsonObject.getJSONObject(jsonObjKey).getJSONArray("total_repayment_amount").get(1));
	    		}			
				
			}
	    	jsonObject.put(jsonObjKey,jsonObjecttemp);
    	} catch (JSONException e) {
			LOGGER.error("Error in json", e);
		}
    }     
	/**
	 * Method to get formated decimal pattern
	 * @param value
	 * @return string
	 */		
   	private String formatDecimalPattern(Double value){		
		DecimalFormat df = new DecimalFormat("#####.000");	
		return df.format(value);
	}
}
