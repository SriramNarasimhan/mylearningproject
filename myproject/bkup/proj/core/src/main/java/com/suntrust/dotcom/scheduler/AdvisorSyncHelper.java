package com.suntrust.dotcom.scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;
import com.day.commons.datasource.poolservice.DataSourcePool;
import com.day.cq.commons.Externalizer;
import com.suntrust.dotcom.config.AdvisorConfigService;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.EmailService;
import com.suntrust.dotcom.services.ServiceAgentService;
import com.suntrust.dotcom.utils.Utils;

public class AdvisorSyncHelper {
	
	/** Logger class reference variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(AdvisorSyncHelper.class);
	
	/** CSV File path */
	private String filePath = "";
	
	/** DBConnectionManager class reference variable */
	private DBConnectionManager connectionManager = null;
	
	/** Connection class reference variable */
	private static Connection connection = null;
	
	/** PreparedStatement class reference variable */
	private PreparedStatement preparedStatementParent = null;
	
	/** PreparedStatement class reference variable */
	private PreparedStatement preparedStatementChild = null;
	
	/** Statement class reference variable */
	private Statement statement = null;
	
	/** Select query string variable */
	private final String deltaTableSelectParentQry = "select COUNT(*)  from tbl_delta_ftp_advisors";
	
	/** Select query string variable */
	private final String deltaTableSelectChildQry = "select COUNT(*)  from tbl_delta_ftp_advisors_address";

	/** Select query string variable */
	private final String finalTableSelectParentQry = "select COUNT(*)  from tbl_final_ftp_advisors";
	
	/** Select query string variable */
	private final String finalTableSelectChildQry = "select COUNT(*)  from tbl_final_ftp_advisors_address";

	/** Select query string variable */
	private final String dailyTableSelectParentQry = "select COUNT(*)  from tbl_daily_ftp_advisors";
	
	/** Select query string variable */
	private final String dailyTableSelectChildQry = "select COUNT(*) from tbl_daily_ftp_advisors_address";
	
	/** Select query string variable */
	private final String dailyVsFinalAdvisorsParent = "select * from tbl_daily_ftp_advisors Except select * from tbl_final_ftp_advisors";
	
	/** Select query string variable */
	//private final String finalVsdailyAdvisorsParent = "select * from tbl_final_ftp_advisors Except select * from tbl_daily_ftp_advisors";
	private final String finalVsdailyAdvisorsParent = "select emailaddress, jobtitle, city, state, zip from tbl_final_ftp_advisors "
			+ "Except select emailaddress, jobtitle, city, state, zip from tbl_daily_ftp_advisors";
	
	/** Delete query string variable */
	private final String deleteDeltaTableParentSqlQry = "delete from tbl_delta_ftp_advisors";
	
	/** Delete query string variable */
	private final String deleteDeltaTableChildSqlQry = "delete from tbl_delta_ftp_advisors_address";

	/** Insert query string variable */
	private final String dailyToDeltaCopyParent = "insert tbl_delta_ftp_advisors select * from tbl_daily_ftp_advisors where EmailAddress=";
	
	/** Insert query string variable */
	private final String dailyToFinalSyncupParent = "insert tbl_final_ftp_advisors select * from tbl_daily_ftp_advisors";

	/** Select query string variable */
	private final String dailyVsFinalAdvisorsChild = "select * from tbl_daily_ftp_advisors_address Except select * from tbl_final_ftp_advisors_address";
	
	/** Select query string variable */
	private final String finalVsdailyAdvisorsChild = "select * from tbl_final_ftp_advisors_address Except select * from tbl_daily_ftp_advisors_address";

	/** Insert query string variable */
	private final String dailyToDeltaCopyChild = "insert tbl_delta_ftp_advisors_address select * from tbl_daily_ftp_advisors_address where EmailAddress=";
	
	/** Insert query string variable */
	private final String dailyToFinalSyncupChild = "insert tbl_final_ftp_advisors_address select * from tbl_daily_ftp_advisors_address";
	
	/** Insert query string variable */
	private final String advisorAuditLogInsertQry = "insert into tbl_advisors_audit_log (AdvisorUpdateStatus,NoOfAdvisorsUpdated,NoOfAdvisorsAddressUpdated,NoOfAdvisorsInAEM) VALUES ";
	
	
	//Newly added variables for email
	/** Variable to hold approver group */
	private String approverGroup = null;
	
	/** UserManager class reference variable */
	private UserManager userManager = null;
		
	 /** ResourceResolver object reference variable*/
    private ResourceResolver resourceResolver;
		
    /** joiner object which holds the deleted profile list*/
    private StringJoiner joiner = null;
    
    /** emailRecipients list*/
    List<String> emailRecipients = null;
    
    /** Externalizer class reference variable */
	private Externalizer externalizer=null;
	
	/** Variable to hold environment */
	private String environmentDetails = null;
    
	/** Approve notification email template path */
	private static final String NOTIFICATION_TEMPLATEPATH = "/etc/notification/email/html/dotcom/advisordeletionemailnotification.html";
    
	/**
	 * Main method call 
	 * 
	 * @param dataSourcePool
	 * @param suntrustDotcomService
	 * @param serviceAgentService
	 */
	public void loadAdvisors(DataSourcePool dataSourcePool,SuntrustDotcomService suntrustDotcomService,ServiceAgentService serviceAgentService, EmailService emailService, AdvisorConfigService configService) {
		LOGGER.debug("CSV SYNC Start ::::");
		Resource resource = null;
		connectionManager = new DBConnectionManager(dataSourcePool);
		
		String sqldailyTableInsertParentQry = "insert into tbl_daily_ftp_advisors (EmailAddress,JobTitle,DesignationCode,LastName,FirstName,Phone,Street,City,State,Zip,PrimaryAddress,Specialty,ProfilePhoto,Bio,Fax,Cell,NMLSR,Spanish,Cd,AppString,Testimonials,Link1text,Link1url,Link2Text,Link2Url,Link3Text,Link3Url,Link4Text,Link4Url,Link5Text,Link5Url,Link6Text,Link6Url,Link7Text,Link7Url,Link8Text,Link8Url,Link9Text,Link9Url,Link10Text,Link10Url,Facebook,Twitter,LinkedIn,YouTube,GooglePlus) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String sqldailyTableInsertChildQry="insert into tbl_daily_ftp_advisors_address(EmailAddress,Street,City,State,Zip) values (?,?,?,?,?)";
		String sqldailyTableDeleteParentQry = "delete from tbl_daily_ftp_advisors";
		String sqldailyTableDeleteChildQry = "delete from tbl_daily_ftp_advisors_address";
		String sqlFinalTableDeleteParentQry = "delete from tbl_final_ftp_advisors";
		String sqlFinalTableDeleteChildQry = "delete from tbl_final_ftp_advisors_address";
		
		try {
			if(connectionManager!=null){
				connection=connectionManager.getConnection();
				statement=connectionManager.getStatement();
				preparedStatementParent=connectionManager.getPreparedStatement(sqldailyTableInsertParentQry);
				preparedStatementParent=connectionManager.getPreparedStatement(sqldailyTableInsertChildQry);
			}	
			
			InputStream inputStream = null;		
			if (suntrustDotcomService != null) {
				filePath = suntrustDotcomService
						.getPropertyValue("advisor-page-path");
				filePath = filePath
						+ "/jcr:content/renditions/original/jcr:content";
			}
			resourceResolver = serviceAgentService.getServiceResourceResolver("dotcomreadservice");
			if(filePath != null || filePath.equals("") == false){
				resource = resourceResolver.getResource(filePath);				
			}
			
			if (resource != null) {
				Node docNode = resource.adaptTo(Node.class);
				inputStream = docNode.getProperty("jcr:data").getBinary()
						.getStream();
			}

			if (connection != null ) {
				deleteTable(sqldailyTableDeleteParentQry);
				deleteTable(sqldailyTableDeleteChildQry);
				preparedStatementParent = connection.prepareStatement(sqldailyTableInsertParentQry);	
				preparedStatementChild = connection.prepareStatement(sqldailyTableInsertChildQry);	
			}
			
			CsvReader advisorCsv = new CsvReader(inputStream, ',', StandardCharsets.UTF_8);	
			Map<String,Boolean> advisorPrimaryMap=new HashMap<String,Boolean>();			
			advisorCsv.readHeaders();	
			if(preparedStatementParent != null && preparedStatementChild != null){
				while (advisorCsv.readRecord()) {
					String emailAddress = advisorCsv.get("Business Email Address");	
					String street=advisorCsv.get("Street");
					String city=advisorCsv.get("City");
					String state=advisorCsv.get("State");
					String zip=advisorCsv.get("Zip");
					if ((emailAddress != null && emailAddress.trim().isEmpty() == false)
							&& (city != null && city.trim().isEmpty() == false)
							&& (state != null && state.trim().isEmpty() == false)
							&& (zip != null && zip.trim().isEmpty() == false)) {
						
						if(advisorPrimaryMap.isEmpty() || !advisorPrimaryMap.containsKey(emailAddress)){
							preparedStatementParent.setString(1, emailAddress);
							preparedStatementParent.setString(2, advisorCsv.get("Title"));
							preparedStatementParent.setString(3, advisorCsv.get("Designation Code"));
							preparedStatementParent.setString(4, advisorCsv.get("Last Name"));
							preparedStatementParent.setString(5, advisorCsv.get("First Name"));				
							preparedStatementParent.setString(6, advisorCsv.get("Phone"));
							preparedStatementParent.setString(7, street);
							preparedStatementParent.setString(8, city);
							preparedStatementParent.setString(9, state);
							preparedStatementParent.setString(10, zip);				
							preparedStatementParent.setString(11, advisorCsv.get("Primary Address"));
							preparedStatementParent.setString(12, advisorCsv.get("Specialty"));
							preparedStatementParent.setString(13, advisorCsv.get("Photo").toLowerCase());
							preparedStatementParent.setString(14, advisorCsv.get("Bio"));
							preparedStatementParent.setString(15, advisorCsv.get("Fax"));
							preparedStatementParent.setString(16, advisorCsv.get("Cell"));
							preparedStatementParent.setString(17, advisorCsv.get("NMLS"));
							preparedStatementParent.setString(18, advisorCsv.get("Spanish"));
							preparedStatementParent.setString(19, advisorCsv.get("CD"));
							preparedStatementParent.setString(20, advisorCsv.get("AppString"));
							preparedStatementParent.setString(21, advisorCsv.get("Testimonials"));
							preparedStatementParent.setString(22, advisorCsv.get("Link1text"));
							preparedStatementParent.setString(23, advisorCsv.get("Link1url"));
							preparedStatementParent.setString(24, advisorCsv.get("Link2Text"));
							preparedStatementParent.setString(25, advisorCsv.get("Link2 Url"));
							preparedStatementParent.setString(26, advisorCsv.get("Link3Text"));
							preparedStatementParent.setString(27, advisorCsv.get("Link3 Url"));
							preparedStatementParent.setString(28, advisorCsv.get("Link4 Text"));
							preparedStatementParent.setString(29, advisorCsv.get("Link4 Url"));
							preparedStatementParent.setString(30, advisorCsv.get("Link 5 Text"));
							preparedStatementParent.setString(31, advisorCsv.get("Link 5 Url"));				
							preparedStatementParent.setString(32, advisorCsv.get("Link 6 Text"));
							preparedStatementParent.setString(33, advisorCsv.get("Link 6 Url"));
							preparedStatementParent.setString(34, advisorCsv.get("Link 7 Text"));
							preparedStatementParent.setString(35, advisorCsv.get("Link 7 Url"));
							preparedStatementParent.setString(36, advisorCsv.get("Link 8 Text"));
							preparedStatementParent.setString(37, advisorCsv.get("Link 8 Url"));
							preparedStatementParent.setString(38, advisorCsv.get("Link 9 Text"));
							preparedStatementParent.setString(39, advisorCsv.get("Link 9 Url"));
							preparedStatementParent.setString(40, advisorCsv.get("Link 10 Text"));
							preparedStatementParent.setString(41, advisorCsv.get("Link 10 Url"));
							preparedStatementParent.setString(42, advisorCsv.get("Facebook"));
							preparedStatementParent.setString(43, advisorCsv.get("Twitter"));
							preparedStatementParent.setString(44, advisorCsv.get("LinkedIn"));
							preparedStatementParent.setString(45, advisorCsv.get("YouTube"));
							preparedStatementParent.setString(46, advisorCsv.get("GooglePlus"));
							preparedStatementParent.addBatch();			
							advisorPrimaryMap.put(emailAddress, true);	
						} else {
							preparedStatementChild.setString(1, emailAddress);
							preparedStatementChild.setString(2, street);
							preparedStatementChild.setString(3, city);
							preparedStatementChild.setString(4, state);
							preparedStatementChild.setString(5, zip);	
							preparedStatementChild.addBatch();	
						}
					}
				}
				preparedStatementParent.executeBatch();
				connection.commit();	
				preparedStatementChild.executeBatch();
				connection.commit();
			}
						
			LOGGER.debug("CSV to DB sync is completed Parent:"+getRecordCount("select COUNT(*)  from tbl_daily_ftp_advisors"));
			LOGGER.debug("CSV to DB sync is completed Child:"+getRecordCount("select COUNT(*)  from tbl_daily_ftp_advisors_address"));
			LOGGER.debug("CSV SYNC End ::::");
			
		LOGGER.debug("DB SYNC Start ::::");		
		//Get all parent records to be updated into final table.
		Set<String> dailyVsFinalAdvisorsListParent = getAdvisorUpdates(dailyVsFinalAdvisorsParent);
		LOGGER.debug("Parent records to be updated:"+dailyVsFinalAdvisorsListParent.size());	
		//Get all child records to be updated into final table.
		Set<String> dailyVsFinalAdvisorsListChild = getAdvisorUpdates(dailyVsFinalAdvisorsChild);
		LOGGER.debug("Child records to be updated:"+dailyVsFinalAdvisorsListChild.size());	
	
		//Get all parent records to be deleted from final table.
		Set<String> finalVsdailyAdvisorsListParent = getAdvisorUpdates(finalVsdailyAdvisorsParent);
		LOGGER.debug("Profiles pages to be deleted: ["+finalVsdailyAdvisorsListParent.size()+ "] List: "+finalVsdailyAdvisorsListParent.toString());	
		
		if(finalVsdailyAdvisorsListParent.isEmpty() == false){
			LOGGER.debug("Delete page count: "+finalVsdailyAdvisorsListParent.size());
			
			// Send email with the list of the deleted page paths			
			userManager = resourceResolver.adaptTo(UserManager.class);
			externalizer= resourceResolver.adaptTo(Externalizer.class);
			environmentDetails = externalizer.authorLink(resourceResolver, ""); 
			LOGGER.debug("environemt: "+environmentDetails);
			//environmentDetails = environmentDetails.substring(0, environmentDetails.length()-1);
			
			if(null != suntrustDotcomService){
				approverGroup = suntrustDotcomService.getPropertyValue("dotcom_people_finder_author");
			}			
			
			List<String> emailRecipients =  new ArrayList<String>();					
			emailRecipients = Utils.setRecipients(approverGroup,userManager);	
			
			String rootPath = configService.getPropertyValue("ROOT_PATH"); 		
			rootPath = rootPath.substring(1,rootPath.length());
			/*Create List of deleted profiles*/								
			joiner = new StringJoiner("</li>","","</li>");
			String pagePath = environmentDetails + rootPath + "/profile";		
			finalVsdailyAdvisorsListParent.stream().forEach((profilelist) -> {			 
				  joiner.add("<li>"+profilelist.split("@")[0]);  
			});						
			
			LOGGER.info("Pages to be deleted: "+joiner.toString());								
			Utils.sendEmail(emailService, pagePath, emailRecipients, NOTIFICATION_TEMPLATEPATH, joiner); 
		}
		
		// add child records also into parent table to update AEM pages, if there is only update into advisor's child record
		dailyVsFinalAdvisorsListParent.addAll(dailyVsFinalAdvisorsListChild);
		//Get all child records that are deleted in daily table and update the parent delta table to update AEM page.
		Set<String> finalVsdailyAdvisorsListChild = getAdvisorUpdates(finalVsdailyAdvisorsChild);
		
		dailyVsFinalAdvisorsListParent.addAll(dailyVsFinalAdvisorsListChild);
		dailyVsFinalAdvisorsListParent.addAll(finalVsdailyAdvisorsListChild);
		
		LOGGER.debug("Total records to be synced up: "+dailyVsFinalAdvisorsListParent.size());
		
		// delete all records from delta parent table
		deleteTable(deleteDeltaTableParentSqlQry);
		// delete all records from delta child table
		deleteTable(deleteDeltaTableChildSqlQry);
		// delete all records from final parent table
		deleteTable(sqlFinalTableDeleteParentQry);
		// delete all records from final child table
		deleteTable(sqlFinalTableDeleteChildQry);		
		// copy all updated/new records into delta parent table
		advisorsTableUpdate(dailyToDeltaCopyParent,
				dailyVsFinalAdvisorsListParent);
		statement.executeBatch();
		connection.commit();
		// copy all updated/new records into delta child table
		//advisorsTableUpdate(dailyToDeltaCopyChild,dailyVsFinalAdvisorsListChild);
		advisorsTableUpdate(dailyToDeltaCopyChild,dailyVsFinalAdvisorsListParent);
		
		statement.executeBatch();
		connection.commit();
		// copy all updated/new records into final parent table
		copyAllRecords(dailyToFinalSyncupParent);
		statement.executeBatch();
		connection.commit();
		// copy all updated/new records into final child table
		copyAllRecords(dailyToFinalSyncupChild);
		statement.executeBatch();
		connection.commit();
		// update details in audit log for reference
		updateAuditLog(advisorAuditLogInsertQry);
		LOGGER.debug("Daily DB to Final Parent :"+getRecordCount("select COUNT(*)  from tbl_final_ftp_advisors"));
		LOGGER.debug("Daily DB to Final Child :"+getRecordCount("select COUNT(*)  from tbl_final_ftp_advisors_address"));
		LOGGER.debug("DB SYNC End ::::");
		} catch (LoginException e) {
			LOGGER.error("LoginException captured: ",e);
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException captured: ",e);
		} catch (IOException e) {
			LOGGER.error("IOException captured: ",e);
		} catch (SQLException e) {
			LOGGER.error("SQLException captured: ",e);
		}finally {	
			closeStatement(preparedStatementParent);
			closeStatement(preparedStatementChild);
			closeConnection(connection);
			serviceAgentService.release(this.resourceResolver.adaptTo(Session.class));
			serviceAgentService.release(this.resourceResolver);
		}
	}
	
	/**
	 * Returns records set for given query
	 * 
	 * @param sqlQuery
	 * @return
	 */
	public Set<String> getAdvisorUpdates(String sqlQuery) {
		Set<String> emailAddressList = new HashSet<String>();
		if(statement !=null){
			try {
				ResultSet resultSet = statement.executeQuery(sqlQuery);			
				while (resultSet.next()) {
					emailAddressList.add(resultSet.getString("EmailAddress"));
				}
				resultSet.close();
			} catch (SQLException e) {
				LOGGER.error("SQLException captured. Message:{} , Trace: {}" ,e.getMessage(),e);
			}
		}
		return emailAddressList;
	}

	/**
	 * Adds execution query to batch from given list
	 * 
	 * @param sqlQuery
	 * @param recordsList
	 */
	public void advisorsTableUpdate(String sqlQuery, Set<String> recordsList) {
		Iterator<String> recordItr = recordsList.iterator();
		if(statement !=null){
			while (recordItr.hasNext()) {
				try {				
					statement.addBatch(sqlQuery + "'" + recordItr.next() + "'");				
				} catch (SQLException e) {
					LOGGER.error("Exception captured. Message:{} , Trace: {}" ,e.getMessage(),e);
				}
	
			}
		}
	}
	
	/**
	 * Adds execution query to batch
	 * 
	 * @param sqlQuery
	 */
	public void copyAllRecords(String sqlQuery) {
		if(statement !=null){
			try {
				statement.addBatch(sqlQuery);
			} catch (SQLException e) {
				LOGGER.error("Exception captured. Message:{} , Trace: {}" ,e.getMessage(),e);
			}	
		}
	}
	
	/**
	 * Query update execution method
	 * 
	 * @param sqlQuery
	 */
	public void deleteTable(String sqlQuery) {
		if(statement !=null){
			try {
				statement.executeUpdate(sqlQuery);			
				connection.commit();
			} catch (SQLException e) {
				LOGGER.error("Exception capture when deleting records from table. Message:{} , Trace: {}" ,e.getMessage(),e);
			}
		}
	}

	/**
	 * Updates audit log table 
	 * 
	 * @param sqlQuery
	 */
	public void updateAuditLog(String sqlQuery) {
		String executionStatus = null;
		if (statement != null) {
			/** check update status of parent and child tables */
			int deltaTableAdvisorsRecordsCount = getRecordCount(deltaTableSelectParentQry);
			int deltaTableAdvisorsAddressRecordsCount = getRecordCount(deltaTableSelectChildQry);
			int finalAdvisorsParentCount=getRecordCount(finalTableSelectParentQry);
			
			if (getRecordCount(dailyTableSelectParentQry) == finalAdvisorsParentCount
					&& getRecordCount(dailyTableSelectChildQry)
							 == getRecordCount(finalTableSelectChildQry)) {
				if (deltaTableAdvisorsRecordsCount > 0
						|| deltaTableAdvisorsAddressRecordsCount > 0) {
					executionStatus = "Success";
				}else{
					executionStatus = "No Update";
				}
			} else {
				executionStatus = "Failed";
			}
			try {
				statement.executeUpdate(sqlQuery+"('"+executionStatus+"',"+"'"+deltaTableAdvisorsRecordsCount+"',"+"'"+deltaTableAdvisorsAddressRecordsCount+"',"+"'"+finalAdvisorsParentCount+"')");
				connection.commit();
			} catch (SQLException e) {
				LOGGER.error("Exception captured in updateAuditLog. Message:{} , Trace: {}" ,e.getMessage(),e);
			}

		}
	}
	
	/**
	 * Returns record count for given execution query
	 * 
	 * @param sqlQuery
	 * @return
	 */
	public int getRecordCount(String sqlQuery){
		int recordsCount=0;
		if(statement !=null){
			ResultSet resultSet = null;
			try {
				resultSet = statement.executeQuery(sqlQuery);
				resultSet.next();
				recordsCount= resultSet.getInt(1);
			} catch (SQLException e) {
				LOGGER.error("Exception captured when executing query. Message:{} , Trace: {}" ,e.getMessage(),e);	
				return recordsCount;
			} finally {
				if(resultSet != null){
					try {
						resultSet.close();
					} catch (SQLException e) {
						LOGGER.error("Exception captured when closing result set. Message:{} , Trace: {}" ,e.getMessage(),e);	
					}
				}
			}
		}
		return recordsCount;		
	}
	
	/**
	 * Close connection method
	 * 
	 * @param connection
	 */
	public void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				LOGGER.error("SQLException captured when closing connection. Message:{} , Trace: {}" ,e.getMessage(),e);
			}
		}
	}
	
	/**
	 *  Close statement method
	 * 
	 * @param statement
	 */
	public void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				LOGGER.error("SQLException captured when closing statement. Message:{} , Trace: {}" ,e.getMessage(),e);
			}
		}
	}		
}
