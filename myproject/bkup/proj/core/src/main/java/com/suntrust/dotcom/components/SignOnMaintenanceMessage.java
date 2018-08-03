package com.suntrust.dotcom.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.services.SignOnMaintenanceService;

public class SignOnMaintenanceMessage extends WCMUsePojo {
	Logger logger = LoggerFactory.getLogger(SignOnMaintenanceMessage.class);

	private String message;
	protected com.suntrust.dotcom.services.SignOnMaintenanceService service;

	@Override
	public void activate() {
		service = getSlingScriptHelper().getService(
				SignOnMaintenanceService.class);
		logger.info("SignOnMaintenanceService::"+ service);
	}

	public String getMessage() {
		this.message = service.getMessage();
		logger.info("message::"+message);

		return this.message;
	}

}