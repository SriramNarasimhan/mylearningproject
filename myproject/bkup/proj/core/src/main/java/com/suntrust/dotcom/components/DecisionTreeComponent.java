package com.suntrust.dotcom.components;

import java.io.IOException;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.beans.DecisonTreeStepItems;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.GenericEnum;
import com.suntrust.dotcom.utils.Utils;

public class DecisionTreeComponent extends BaseUsePojo{
	
	private List<DecisonTreeStepItems> stepItems=null;
	private List<DecisonTreeStepItems> stepItemsforSlides=null;
	private boolean showNavButton=false;
	private String theme=null;
	private String desktopImage=null;
	private String retinaImage=null;
	private String mobileImage = null;
	private String stepColor = null;
	private String isPageCarousel = null;
	private String hideStepsDesktop = null;
	private String hideStepsMobile = null;
	private SuntrustDotcomService dotcomService;
	private boolean isPublish;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DecisionTreeComponent.class);
	@Override
	public void activate() throws Exception {

		LOGGER.info("Retreiving stepItems and corresponding slides.");
		dotcomService= getSlingScriptHelper().getService(SuntrustDotcomService.class);
		isPublish = Utils.isPublishRunMode(getSlingScriptHelper().getService(SlingSettingsService.class));
		stepItems = populateBeanList("stepItems", DecisonTreeStepItems.class);
		stepItems.stream().forEach(steps -> {
			if(null!=steps.getSlideItems()){
				try {
					if(null!=steps.getSlideItems()){
						/*if(isPublish)
							steps.setFirstSlideURL(Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),steps.getSlideItems().get(0).getSlidepath(),getResourceResolver()));
						else*/
							steps.setFirstSlideURL(steps.getSlideItems().get(0).getSlidepath());
							if(isPublish){
								steps.setFirstSlideAjaxURL(Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),getResourceResolver().getResource(steps.getSlideItems().get(0).getSlidepath()).adaptTo(javax.jcr.Node.class).getNode("jcr:content/root/responsivegrid/").getNodes().nextNode().getPath(),getResourceResolver()));
								steps.setFirstSlidePageAjaxURL(Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),getResourceResolver().getResource(steps.getSlideItems().get(0).getSlidepath()).adaptTo(javax.jcr.Node.class).getNode("jcr:content/root/responsivegrid/").getPath(),getResourceResolver()));
							}
							
							else{
								steps.setFirstSlideAjaxURL(Utils.getModifyURL(getResourceResolver().getResource(steps.getSlideItems().get(0).getSlidepath()).adaptTo(javax.jcr.Node.class).getNode("jcr:content/root/responsivegrid/").getNodes().nextNode().getPath(),"wcmmode=disabled",""));
								steps.setFirstSlidePageAjaxURL(Utils.getModifyURL(getResourceResolver().getResource(steps.getSlideItems().get(0).getSlidepath()).adaptTo(javax.jcr.Node.class).getNode("jcr:content/root/responsivegrid/").getPath(),"wcmmode=disabled",""));
							}
							steps.setFirstSlideresourceURL(getResourceResolver().getResource(steps.getSlideItems().get(0).getSlidepath()).adaptTo(javax.jcr.Node.class).getNode("jcr:content/root/responsivegrid/").getNodes().nextNode().getPath());
							
					}
				} catch (Exception e) {
					LOGGER.error("error in getting slide items",e);
				}
			}
		});
		ValueMap map=null;
		Resource mainComponentNode = getResourcePage().getParent().getContentResource("root/responsivegrid/decisiontree");
		if(null!=mainComponentNode){
			map=mainComponentNode.getValueMap();
			if(map.containsKey("showNextPrevNav")) {
				this.showNavButton = map.get("showNextPrevNav", Boolean.class);
			}
			if(map.containsKey("fontTheme")) {
				this.theme = map.get("fontTheme",String.class);
			}
			if(map.containsKey("normalbgimg")) {
				this.desktopImage = map.get("normalbgimg",String.class);
			}
			if(map.containsKey("retinabgimg")) {
				this.retinaImage = map.get("retinabgimg",String.class);
			}
			if(map.containsKey("mobileimg")) {
				this.mobileImage = map.get("mobileimg",String.class);
			}
			if(map.containsKey("stepColor")) {
				this.stepColor = map.get("stepColor",String.class);
			}
			
			if(map.containsKey("showPageCarousel")) {
				this.isPageCarousel = map.get("showPageCarousel", String.class);
			}
			
			if(map.containsKey("hidestepsindesktop") && map.get("hidestepsindesktop", Boolean.class)) {
				this.hideStepsDesktop = "hideinDesktop";
			}
			if(map.containsKey("hidestepsinmobile") && map.get("hidestepsinmobile", Boolean.class)) {
				this.hideStepsMobile = "hideinMobile";
			}
		}
	}
	
	public List<DecisonTreeStepItems> getStepListforSlides(){
		
		try {
		String pagePath=get(GenericEnum.PARENT_PAGE_PATH.getValue(), String.class);
		String currentPagePath=get(GenericEnum.PAGE_PATH.getValue(), String.class);
		String nodePathInsidePage="/jcr:content/root/responsivegrid/decisiontree";
		
		stepItemsforSlides =populateBeanList(pagePath+nodePathInsidePage, "stepItems", DecisonTreeStepItems.class);
		
		stepItemsforSlides.stream().forEach(steps ->{
			try{
			if(null!=steps.getSlideItems()){
				/*if(isPublish)
					steps.setFirstSlideURL(Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),steps.getSlideItems().get(0).getSlidepath(),getResourceResolver()));
				else*/
					steps.setFirstSlideURL(Utils.getModifyURL(StringUtils.defaultString(steps.getSlideItems().get(0).getSlidepath()),"",""));
				if(getResourceResolver().getResource(steps.getSlideItems().get(0).getSlidepath()).adaptTo(javax.jcr.Node.class).getNode("jcr:content/root/responsivegrid/").hasNodes()){
					if(isPublish){
						steps.setFirstSlideAjaxURL(Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),getResourceResolver().getResource(steps.getSlideItems().get(0).getSlidepath()).adaptTo(javax.jcr.Node.class).getNode("jcr:content/root/responsivegrid/").getNodes().nextNode().getPath(),getResourceResolver()));
						steps.setFirstSlidePageAjaxURL(Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),getResourceResolver().getResource(steps.getSlideItems().get(0).getSlidepath()).adaptTo(javax.jcr.Node.class).getNode("jcr:content/root/responsivegrid/").getPath(),getResourceResolver()));
					}
					else{
						steps.setFirstSlideAjaxURL(Utils.getModifyURL(getResourceResolver().getResource(steps.getSlideItems().get(0).getSlidepath()).adaptTo(javax.jcr.Node.class).getNode("jcr:content/root/responsivegrid/").getNodes().nextNode().getPath(),"wcmmode=disabled",""));
						steps.setFirstSlidePageAjaxURL(Utils.getModifyURL(getResourceResolver().getResource(steps.getSlideItems().get(0).getSlidepath()).adaptTo(javax.jcr.Node.class).getNode("jcr:content/root/responsivegrid/").getPath(),"wcmmode=disabled",""));
					}
				}
				steps.setStepSelected("");
			steps.getSlideItems().stream().forEach(slides -> {
				if(slides.getSlidepath().equals(currentPagePath))
					steps.setStepSelected("sun-active");
			});
			}
			}
			catch(Exception e){
				LOGGER.error("error in getting slide items",e);
			}
		});
		
		} 
		catch (LoginException | RepositoryException | IllegalStateException | IOException e) {
			LOGGER.error("Encountered error while retrieving step list ==>",e);
		}
		return stepItemsforSlides;
	}
	
	public List<DecisonTreeStepItems> getStepItems() {
		return stepItems;
	}

	public boolean getShowNavButton() {
		return showNavButton;
	}

	public String getTheme() {
		return theme;
	}

	public String getDesktopImage() {
		return desktopImage;
	}

	public String getRetinaImage() {
		return retinaImage;
	}
	public String getMobileImage() {
		return mobileImage;
	}
	
	public String getStepColor() {
		return stepColor;
	}

	public String getPageCarousel() {
		return isPageCarousel;
	}
	
	public String getHideStepsDesktop() {
		return hideStepsDesktop;
	}

	public String getHideStepsMobile() {
		return hideStepsMobile;
	}
	

}
