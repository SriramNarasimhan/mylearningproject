package com.suntrust.dotcom.workflow;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.suntrust.dotcom.scheduler.DBConnectionManager;

public class RatesProdDeploymentHelper {
	DBConnectionManager connectionManager = null;
	private Connection connection = null;
	private CallableStatement callableStatement =null;
	private PreparedStatement preparedStatement=null;
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RatesProdDeploymentHelper.class);

	public void loadCDRates(WorkItem workItem,WorkflowSession workflowSession, DataSourcePool dataSourcePool) {
		WorkflowData workflowData = workItem.getWorkflowData();
		if (dataSourcePool != null && workflowData != null) {
			connectionManager = new DBConnectionManager(dataSourcePool);
			try {				
				connection = connectionManager.getConnection();
				if(connection!=null && workflowSession!=null && workItem!=null){
					LOGGER.debug("execute procedure start");
					connection = connectionManager.getConnection();
					//delete all records of prod table
					PreparedStatement preparedStatement=connection.prepareStatement("delete from tbl_prod_cd_rates");
					preparedStatement.executeUpdate();	
					connection.commit();
					//swap temp table as prod table and create temp table 
					callableStatement = connection.prepareCall("{call sp_swap_cd_rates_table}");
					callableStatement.execute();				
					connection.commit();
					connection.close();
					LOGGER.debug("execute procedure end");
				}

			} catch (SQLException e) {
				LOGGER.error("SQLException in RatesProdDeployment"+e.getMessage());
			}
			finally {
				closeConnection(connection);
				closeStatement(preparedStatement);
				closeStatement(callableStatement);
			}
		} else {
			LOGGER.error("datasource pool or workflowdata is null");
		}
	}
	public void loadEquityRates(WorkItem workItem,WorkflowSession workflowSession, DataSourcePool dataSourcePool) {
		WorkflowData workflowData = workItem.getWorkflowData();
		if (dataSourcePool != null && workflowData != null) {
			connectionManager = new DBConnectionManager(dataSourcePool);
			try {
				connection = connectionManager.getConnection();
				if(connection!=null && workflowSession!=null && workItem!=null){
					LOGGER.debug("execute procedure start");
					connection = connectionManager.getConnection();
					//delete all records of prod table
					PreparedStatement preparedStatement=connection.prepareStatement("delete from tbl_prod_equity_rates");
					preparedStatement.executeUpdate();	
					connection.commit();
					//swap temp table as prod table and create temp table 
					callableStatement = connection.prepareCall("{call sp_swap_equity_rates}");
					callableStatement.execute();				
					connection.commit();
					connection.close();
					LOGGER.debug("execute procedure end");
				}

			} catch (SQLException e) {
				LOGGER.error("SQLException in RatesProdDeployment"+e.getMessage());
			}
			finally {
				closeConnection(connection);
				closeStatement(preparedStatement);
				closeStatement(callableStatement);
			}
		} else {
			LOGGER.error("datasource pool or workflowdata is null");
		}
	}
	public void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				LOGGER.error("Error in closing connection"+e.getMessage());
			}
		}
	}

	public void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				LOGGER.error("Error in closing statement"+e.getMessage());
			}
		}
	}

}
