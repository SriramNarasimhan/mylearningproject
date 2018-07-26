package com.first.myproject.services.impl;

import java.util.Dictionary;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This service executes scheduled jobs
 * 
 */
@Component(immediate = true, metatype = true, label = " Cleanup Service")//
@Service(value = Runnable.class)
@Property(name = "scheduler.expression", value = "0 0/30 13-14 * * ?") // Every 30 minutes between 1PM and 2PM
public class CleanupServiceImpl implements Runnable {

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/** The scheduler for rescheduling jobs. */
	@Reference
	private SlingRepository repository;
	
	@Property(label = "Path", description = "Delete this path", value = "/dummy")
	public static final String CLEANUP_PATH = "cleanupPath";
	private String cleanupPath;
	
	protected void activate(ComponentContext componentContext) {
//		configure(componentContext.getProperties());
	}

	private void configure(Dictionary<?, ?> properties) {
		this.cleanupPath = PropertiesUtil.toString(properties.get(CLEANUP_PATH), null);
		log.info("configure : cleanupPath = '{}''", this.cleanupPath);
	}

	public void run() {
		/*log.info("Running Now...");
		Session session = null;
		try {
			session = repository.loginAdministrative(null);
			if(session.itemExists(cleanupPath)) {
				session.removeItem(cleanupPath);
				log.info("Dummy node deleted");
				session.save();
			}
		}
		catch(RepositoryException e) {
			log.error("Exception during cleanup", e);
		}
		finally {
			if(session != null) {
				session.logout();
			}
		}*/
	}
}



 /*Following is for AEM 6.0 


package com.adobe.training.core;

import java.util.Dictionary;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, metatype = true,
label = "Cleanup Service")
@Service(value = Runnable.class)
@Property(name = "scheduler.expression", value = "* /5 * * * * ?") // Every 5 seconds -remove space between * and /-
public class CleanupServiceImpl implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(CleanupServiceImpl.class);
	@Reference
	private SlingRepository repository;
	@Property(label = "Path", description = "Delete this path", value = "/tmp/mypath")
	public static final String CLEANUP_PATH = "cleanupPath";
	private String cleanupPath;
	protected void activate(ComponentContext componentContext){
		configure(componentContext.getProperties());
	}
	protected void configure(Dictionary<?, ?> properties) {
		this.cleanupPath = (String.valueOf(properties.get(CLEANUP_PATH)) != null)?String.valueOf(properties.get(CLEANUP_PATH)):null;
		LOGGER.info("configure: cleanupPath='{}''", this.cleanupPath);
	}
	@Override
	public void run() {
		LOGGER.info("running now-Cleanup path: "+this.cleanupPath);
		Session session = null;
		try {
			
			session = repository.loginService(null, repository.getDefaultWorkspace());
			LOGGER.info("Logged in with "+session.getUserID()+" & Is session Live: "+session.isLive());
	
			if(session.itemExists(cleanupPath) == true) {
				session.removeItem(cleanupPath);
				LOGGER.info("node deleted");
				session.save();
			}
		}
		catch (RepositoryException e) {
			LOGGER.error("exception during cleanup", e);
		} finally {
			if (session != null) {
				session.logout();
			}
		}
	}
}*/
