package com.suntrust.dotcom.components;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;

public class PromoLogoComponent extends WCMUsePojo{

	private static final Logger LOGGER = LoggerFactory.getLogger(PromoLogoComponent.class);
	private static final String THUMBNAIL_RELPATH="/jcr:content/image";
	private String imageReference=null;
	private String imageAlt=null;
	
	
	@Override
	public void activate() {
		
		String thumbnailPath=get("pagePath",String.class)+THUMBNAIL_RELPATH;
		LOGGER.info("PROMO LOGO COMPOENENT===> thumbnail path is : "+thumbnailPath);
		Resource resource=getResourceResolver().getResource(thumbnailPath);
		if(null!=resource){
		ValueMap prop=resource.adaptTo(ValueMap.class);
		String logoPath=prop.get("fileReference",String.class);
		String logoAlt=prop.get("alttext",String.class);
		imageReference=StringUtils.defaultString(logoPath,"");
		imageAlt=StringUtils.defaultString(logoAlt,"");
		LOGGER.info("PROMO LOGO COMPOENENT===> company logo imagepath : "+ imageReference);
		LOGGER.info("PROMO LOGO COMPOENENT===> company logo alttext : "+ imageAlt);
		}
		else{
			LOGGER.error("PROMO LOGO COMPOENENT===> Resource not found at path  : "+thumbnailPath);
			imageReference="";
			imageAlt="";
		}
	}


	public String getImageAlt() {
		return imageAlt;
	}


	public String getImageReference() {
		return imageReference;
	}

}
