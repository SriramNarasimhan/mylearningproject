package com.suntrust.dotcom.workflow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import com.suntrust.dotcom.config.STELConfigService;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.scheduler.DBConnectionManager;
import com.suntrust.dotcom.services.EmailService;
import com.suntrust.dotcom.utils.AWSUtils;
import com.suntrust.dotcom.utils.DatabaseUtils;
import com.suntrust.dotcom.utils.RPXUtils;
import com.suntrust.dotcom.utils.Utils;

/**
 * This class is used to sync up the rates content from RPX feed into stage and
 * prod tables
 */
public class RPXSyncHelper {
	/** Logger variable */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RPXSyncHelper.class);
	/** ResourceResolver class reference variable */
	private ResourceResolver resourceResolver = null;
	/** Session class reference variable */
	private Session session = null;
	/** List variable to hold product codes */
	private List<String> productCodeList;
	/** Integer variable to store record count */
	private int jsonRecordsCount = 0;
	/** Integer variable to store gbl duplicate records count */
	private int gblDuplicateRecordsCount = 0;
	/**
	 * This method loads the RPX data into database tables
	 * 
	 * @param dataSourcePool
	 * @param stelConfigService
	 * @param emailService
	 * @param resourceResolverFactory
	 * @param dotcomService
	 * @param rpxEnvironment
	 * @return boolean
	 */
	public boolean loadRPXRates(DataSourcePool dataSourcePool,
			STELConfigService stelConfigService, EmailService emailService,
			ResourceResolverFactory resourceResolverFactory,
			SuntrustDotcomService dotcomService, String rpxServletUrl,
			String deleteQry, String insertSpStatement,
			String recordCountSelectQry) {
		// Initialize the objects and variables to clear if any values assigned
		// by other execution.
		jsonRecordsCount = 0;
		gblDuplicateRecordsCount = 0;

		DBConnectionManager connectionManager = null;
		DatabaseUtils databaseUtils = null;
		Connection connection = null;
		Statement statement = null;
		try {
			productCodeList = stelConfigService
					.getPropertyArray("rpx.product.codes");
			connectionManager = new DBConnectionManager(dataSourcePool);
			databaseUtils = new DatabaseUtils();
			connection = connectionManager.getConnection();
			LOGGER.debug("Second connection>"+connection);
			if (rpxServletUrl != null && connection != null
					&& productCodeList.isEmpty() == false
					&& rpxServletUrl.isEmpty() == false
					&& isServletUrlsAccessible(rpxServletUrl)) {
				statement = connection.createStatement();
				databaseUtils.executeQuery(deleteQry, statement, connection);
				String productCode = null;
				Iterator<String> productCodesItr = productCodeList.iterator();
				while (productCodesItr.hasNext()) {
					productCode = productCodesItr.next();
					// Example Servlet path for
					// STUFPSL1:https://www.alternativeloan.com/RPX/RPXServlet?linkId=STUFPSL1
					URLConnection urlConnection = getConnection(rpxServletUrl
							+ "?linkId=" + productCode);
					if (urlConnection != null) {
						loadRPXRatesByProduct(urlConnection, productCode,
								stelConfigService, databaseUtils, connection,
								insertSpStatement);
					} else {
						return false;
					}
				}

			} else {
				LOGGER.error("Required parmeters are missing or issues in connecting to database");
				return false;
			}
			LOGGER.debug(" Table records count :"
					+ databaseUtils.getRecordCount(recordCountSelectQry,
							statement));
			LOGGER.debug(" RPX JSON records count :" + jsonRecordsCount / 2);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			return false;
		} finally {
			databaseUtils.closeStatement(statement);
			databaseUtils.closeConnection(connection);
		}
		return true;
	}

	/**
	 * This method loads the RPX data into database tables through scheduler
	 * 
	 * @param dataSourcePool
	 * @param stelConfigService
	 * @param emailService
	 * @param resourceResolverFactory
	 * @param dotcomService
	 * @return boolean
	 */
	public boolean loadRPXProdRates(DataSourcePool dataSourcePool,
			STELConfigService stelConfigService, EmailService emailService,
			ResourceResolverFactory resourceResolverFactory,
			SuntrustDotcomService dotcomService) {
		int tempRecordsCount = 0;
		int prodRecordsCount = 0;
		DBConnectionManager connectionManager = null;
		DatabaseUtils databaseUtils = null;
		Connection connection = null;
		CallableStatement swapCallableStatement = null;
		Statement statement = null;

		String rpxServletUrl = stelConfigService
				.getPropertyValue("rpx.prod.servlet.url");
		String deleteQry = stelConfigService
				.getPropertyValue("rpx.prod.delete.temp.table");
		String insertSpStatement = stelConfigService
				.getPropertyValue("rpx.prod.insert.sp.statement");
		String recordCountSelectQry = stelConfigService
				.getPropertyValue("rpx.prod.temp.record.count");
		try {
			connectionManager = new DBConnectionManager(dataSourcePool);
			databaseUtils = new DatabaseUtils();
			connection = connectionManager.getConnection();
			LOGGER.debug("Connection object >" + connection);
			if (connection == null) {
				LOGGER.debug("Database connection failed");
				return false;
			}
			statement = connection.createStatement();
			databaseUtils.executeQuery(deleteQry, swapCallableStatement,
					connection);
			if (loadRPXRates(dataSourcePool, stelConfigService, emailService,
					resourceResolverFactory, dotcomService, rpxServletUrl,
					deleteQry, insertSpStatement, recordCountSelectQry)) {
				tempRecordsCount = databaseUtils.getRecordCount(
						recordCountSelectQry, statement);
				LOGGER.debug("Production temp table records count:"
						+ tempRecordsCount);
				//JSON records count from RPX structure
				jsonRecordsCount=jsonRecordsCount/ 2;
				//Remove gbl 51 deferment duplicate records count
				jsonRecordsCount=jsonRecordsCount-gblDuplicateRecordsCount;
				LOGGER.debug("Json records count:" + jsonRecordsCount);
				if (tempRecordsCount != 0
						&& tempRecordsCount == jsonRecordsCount) {
					// swap the tables, record count matches with json count
					swapCallableStatement = connection
							.prepareCall(stelConfigService
									.getPropertyValue("rpx.prod.swap.sp.statement"));
					swapCallableStatement.execute();
					connection.commit();
					prodRecordsCount = databaseUtils.getRecordCount(
							stelConfigService
									.getPropertyValue("rpx.prod.record.count"),
							statement);
					LOGGER.debug("Production table records count after RPX swap:"
							+ prodRecordsCount);
				} else {
					LOGGER.debug("Records count not matching");
					return false;
				}
			} else {
				LOGGER.debug("Error in table insertion");
				return false;
			}
		} catch (SQLException e) {
			LOGGER.debug("SQLException " + e.getMessage());
			return false;
		} finally {
			if (statement != null) {
				databaseUtils.closeStatement(statement);
			}
			if (connection != null) {
				databaseUtils.closeConnection(connection);
			}
		}
		return true;
	}

	/**
	 * Method to create SQL batch
	 * 
	 * @param columnValues
	 * @return
	 * @throws SQLException
	 */
	public void addSqlBatch(HashMap<String, String> columnValues,
			Connection connection, CallableStatement insertCallableStatement)
			throws SQLException {
		try {
			insertCallableStatement.setInt(1,
					Integer.parseInt(columnValues.get("productId")));
			insertCallableStatement.setString(2,
					columnValues.get("productCode"));
			insertCallableStatement.setString(3, columnValues.get("repayCode"));
			insertCallableStatement.setString(4, columnValues.get("term"));
			insertCallableStatement.setString(5, columnValues.get("loanType"));
			insertCallableStatement.setString(6,
					columnValues.get("lowestInterestRate"));
			insertCallableStatement.setString(7,
					columnValues.get("highestInterestRate"));
			insertCallableStatement.setInt(8,
					Integer.parseInt(columnValues.get("amountRequestedMin")));
			insertCallableStatement.setInt(9,
					Integer.parseInt(columnValues.get("amountRequestedMax")));
			insertCallableStatement.setString(10, columnValues.get("aprMin"));
			insertCallableStatement.setString(11, columnValues.get("aprMax"));
			insertCallableStatement.setString(12,
					columnValues.get("monthlyPaymentInSchoolMin"));
			insertCallableStatement.setString(13,
					columnValues.get("monthlyPaymentInSchoolMax"));
			insertCallableStatement.setString(14,
					columnValues.get("monthlyRepaymentMin"));
			insertCallableStatement.setString(15,
					columnValues.get("monthlyRepaymentMax"));
			insertCallableStatement.setString(16,
					columnValues.get("monthlyPaymentPostGradMin"));
			insertCallableStatement.setString(17,
					columnValues.get("monthlyPaymentPostGradMax"));
			insertCallableStatement.setInt(18,
					Integer.parseInt(columnValues.get("defermentPeriodMin")));
			insertCallableStatement.setInt(19,
					Integer.parseInt(columnValues.get("defermentPeriodMax")));
			insertCallableStatement.setInt(20,
					Integer.parseInt(columnValues.get("repaymentPeriodMin")));
			insertCallableStatement.setInt(21,
					Integer.parseInt(columnValues.get("repaymentPeriodMax")));
			insertCallableStatement.setString(22,
					columnValues.get("totalPaymentMin"));
			insertCallableStatement.setString(23,
					columnValues.get("totalPaymentMax"));
			insertCallableStatement.setString(24, columnValues.get("baseRate"));
			insertCallableStatement.setString(25,
					columnValues.get("baseRateStartDate"));
			insertCallableStatement.setString(26,
					columnValues.get("intRateEffectiveDateForFixed"));
			insertCallableStatement.setString(27,
					columnValues.get("intRateEffectiveDateForvariable"));
			insertCallableStatement.setString(28,columnValues.get("rateImportDateNTime"));			
			insertCallableStatement.addBatch();
			insertCallableStatement.executeBatch();
			connection.commit();
			insertCallableStatement.clearParameters();
			insertCallableStatement.clearBatch();
		} catch (NumberFormatException e) {
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
			throw new NumberFormatException();
		}
	}

	/**
	 * Method to loads the RPX data by product
	 * 
	 * @param urlConnection
	 * @param productCode
	 * @param stelConfigService
	 * @param sqlQuery
	 * @return boolean
	 */
	public boolean loadRPXRatesByProduct(URLConnection urlConnection,
			String productCode, STELConfigService stelConfigService,
			DatabaseUtils databaseUtils, Connection connection, String sqlQuery) {
		int index = 1;
		String jsonResponseLine = null;
		String baseRate = null;
		String baseRateStartDate = null;
		String intRateEffectiveDateForFixed = null;
		String intRateEffectiveDateForvariable = null;
		CallableStatement insertCallableStatement = null;
		try {
			JSONParser parser = new JSONParser();

			if (urlConnection != null) {
				urlConnection.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						urlConnection.getInputStream()));
				insertCallableStatement = connection.prepareCall(sqlQuery);
				while ((jsonResponseLine = in.readLine()) != null) {
					if (jsonResponseLine.contains("(")) {
						jsonResponseLine = jsonResponseLine.substring(5);
					}
					if (jsonResponseLine.contains(")")) {
						jsonResponseLine = jsonResponseLine.substring(0,
								jsonResponseLine.indexOf(")"));
					}
					Object obj = parser.parse(jsonResponseLine);
					JSONObject jsonObject = (JSONObject) obj;
					JSONObject commonPricingDataJson = (JSONObject) jsonObject
							.get("commonPricingData");

					baseRate = commonPricingDataJson.get("baseRate").toString();
					baseRateStartDate = commonPricingDataJson.get(
							"baseRateStartDate").toString();
					intRateEffectiveDateForFixed = commonPricingDataJson.get(
							"intRateEffectiveDateForFixed").toString();
					intRateEffectiveDateForvariable = commonPricingDataJson
							.get("intRateEffectiveDateForFixed").toString();

					JSONArray rpxCalcRespDTOList = (JSONArray) jsonObject
							.get("rpxCalcRespDTOList");

					Iterator<JSONObject> rpxCalcRespDTOListItr = rpxCalcRespDTOList
							.iterator();
				
					HashMap<String, String> columnValues = null;
					while (rpxCalcRespDTOListItr.hasNext()) {
						JSONObject childObj = rpxCalcRespDTOListItr.next();
						if (connection != null) {
							if (index % 2 != 0) {
								columnValues = new HashMap<String, String>();
								columnValues.put("productCode", productCode);
								columnValues.put("rateImportDateNTime",RPXUtils.getRPXImportDateNTitme(stelConfigService.getPropertyValue("rpx.prod.import.date.pattern"), stelConfigService.getPropertyValue("rpx.prod.import.time.pattern")));
								columnValues.put("baseRate", baseRate);
								columnValues.put("baseRateStartDate",
										baseRateStartDate);
								columnValues.put(
										"intRateEffectiveDateForFixed",
										intRateEffectiveDateForFixed);
								columnValues.put(
										"intRateEffectiveDateForvariable",
										intRateEffectiveDateForvariable);
								columnValues.put("productId",
										childObj.get("bptId").toString());
								columnValues.put("repayCode",
										childObj.get("calcType").toString());
								columnValues.put("term",
										childObj.get("monthsInRepayment")
												.toString());
								columnValues.put("loanType",
										childObj.get("rateIndexCode")
												.toString());
								columnValues.put("lowestInterestRate", childObj
										.get("borrDeferIntRate").toString());
								columnValues.put("amountRequestedMin", childObj
										.get("principalFinalDisbursement")
										.toString());
								columnValues.put("aprMin", childObj.get("apr")
										.toString());
								columnValues.put("monthlyPaymentInSchoolMin",
										childObj.get("monthlyPmtPreGrad")
												.toString());
								columnValues.put("monthlyRepaymentMin",
										childObj.get("monthlyPmtEstimated")
												.toString());
								columnValues.put("monthlyPaymentPostGradMin",
										childObj.get("monthlyPmtPostGrad")
												.toString());
								columnValues.put("defermentPeriodMin", childObj
										.get("monthsInDeferment").toString());
								columnValues.put("repaymentPeriodMin", childObj
										.get("monthsInRepayment").toString());
								columnValues.put("totalPaymentMin", childObj
										.get("totalPmtAmt").toString());
							} else {
								columnValues.put("highestInterestRate",
										childObj.get("borrDeferIntRate")
												.toString());
								columnValues.put("amountRequestedMax", childObj
										.get("principalFinalDisbursement")
										.toString());
								columnValues.put("aprMax", childObj.get("apr")
										.toString());
								columnValues.put("monthlyPaymentInSchoolMax",
										childObj.get("monthlyPmtPreGrad")
												.toString());
								columnValues.put("monthlyRepaymentMax",
										childObj.get("monthlyPmtEstimated")
												.toString());
								columnValues.put("monthlyPaymentPostGradMax",
										childObj.get("monthlyPmtPostGrad")
												.toString());
								columnValues.put("defermentPeriodMax", childObj
										.get("monthsInDeferment").toString());
								columnValues.put("repaymentPeriodMax", childObj
										.get("monthsInRepayment").toString());
								columnValues.put("totalPaymentMax", childObj
										.get("totalPmtAmt").toString());
								if(removeDuplicatesGBL51DefRecords(columnValues)){								
									addSqlBatch(columnValues, connection,
											insertCallableStatement);
									// clear hash map to avoid duplicates
									columnValues.clear();
								}else{
									LOGGER.info("gblDuplicateRecordsCount >>"+gblDuplicateRecordsCount+">>"+"product name"+productCode+columnValues.get("defermentPeriodMax"));
									gblDuplicateRecordsCount++;
								}								
							}
							index++;
						}
						jsonRecordsCount++;						
					}
				}
			}
		} catch (MalformedURLException e) {
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
			return false;
		} catch (IOException e) {
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
			return false;
		} catch (ParseException e) {
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
			return false;
		} catch (SQLException e) {
			LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
			return false;
		} finally {
			databaseUtils.closeStatement(insertCallableStatement);
		}
		return true;
	}

	/**
	 * Method to get connection object
	 * 
	 * @param rpxWebServiceUrl
	 * @return urlConnection
	 */
	public URLConnection getConnection(String rpxWebServiceUrl) {

		URLConnection urlConnection = null;
		try {
			URL rpxProductUrl = new URL(rpxWebServiceUrl);
			urlConnection = rpxProductUrl.openConnection();
			if (urlConnection instanceof HttpURLConnection
					&& ((HttpURLConnection) urlConnection).getResponseCode() == 200
					&& urlConnection.getContentLength() != 0) {
				return urlConnection;
			} else {
				LOGGER.error("Error connecting to servlet url "
						+ rpxWebServiceUrl);
				return null;
			}
		} catch (MalformedURLException e) {
			LOGGER.error(
					"Exception in getConnection {} TRACE: {}" + e.getMessage(),
					e);
			return null;
		} catch (IOException e) {
			LOGGER.error(
					"Exception in getConnection {} TRACE: {}" + e.getMessage(),
					e);
			return null;
		}
	}

	/**
	 * Method to get servlet urls are accessible or not
	 * 
	 * @param rpxServletUrl
	 * @return boolean
	 */
	public boolean isServletUrlsAccessible(String rpxServletUrl) {
		String productCode = null;
		URLConnection urlConnection = null;
		boolean isUrlAccessible = true;
		Iterator<String> productCodeItr = productCodeList.iterator();
		while (productCodeItr.hasNext()) {
			productCode = productCodeItr.next();
			// Example Servlet path for
			// STUFPSL1:https://www.alternativeloan.com/RPX/RPXServlet?linkId=STUFPSL1
			urlConnection = getConnection(rpxServletUrl + "?linkId="
					+ productCode);
			if (urlConnection == null && isUrlAccessible) {
				LOGGER.debug("Url Connection object is null");
				isUrlAccessible = false;
			}
		}
		return isUrlAccessible;
	}

	/**
	 * Method to flush the dispatcher and AWS cache
	 * 
	 * @param dotcomService
	 * @param resourceResolverFactory
	 * @return boolean
	 */
	public boolean flushCache(SuntrustDotcomService dotcomService,
			ResourceResolverFactory resourceResolverFactory) {
		AWSUtils awsUtils = new AWSUtils();
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);
			//int awsflushlimit = 10;
			//long awsflushwaittime = 180000;
			Set<String> awsflushpath = new HashSet<String>();
			Set<String> dispatcherflushpath = new HashSet<String>();
			List<String> canonicalUrl = dotcomService
					.getPropertyArray("canonical.urls");
			LOGGER.debug("Canonical URL >"+Utils.getCanonicalUrl(canonicalUrl,dotcomService.getPropertyValue("rpx.paymentstable.servlet.config.root"),
					resourceResolver));
			awsflushpath.add(Utils.getCanonicalUrl(canonicalUrl,dotcomService.getPropertyValue("rpx.paymentstable.servlet.config.root"),
					resourceResolver));
			dispatcherflushpath
					.add(dotcomService.getPropertyValue("rpx.paymentstable.servlet.config.root"));
			// flush dispatcher cache
			awsUtils.flushDispatcher(dispatcherflushpath, "Activate",
					dotcomService);
			LOGGER.info("flushDispatcher done");
			// flush aws cache
			AWSUtils.flushAWSCache(awsflushpath, dotcomService);
			LOGGER.info("flushAWSCache done");
		} catch (LoginException e) {
			LOGGER.error(
					"Exception in getConnection {} TRACE: {}" + e.getMessage(),
					e);
			return false;
		}
		return true;
	}
	/**
	 * Method to initiate the workflow
	 * 
	 * @param wfService
	 * @param resolver
	 * @param workflowModelPath
	 * @param payloadPath
	 * @return 
	 */

	public void workflowInitiator(WorkflowService wfService,
			ResourceResolver resolver,String workflowModelPath, String payloadPath) {
		this.resourceResolver = resolver;
		this.session = resolver.adaptTo(Session.class);
		WorkflowSession wfSession = wfService.getWorkflowSession(this.session);		
		try {
			if(wfSession !=null && workflowModelPath.isEmpty()==false && payloadPath.isEmpty()==false){
			WorkflowModel model;			
			model = wfSession
					.getModel(workflowModelPath);			
			WorkflowData data = wfSession.newWorkflowData("JCR_PATH",payloadPath);
			Map<String, Object> metaData = new HashMap<String, Object>();
			wfSession.startWorkflow(model, data, metaData);
			}else{
				LOGGER.error("Required parameters are missing to initiate the workflow");
			}
		} catch (WorkflowException e) {
			LOGGER.error("WorkflowException occurred {} TRACE: {}" + e.getMessage(), e);
		}
	}
	/**
	 * Method to remove GBL deferment 51 period duplicates
	 * @param columnValues
	 * @return boolean
	 */
	private boolean removeDuplicatesGBL51DefRecords(HashMap<String, String> columnValues){
		if(columnValues.get("defermentPeriodMin").equalsIgnoreCase("51")){
			if(columnValues.get("productCode").equalsIgnoreCase("STGBL1")){
				return false;
			}else{
				return true;
			}
		}
		else{
			return true;
		}
	}
}
