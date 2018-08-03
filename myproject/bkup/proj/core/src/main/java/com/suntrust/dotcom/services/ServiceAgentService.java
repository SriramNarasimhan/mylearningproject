package com.suntrust.dotcom.services;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * Service responsible for exposing Service Agent accounts to code paths where the SlingRequest's ResourcesResolver/Session is not available.
 * Common use is in asynchronous processes such as Workflow, Scheduled Jobs or EventHandlers.
 */
public interface ServiceAgentService {
	
	/**
	 * Returns ResourceResolver for given type
	 * 
	 * @param principalName
	 * @return
	 * @throws LoginException
	 * @throws RepositoryException
	 */
	public ResourceResolver getResourceResolver(String principalName) throws LoginException, RepositoryException;

	/**
	 * Return Accounts ResourceResolver
	 * 
	 * @return
	 * @throws LoginException
	 * @throws RepositoryException
	 */
	public ResourceResolver getAccountsResourceResolver() throws LoginException, RepositoryException;
	
	/**
	 * Return Replication ResourceResolver
	 * 
	 * @return
	 * @throws LoginException
	 * @throws RepositoryException
	 */
	public ResourceResolver	getReplicationResourceResolver() throws LoginException, RepositoryException;
	
	/**
	 * Return Reverse Replication ResourceResolver
	 * 
	 * @return
	 * @throws LoginException
	 * @throws RepositoryException
	 */
	public ResourceResolver getReverseReplicationResourceResolver() throws LoginException, RepositoryException;
	
	/**
	 * Return Taxonomy ResourceResolver
	 * 
	 * @return
	 * @throws LoginException
	 * @throws RepositoryException
	 */
	public ResourceResolver getTaxonomyResourceResolver() throws LoginException, RepositoryException;
	
	/**
	 * Return Workflow ResourceResolver
	 * 
	 * @return
	 * @throws LoginException
	 * @throws RepositoryException
	 */
	public ResourceResolver getWorkflowResourceResolver() throws LoginException, RepositoryException;
	
	/**
	 * Returns session of given type
	 * 
	 * @param principalName
	 * @return
	 * @throws RepositoryException
	 */
	public Session getSession(String principalName) throws RepositoryException;

	/**
	 * Returns Accounts Session
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public Session getAccountsSession() throws RepositoryException;
	
	/**
	 * Returns Replication Session
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public Session getReplicationSession() throws RepositoryException;
	
	/**
	 * Returns Reverse Replication Session
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public Session getReverseReplicationSession() throws RepositoryException;
	
	/**
	 * Returns Taxonomy Session
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public Session getTaxonomySession() throws RepositoryException;	
	
	/**
	 * Returns Workflow Session
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public Session getWorkflowSession() throws RepositoryException;
	
	/**
	 * Returns Config Session
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	public Session getSiteConfigSession() throws RepositoryException;
	
	/**
	 * Release given session
	 * 
	 * @param jcrSession
	 */
	public void release(final Session jcrSession);
	
	/**
	 * Release ResourceResolver 
	 * 
	 * @param resourceResolver
	 */
	public void release(final ResourceResolver resourceResolver);
	
	/**
	 * Returns Service Resource Resolver
	 * 
	 * @param principalName
	 * @return
	 * @throws LoginException
	 * @throws RepositoryException
	 */
	public ResourceResolver getServiceResourceResolver(String principalName) throws LoginException, RepositoryException;
	
	
}
