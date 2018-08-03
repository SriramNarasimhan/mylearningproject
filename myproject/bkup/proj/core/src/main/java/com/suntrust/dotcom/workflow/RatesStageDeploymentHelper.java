package com.suntrust.dotcom.workflow;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.csvreader.CsvReader;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.scheduler.DBConnectionManager;

public class RatesStageDeploymentHelper{
	DBConnectionManager connectionManager=null;
	private String ratesCsvPayloadPath = "";
	private static final String TYPE_JCR_PATH = "JCR_PATH";
	private Resource resource = null;
	private CsvReader ratesCsv = null;
	private Connection connection = null;
	private CallableStatement callableStatement =null;
	private Statement statement=null;	
	private String stageCDTblQry="delete from tbl_stage_cd_rates";
	private String tempCDTblQry="delete from tbl_prod_temp_cd_rates";
	private String stageEquityTblQry="delete from tbl_stage_equity_rates";
	private String tempEquityTblQry="delete from tbl_prod_temp_Equity_rates";
	ResourceResolver resourceResolver = null;
	
	private static final Logger logger = LoggerFactory
			.getLogger(RatesStageDeploymentHelper.class);
	
	public void loadCDRates(WorkItem workItem, DataSourcePool dataSourcePool,WorkflowSession workflowSession,ResourceResolverFactory resourceResolverFactory,SuntrustDotcomService suntrustDotcomService){
		connectionManager = new DBConnectionManager(dataSourcePool);
		try {
			WorkflowData workflowData = workItem.getWorkflowData();
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);
			if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
				ratesCsvPayloadPath = workflowData.getPayload().toString();
				if (ratesCsvPayloadPath.contains(".csv")) {
					if (suntrustDotcomService != null) {
						ratesCsvPayloadPath = ratesCsvPayloadPath
								+ "/jcr:content/renditions/original/jcr:content";
					}
					logger.debug("Attached CSV path" + ratesCsvPayloadPath);
					if (ratesCsvPayloadPath != ""
							|| ratesCsvPayloadPath != null) {
						resource = resourceResolver
								.getResource(ratesCsvPayloadPath);
						logger.debug("Resource path" + resource.getPath());
					}
					InputStream inputStream = null;
					if (resource != null) {
						Node docNode = resource.adaptTo(Node.class);
						inputStream = docNode.getProperty("jcr:data")
								.getBinary().getStream();
						ratesCsv = new CsvReader(inputStream, ',',
								StandardCharsets.UTF_8);
						ratesCsv.readHeaders();
						connection = connectionManager.getConnection();
						
						statement = connection.createStatement();
						//delete all records from stage table
						executeQuery(stageCDTblQry);
						//delete all records from prod temp table
						executeQuery(tempCDTblQry);
						//load all new cd rates content into prod temp/stage tables
						callableStatement = connection.prepareCall("{call sp_insert_cd_rates(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
						logger.debug("data load begins");
						while (ratesCsv.readRecord() && workflowSession!=null && workItem!=null) {
								String annual_rate_type = ratesCsv
										.get("annual_rate_type");
								String product = ratesCsv.get("product");
								String apr_apy_value = ratesCsv
										.get("apr_apy_value");
								String zipcode = ratesCsv.get("zipcode");
								String region = ratesCsv.get("region");
								String universal_value = ratesCsv
										.get("universal_value");
								String state = ratesCsv.get("state");
								String msa = ratesCsv.get("msa");
								String city = ratesCsv.get("city");
								String rate = ratesCsv.get("rate");
								String min_balance = ratesCsv
										.get("min_balance");
								String max_balance = ratesCsv
										.get("max_balance");
								String terms = ratesCsv.get("Terms");
								if (connection != null) {
									callableStatement.setString(1, annual_rate_type);
									callableStatement.setString(2, product);
									callableStatement.setDouble(3, apr_apy_value.isEmpty() ? 0
											: 100*(Double.parseDouble(apr_apy_value)));
									callableStatement.setString(4, zipcode);
									callableStatement.setString(5, region);
									callableStatement.setString(6, universal_value);
									callableStatement.setString(7, state);
									callableStatement.setString(8, msa);
									callableStatement.setString(9, city);
									callableStatement.setDouble(10, rate.isEmpty() ? 0
											: 100*(Double.parseDouble(rate)));
									callableStatement.setDouble(11, min_balance.isEmpty() ? 0
											: 100*(Double.parseDouble(min_balance)));
									callableStatement.setDouble(12, max_balance.isEmpty() ? 0
											: 100*(Double.parseDouble(max_balance)));
									callableStatement.setString(13, terms);
									callableStatement.addBatch();									
									if(ratesCsv.getCurrentRecord()%1000 ==0){
										callableStatement.executeBatch();
										connection.commit();
										callableStatement.clearParameters(); 
										callableStatement.clearBatch();										
									}
							}
						}						
						callableStatement.executeBatch();
						connection.commit();
						callableStatement.clearParameters(); 
						callableStatement.clearBatch();	
						callableStatement.close();
						connection.close();
						logger.debug("Loading Ends");
					}else{
						logger.debug("Attached file(s) in workflow are not csv file");
					}
				}
			}
		} catch (ValueFormatException e) {
			logger.debug(e.getMessage());			
		} catch (PathNotFoundException e) {
			logger.debug(e.getMessage());
		} catch (RepositoryException e) {
			logger.debug(e.getMessage());	
		} catch (IOException e) {
			logger.debug(e.getMessage());
		} catch (LoginException e) {
			logger.debug(e.getMessage());
		} catch (SQLException e) {
			logger.debug(e.getMessage());
		}
		finally {
			closeConnection(connection);
			closeStatement(statement);
			closeStatement(callableStatement);			
		}
	}
	
	public void loadEquityRates(WorkItem workItem, DataSourcePool dataSourcePool,WorkflowSession workflowSession,ResourceResolverFactory resourceResolverFactory,SuntrustDotcomService suntrustDotcomService){
		connectionManager = new DBConnectionManager(dataSourcePool);
		try {
			WorkflowData workflowData = workItem.getWorkflowData();
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);
			if (workflowData.getPayloadType().equals(TYPE_JCR_PATH)) {
				ratesCsvPayloadPath = workflowData.getPayload().toString();
				if (ratesCsvPayloadPath.contains(".csv")) {
					if (suntrustDotcomService != null) {
						ratesCsvPayloadPath = ratesCsvPayloadPath
								+ "/jcr:content/renditions/original/jcr:content";
					}
					logger.debug("Attached CSV path" + ratesCsvPayloadPath);
					if (ratesCsvPayloadPath != ""
							|| ratesCsvPayloadPath != null) {
						resource = resourceResolver
								.getResource(ratesCsvPayloadPath);
						logger.debug("Resource path" + resource.getPath());
					}
					InputStream inputStream = null;
					if (resource != null) {
						Node docNode = resource.adaptTo(Node.class);
						inputStream = docNode.getProperty("jcr:data")
								.getBinary().getStream();
						ratesCsv = new CsvReader(inputStream, ',',
								StandardCharsets.UTF_8);
						ratesCsv.readHeaders();
						connection = connectionManager.getConnection();						
						statement = connection.createStatement();
						//delete all records from stage table
						executeQuery(stageEquityTblQry);
						//delete all records from prod temp table
						executeQuery(tempEquityTblQry);
						//load all new equity rates content into prod temp/stage tables
						callableStatement = connection.prepareCall("{call sp_insert_equity_rates(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
						logger.debug("data load begins");
						while (ratesCsv.readRecord() && workflowSession!=null && workItem!=null) {
								String annual_rate_type = ratesCsv
										.get("Annual Rate Type (APR = R, APY = Y)");
								String product = ratesCsv.get("Product");
								String apr_apy_value = ratesCsv
										.get("APR/APY Value");
								String productEligibility = ratesCsv.get("roduct Eligibility");
								String zipcode = ratesCsv.get("Zip");
								String region = ratesCsv.get("Region");
								String universal_value = ratesCsv
										.get("Universal Value");
								String state = ratesCsv.get("State");
								String msa = ratesCsv.get("MSA");
								String city = ratesCsv.get("City");
								
								String min_balance = ratesCsv
										.get("Min Balance");
								String max_balance = ratesCsv
										.get("Max Balance");
								String terms = ratesCsv.get("Terms");
								if (connection != null) {
									callableStatement.setString(1, annual_rate_type);
									callableStatement.setString(2, product);
									callableStatement.setDouble(3, apr_apy_value.isEmpty() ? 0
											:100*(Double.parseDouble(apr_apy_value)));
									callableStatement.setString(4, zipcode);
									callableStatement.setString(5, region);
									callableStatement.setString(6, universal_value);
									callableStatement.setString(7, state);
									callableStatement.setString(8, msa);
									callableStatement.setString(9, city);
									callableStatement.setDouble(10, productEligibility.isEmpty() ? 0
											: Double.parseDouble(productEligibility));
									callableStatement.setDouble(11, min_balance.isEmpty() ? 0
											: 100*(Double.parseDouble(min_balance)));
									callableStatement.setDouble(12, max_balance.isEmpty() ? 0
											: 100*(Double.parseDouble(max_balance)));
									callableStatement.setString(13, terms);
									callableStatement.addBatch();									
									if(ratesCsv.getCurrentRecord()%1000 ==0){
										callableStatement.executeBatch();
										connection.commit();
										callableStatement.clearParameters(); 
										callableStatement.clearBatch();										
									}
							}
						}						
						callableStatement.executeBatch();
						connection.commit();
						callableStatement.clearParameters(); 
						callableStatement.clearBatch();	
						callableStatement.close();
						connection.close();
						logger.debug("Loading Ends");
					}else{
						logger.debug("Attached file(s) in workflow are not csv file");
					}
				}
			}
		} catch (ValueFormatException e) {
			logger.debug(e.getMessage());			
		} catch (PathNotFoundException e) {
			logger.debug(e.getMessage());
		} catch (RepositoryException e) {
			logger.debug(e.getMessage());	
		} catch (IOException e) {
			logger.debug(e.getMessage());
		} catch (LoginException e) {
			logger.debug(e.getMessage());
		} catch (SQLException e) {
			logger.debug(e.getMessage());
		}
		finally {
			closeConnection(connection);
			closeStatement(statement);
			closeStatement(callableStatement);			
		}
	}
	public void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());				
			}
		}
	}

	public void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
	}
	public void executeQuery(String sqlQuery) {
		if(statement !=null){
			try {
				statement.executeUpdate(sqlQuery);			
				connection.commit();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
	}
}