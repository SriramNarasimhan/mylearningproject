package com.suntrust.dotcom.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to perform the db connection related operations.
 */
public class DatabaseUtils {
	/** Logger variable */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(DatabaseUtils.class);

	/**
	 * This method closes the database connection
	 * 
	 * @param connection
	 * @return
	 */
	public void closeConnection(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * This method closes the statement
	 * 
	 * @param statement
	 * @return
	 */
	public void closeStatement(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * This method executes the SQL query
	 * 
	 * @param sqlQuery
	 * @param statement
	 * @param connection
	 * @return
	 */
	public void executeQuery(String sqlQuery, Statement statement,
			Connection connection) {
		try {
			if (statement != null) {
				statement.executeUpdate(sqlQuery);
				connection.commit();

			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		}
	}

	/**
	 * This method executes the SQL query and returns the record count
	 * 
	 * @param sqlQuery
	 * @param statement
	 * @return recordsCount
	 */
	public int getRecordCount(String sqlQuery, Statement statement) {
		int recordsCount = 0;
		try {
			if (statement != null) {
				ResultSet resultSet = statement.executeQuery(sqlQuery);
				resultSet.next();
				recordsCount = resultSet.getInt(1);
				resultSet.close();

			}
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			return recordsCount;
		}
		return recordsCount;
	}

}
