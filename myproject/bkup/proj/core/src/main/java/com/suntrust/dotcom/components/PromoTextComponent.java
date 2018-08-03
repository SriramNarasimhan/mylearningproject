package com.suntrust.dotcom.components;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;


public class PromoTextComponent extends WCMUsePojo {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PromoTextComponent.class);
	private static final String PROMO_TEXT="text";
	private static final String PROMO_COMPANY="jcr:title";
	private static final String COMPANY_PLACEHOLDER="#CompanyName";
	private String promoTextContent=null;
	private String promoCompany=null;

	@Override
	public void activate(){

		LOGGER.info("PromoText Component Activated");
		String currentPagePath=get("pagePath",String.class)+"/jcr:content";
		Resource resource =getResourceResolver().getResource(currentPagePath);
		ValueMap compProp=getProperties();
		promoTextContent=compProp.get(PROMO_TEXT,String.class);
		if(null!=resource){
			ValueMap prop=resource.adaptTo(ValueMap.class);
			promoCompany=prop.get(PROMO_COMPANY,String.class);
			LOGGER.info("PROMO TEXT COMPOENENT===> Promo Company Name Entered "+promoCompany);
			if(null!=promoTextContent && promoTextContent.contains(COMPANY_PLACEHOLDER))
				promoTextContent=promoTextContent.replaceAll(COMPANY_PLACEHOLDER, promoCompany);
		}
		else
			LOGGER.error("PROMO TEXT COMPOENENT===> No data in promo text component");
	}

	public String getPromoTextContent() {
		return promoTextContent;
	}


	public String getPromoCompany() {
		return promoCompany;
	}


}
