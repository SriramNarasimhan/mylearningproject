package com.suntrust.dotcom.services.impl;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.services.SignOnMaintenanceService;

@Service
@Component(immediate = true, metatype = true, label = "Maintenance Message Configuration Service")
public class SignOnMaintenanceServiceImpl implements SignOnMaintenanceService {
	private static final Logger LOG = LoggerFactory
			.getLogger(SignOnMaintenanceServiceImpl.class);
	@Property(unbounded = PropertyUnbounded.DEFAULT, label = "Maintenance Message", description = "Maintenance Message to display when site is under maintenance")
	private String message = "false";
	private String default_message = "false";

	@Activate
	protected void activate(Map<String, Object> properties) {
		LOG.info("[ConfigurationService]: activating configuration service");
		message = getProperties(properties);
	}

	@Override
	public String getProperties(Map<String, Object> properties) {
		return PropertiesUtil.toString(
				properties.get("signon.maintenance.mode"), default_message);
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
