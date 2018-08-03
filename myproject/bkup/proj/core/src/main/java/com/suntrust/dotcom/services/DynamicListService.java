package com.suntrust.dotcom.services;

import java.util.List;

/**
 * This DynamicListService is an interface to declare methods.
 * 
 * @author Cognizant
 * @version 1.0
 * @since 10 July 2017
 * 
 */
public interface DynamicListService {
	/**
	 * @param pageUrls
	 * @return
	 * @throws RepositoryException
	 */
	// Date getModifiedDate(String pageUrls) throws RepositoryException;

	/**
	 * @param pageUrls
	 * @return
	 * @throws RepositoryException
	 */
	// String getPageDescription(String pageUrls) throws RepositoryException;

	/**
	 * @param pageUrls
	 * @return
	 * @throws RepositoryException
	 */
	// List<String> getTags(String pageUrls) throws RepositoryException;

	/**
	 * @param pageUrls
	 * @return
	 * @throws RepositoryException
	 */
	// String getPageTitle(String pageUrls) throws RepositoryException;

	/**
	 * @param contextUrl
	 * @param authorTags
	 * @param authorLogic
	 * @param criteriaForRule
	 * @param currentPagePath
	 * @return
	 */
	List<String> getResultsFromQuery(String contextUrl, String authorTags, String authorLogic, String criteriaForRule,
			String currentPagePath);

	/**
	 * @param contextUrl
	 * @param authorTags
	 * @param authorLogic
	 * @param criteriaForRule
	 * @param currentPagePath
	 * @return
	 */
	List<String> getResultsFromQueryForPooledTags(String contextUrl, List<String> authorTags, List<String> authorLogic,
			String criteriaForRule, String currentPagePath);
}
