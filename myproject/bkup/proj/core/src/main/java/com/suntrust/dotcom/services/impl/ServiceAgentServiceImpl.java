package com.suntrust.dotcom.services.impl;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.services.ServiceAgentService;

@Component(label = "Business Service - Service Agents", description="Exposes service agents accounts", metatype = true, immediate = false, enabled = true)
@Properties({ @Property(label = "Vendor", name = Constants.SERVICE_VENDOR, value = "SunTrust", propertyPrivate = true) })
@Service
/**
 * Class exposing Service Agent accounts to code paths where the SlingRequest's ResourcesResolver/Session is not available. 
 * 
 * @author ugnk52
 *
 */
public class ServiceAgentServiceImpl implements ServiceAgentService {
	/** Logger class reference variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceAgentServiceImpl.class);
	
	/** String constant variable  for workflow agent*/
	private static final String PRINCIPAL_WORKFLOW = "workflow-agent";
	
	/** String constant variable  for siteconfig agent*/
	private static final String PRINCIPAL_SITECONFIG = "siteconfig-agent";
	
	/** String constant variable  for taxonomy agent*/
	private static final String PRINCIPAL_TAXONOMY = "taxonomy-agent";
	
	/** String constant variable  for accounts agent*/
	private static final String PRINCIPAL_ACCOUNTS = "accounts-agent";
	
	/** String constant variable  for replication agent*/
	private static final String PRINCIPAL_REPLICATION = "replication-agent";
	
	/** String constant variable  for reverse replicator*/
	private static final String PRINCIPAL_REVERSE_REPLICATION = "reverse_replicator"; 
	
    /** OSGi Properties */
    private static final boolean DEFAULT_DEFAULT_TO_ADMINISTRATIVE = true;
    
    /** String constant variable  for reverse replicator*/
    private boolean defaultToAdministrative = DEFAULT_DEFAULT_TO_ADMINISTRATIVE;
    
    @Property(label = "Default to Administrative Permissions", 
    		  description = "Select a CQ Service Agent Use Account cannot be acquired default to administrative permissions (admin Resource Resolver or JCR Session)", 
    		  boolValue = DEFAULT_DEFAULT_TO_ADMINISTRATIVE)
    
    /** Keyword */
    public static final String PROP_DEFAULT_TO_ADMINISTRATIVE = "prop.default-to-administrative";	
	
    /** SlingRepository class reference variable */
	@Reference
    private SlingRepository repository;
	
	/** ResourceResolverFactory class reference variable */
	@Reference
	private ResourceResolverFactory resourceResolverFactory;	
	
	/**
     * {@inheritDoc}
     */
	@Override
	public ResourceResolver getWorkflowResourceResolver() throws RepositoryException, LoginException {
		return this.getResourceResolver(this.getWorkflowSession());
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public ResourceResolver getTaxonomyResourceResolver() throws LoginException, RepositoryException {
		return this.getResourceResolver(this.getTaxonomySession());
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public ResourceResolver getAccountsResourceResolver() throws LoginException, RepositoryException {
		return this.getResourceResolver(this.getAccountsSession());
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public ResourceResolver getReplicationResourceResolver() throws LoginException, RepositoryException {
		return this.getResourceResolver(this.getReplicationSession());
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public ResourceResolver getReverseReplicationResourceResolver() throws LoginException, RepositoryException {
		return this.getResourceResolver(this.getReverseReplicationSession());
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Session getWorkflowSession() throws RepositoryException {
		return this.getSession(PRINCIPAL_WORKFLOW);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Session getSiteConfigSession() throws RepositoryException {
		return this.getSession(PRINCIPAL_SITECONFIG);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Session getTaxonomySession() throws RepositoryException {
		return this.getSession(PRINCIPAL_TAXONOMY);
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public Session getAccountsSession() throws RepositoryException {
		return this.getSession(PRINCIPAL_ACCOUNTS);
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public Session getReplicationSession() throws RepositoryException {
		return this.getSession(PRINCIPAL_REPLICATION);
	}
	
	/**
     * {@inheritDoc}
     */
	@Override
	public Session getReverseReplicationSession() throws RepositoryException {
		return this.getSession(PRINCIPAL_REVERSE_REPLICATION);
	}	
	
	/**
     * {@inheritDoc}
     */
	public void release(final Session jcrSession) {
		if(jcrSession != null) {
			jcrSession.logout();
		}
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void release(ResourceResolver resourceResolver) {
		if(resourceResolver != null) {
			resourceResolver.close();
		}
	}	
	
	/**
     * {@inheritDoc}
     */
	public ResourceResolver getResourceResolver(final String principalName) throws LoginException, RepositoryException {
		return this.getResourceResolver(this.getSession(principalName));
	}
	
	/**
     * {@inheritDoc}
     */
	public Session getSession(final String principalName) throws RepositoryException {		
		Session adminSession = null;
		try {
			adminSession = repository.loginAdministrative(null);
			final Session jcrSession = adminSession.impersonate(
					new SimpleCredentials(principalName, new char[0]));
			return jcrSession;
		} catch(RepositoryException ex) {
			if(defaultToAdministrative) {
				LOGGER.debug("Could not obtain service agent account [ {} ] defaulting to Administrative privileges.", principalName);
				return repository.loginAdministrative(null);
			} else {
				LOGGER.error("Could not obtain service agent account [ {} ]", principalName);
				throw ex;
			}
		} finally {
			if(adminSession != null) {
				adminSession.logout();
			}
		}
	}
	
	/**
	 * Returns ResourceResolver for given session object
	 *  
	 * @param jcrSession
	 * @return
	 * @throws LoginException
	 */
	private ResourceResolver getResourceResolver(final Session jcrSession) throws LoginException {
		Map<String, Object> authInfo = new HashMap<String, Object>();
		authInfo.put(JcrResourceConstants.AUTHENTICATION_INFO_SESSION, jcrSession);
		return resourceResolverFactory.getResourceResolver(authInfo);	
	}	
	
	/**
	 * Overridden activate method
	 * 
	 * @param ctx
	 * @throws Exception
	 */
    @Activate
    protected void activate(final ComponentContext ctx) 
    	throws Exception {
        @SuppressWarnings("unchecked")
		final Map<String, String> properties = 
        	(Map<String, String>) ctx.getProperties();
        
        defaultToAdministrative = PropertiesUtil.toBoolean(properties.get(PROP_DEFAULT_TO_ADMINISTRATIVE), DEFAULT_DEFAULT_TO_ADMINISTRATIVE);
    }

    /**
     * Returns custom service user ResourceResolver instance
     */
    public ResourceResolver getServiceResourceResolver(String principalName) throws LoginException
    {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
        return resourceResolverFactory.getServiceResourceResolver(param);
    }

}
