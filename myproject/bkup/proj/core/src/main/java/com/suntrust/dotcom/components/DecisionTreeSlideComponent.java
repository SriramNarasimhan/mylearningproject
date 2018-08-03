package com.suntrust.dotcom.components;

import java.util.List;

import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.beans.DecisionTreeCTABean;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.Utils;

public class DecisionTreeSlideComponent extends BaseUsePojo {

	private static final Logger LOGGER = LoggerFactory.getLogger(DecisionTreeSlideComponent.class);
	private List<DecisionTreeCTABean> ctaList = null;
	private int noOfItems;
	private String currentSlideAjaxUrl=null;
	private String currentSlidePageAjaxUrl=null;
	private String currentSlideUrl=null;
	private SuntrustDotcomService dotcomService;
	private String runMode;

	@Override
	public void activate() throws Exception {

		LOGGER.info("Retrieving CTA Bean List");
		ctaList = populateBeanList("ctaItems", DecisionTreeCTABean.class);
		boolean isPublish = Utils.isPublishRunMode(getSlingScriptHelper().getService(SlingSettingsService.class));
		dotcomService = getSlingScriptHelper().getService(SuntrustDotcomService.class);		
		ctaList.stream().forEach(cta -> {
			try {
				/*if(isPublish)
					cta.setRelativeCtaURL(Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),cta.getCtaurl(),getResourceResolver()));
				else*/
					cta.setRelativeCtaURL(Utils.getModifyURL(StringUtils.defaultString(cta.getCtaurl()),
						StringUtils.defaultString(cta.getCtaurlparam()), StringUtils.defaultString(cta.getCtatag())));
					cta.setCtaAjaxUrl("ExternalUrl");
					cta.setCtaPageAjaxUrl("External");
					LOGGER.info("CTA URl: " + cta.getCtaurl());
					if (getPageManager().getPage(cta.getCtaurl()).getParent().getContentResource().adaptTo(Node.class).getNode("root/responsivegrid/").hasNodes()
									&& ("decisiontree").equals(getPageManager().getPage(cta.getCtaurl()).getParent().getContentResource().adaptTo(Node.class).getNode("root/responsivegrid/").getNodes().nextNode().getName()))
						
						if (getResourceResolver().getResource(cta.getCtaurl()).adaptTo(Node.class).getNode("jcr:content/root/responsivegrid/").hasNodes()){
							if(isPublish){
								cta.setCtaAjaxUrl(Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),getResourceResolver().getResource(cta.getCtaurl()).adaptTo(Node.class).getNode("jcr:content/root/responsivegrid/").getNodes().nextNode().getPath(),getResourceResolver()));
								cta.setCtaPageAjaxUrl(Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),getResourceResolver().getResource(cta.getCtaurl()).adaptTo(Node.class).getNode("jcr:content/root/responsivegrid/").getPath(),getResourceResolver()));
							}
							else{	
								cta.setCtaAjaxUrl(Utils.getModifyURL(getResourceResolver().getResource(cta.getCtaurl()).adaptTo(Node.class).getNode("jcr:content/root/responsivegrid/").getNodes().nextNode().getPath(),"wcmmode=disabled",""));
								cta.setCtaPageAjaxUrl(Utils.getModifyURL(getResourceResolver().getResource(cta.getCtaurl()).adaptTo(Node.class).getNode("jcr:content/root/responsivegrid/").getPath(),"wcmmode=disabled",""));
							}
						}
			} catch (Exception e) {
				LOGGER.error("Error Encountered in setting CTA Ajax URL", e);
			}
		});
		
		this.runMode= isPublish ? "publish" : "author";
		
		this.noOfItems = ctaList.size();
		if(getResourcePage().adaptTo(Node.class).getNode("jcr:content/root/responsivegrid/").hasNodes())
		if(isPublish){
			this.currentSlideAjaxUrl=Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),getResourcePage().adaptTo(Node.class).getNode("jcr:content/root/responsivegrid/").getNodes().nextNode().getPath(),getResourceResolver());
			this.currentSlidePageAjaxUrl=Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),getResourcePage().adaptTo(Node.class).getNode("jcr:content/root/responsivegrid/").getPath(),getResourceResolver());
			this.currentSlideUrl=Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),getResourcePage().getPath(),getResourceResolver());
		}
		else{
			this.currentSlideAjaxUrl=Utils.getModifyURL(getResourcePage().adaptTo(Node.class).getNode("jcr:content/root/responsivegrid/").getNodes().nextNode().getPath(),"wcmmode=disabled","");
			this.currentSlidePageAjaxUrl=Utils.getModifyURL(getResourcePage().adaptTo(Node.class).getNode("jcr:content/root/responsivegrid/").getPath(),"wcmmode=disabled","");
			this.currentSlideUrl=Utils.getModifyURL(getResourcePage().getPath(),"","");
		}

	}

	public List<DecisionTreeCTABean> getCtaList() {
		return ctaList;
	}

	public int getNoOfItems() {
		return noOfItems;
	}

	public String getCurrentSlideAjaxUrl() {
		return currentSlideAjaxUrl;
	}

	public String getRunMode() {
		return runMode;
	}

	public String getCurrentSlideUrl() {
		return currentSlideUrl;
	}

	public String getCurrentSlidePageAjaxUrl() {
		return currentSlidePageAjaxUrl;
	}

	public void setCurrentSlidePageAjaxUrl(String currentSlidePageAjaxUrl) {
		this.currentSlidePageAjaxUrl = currentSlidePageAjaxUrl;
	}
	
}
