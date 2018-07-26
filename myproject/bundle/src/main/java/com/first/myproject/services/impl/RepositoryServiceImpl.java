/**
 * 
 */
package com.first.myproject.services.impl;

import javax.jcr.Repository;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.first.myproject.services.RepositoryService;

/**
 * @author Welcome
 *
 */
@Component(name = "RepositoryName", metatype = false, immediate=true)
@Service
public class RepositoryServiceImpl implements RepositoryService{
	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryService.class);
	
	@Reference
	private Repository repository;

	public String getRepositoryName() {
		return repository.getDescriptor(Repository.REP_NAME_DESC);
	}
	
	@Activate
	protected void activate(){
		LOGGER.info("Service activated");
	}
	
	@Deactivate
	protected void deactivate(){
		LOGGER.info("Service deactivated");
	}

}
