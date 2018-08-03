package com.suntrust.dotcom.utils;


import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.suntrust.dotcom.config.SuntrustDotcomService;

/**
 * Purpose - The AnalyticsGlobalDataLayerHelper program provides backend logic support to assign custom values to Global Data Layer variable.
 * @author UGRK104
 */
public class AnalyticsGlobalDataLayerHelper extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AnalyticsGlobalDataLayerHelper.class);

	/** instance variable to hold pageName value.pageName is would be STcom|AEM|L1|L2|..|currentPageName * */
	private String pageName;
    /** instance variable to hold hier1 value. * */
	private String hier1;
	/** instance variable to hold pageSet value. * */
	private String pageSet;
	/** instance variable to hold taxonomyPageClass value. * */
	private String taxonomyPageClass;
    /** instance variable to hold taxonomyProducts value. * */
	private String taxonomyProducts;
	/** instance variable to hold taxonomySegment value. * */
	private String taxonomySegment;
	/** instance variable to hold taxonomyLOB value. * */
	private String taxonomyLOB;
	/** instance variable to hold taxonomyCircumstance value. * */
	private String taxonomyCircumstance;
	/** instance variable to hold taxonomyContentType value. * */
	private String taxonomyContentType;
	/** instance variable to hold pageURL value. * */
	private String pageURL;
	/** instance variable to hold siteLanguage value. * */
	private String siteLanguage;
	/** instance variable to hold request value. * */
	private SlingHttpServletRequest request;
	/** instance variable to hold resourceResolver value. * */
	private ResourceResolver resourceResolver = null;
	/** instance variable to hold genericUtil value. * */
	private GenericUtil genericUtil = null;
	/** instance variable to hold externalSearch value. * */
	private String externalSearch;
	/** instance variable to hold pageTitle value. * */
	private String pageTitle;
	/** instance variable to hold pageDescription value. * */
	private String pageDescription;
	/** instance variable to hold canonicalUrl value. * */
	private String canonicalUrl;
	/** instance variable to hold DFS page path. * */
	private String dfsPagePath;
	/** instance variable to hold PageID. * */
	private String pageId;
	/** instance variable to hold absoluteImagePath. * */
	private String absoluteImagePath;
	/* (non-Javadoc)
	 * @see com.adobe.cq.sightly.WCMUsePojo#activate()
	 */
	@Override
	public void activate(){ 

		LOGGER.debug("Invoked AnalyticsGlobalDataLayerHelper activate method");
		
		try
		{
			resourceResolver = getResourceResolver();
			genericUtil = new GenericUtil();
			String currentPageTemplateTitle;
			SuntrustDotcomService dotcomService = getSlingScriptHelper().getService(SuntrustDotcomService.class);
			String templateName = "";
			Page currPage = null;
			request = getRequest();
			String currentPageURL = null;
			
			currPage = getCurrentPage();
			templateName = getCurrentPage().getTemplate().getName();
			currentPageURL = getCurrentPage().getPath();
			
			
			if(templateName.contains("wrapper"))
			{
				//check for suffix
				String suffix = request.getRequestPathInfo().getSuffix();
				if(StringUtils.isNotBlank(suffix))
				{
					if(suffix.endsWith(".html"))
					{
						suffix = suffix.substring(0,suffix.indexOf(".html"));
					}
					if(getPageManager().getPage(suffix) != null) {
						currPage = getPageManager().getPage(suffix);
						currentPageURL =Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),currPage.getPath(),resourceResolver);
					}
				}

			}
			/*Set current page id*/
			if(currPage.getProperties().containsKey("pageid"))
				pageId = currPage.getProperties().get("pageid","");
			setPageThumbnailImage(currPage);
			currentPageTemplateTitle = AnalyticsEnum.STCOM_DOTCOM_AEM_ANALYTIC_TAXONOMY_PAGE_CLASS.getValue();
			
			currentPageTemplateTitle = currPage.getTemplate().getTitle();
			
			currentPageTemplateTitle = StringUtils.isEmpty(currentPageTemplateTitle) ? currPage.getTemplate().getName() : currentPageTemplateTitle;
 		// Get the list of all tags linked to the page.
			List<Tag> tags = genericUtil.getLinkedTagsToResorce(currPage , resourceResolver);
			LOGGER.info("Current Page ==>"+currPage.getName());
			setPageName(currentPageURL,currPage);
			setHier1(getPageName(),currPage);
			setPageSet(AnalyticsEnum.STCOM_DOTCOM_AEM_ANALYTIC_PAGE_SET.getValue());
			setTaxonomyPageClass(currentPageTemplateTitle,currPage);
			String port = Integer.toString(request.getServerPort());
			String fullPageUrl = request.getScheme()+"://"+request.getServerName()+currentPageURL;
			if(request.getServerName().equals("localhost")){
				fullPageUrl = request.getScheme()+"://"+request.getServerName()+":"+port+currentPageURL;
			}
			setPageURL(fullPageUrl);
			setSiteLanguage(genericUtil.getPageLanguage(currPage));
			analyticsTaxonomyValueGeneration(tags , genericUtil);
			setExternalSearch(currPage.getProperties().containsKey("externalsearch") ? (String) currPage.getProperties().get("externalsearch") : "false");
			setPageTitle(currPage.getProperties().containsKey("pageTitle") ? (String) currPage.getProperties().get("pageTitle") : (String) currPage.getProperties().get("jcr:title"));
			setPageDescription(currPage.getProperties().containsKey("jcr:description") ? (String) currPage.getProperties().get("jcr:description") : "");
			String canonicalpath = Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),currPage.getPath(),resourceResolver);
			setCanonicalUrl(Utils.getFullCanonicalUrl(resourceResolver, canonicalpath));
		}
		catch (Exception exception) {		
			LOGGER.error("Exception cought in AnalyticsGlobalDataLayerHelper activate method : " , exception);
		}			
		
	}

	/**
	 * @return pageTitle value
	 */
	public String getPageTitle() {
		return pageTitle;
	}

	
	/**
	 * @param pageTitle {@link String}
	 */
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	/**
	 * @return pageDescription value
	 */
	public String getPageDescription() {
		return pageDescription;
	}

	/**
	 * @param pageDescription {@link String}
	 */
	public void setPageDescription(String pageDescription) {
		this.pageDescription = pageDescription;
	}


	/**
	 * @return external search value
	 */
	public String getExternalSearch() {
		return externalSearch;
	}

	/**
	 * @param externalSearch {@link String}
	 */
	public void setExternalSearch(String externalSearch) {
		this.externalSearch = externalSearch;
	}

	
	/**
	 * @return page name value
	 */
	public String getPageName() {
		return this.pageName;
	}

	/**
	 * @return encoded page id value
	 */
	public String getPageId() {
		return this.pageId;
	}

	/**
	 * @param relativeURLPathString {@link String}
	 * @param currPage {@link Page}
	 */
	private void setPageName(final String relativeURLPathString, final Page currPage) {
		
		String relativeURLPath = relativeURLPathString;
		
		String dfsPrefix = "";
		
		SuntrustDotcomService dotcomService = getSlingScriptHelper().getService(SuntrustDotcomService.class);
		dfsPagePath = dotcomService.getPropertyValue("newaccountstart.content.path");
		
		if(StringUtils.contains(relativeURLPath, dfsPagePath)) {
			dfsPrefix = GenericEnum.PIPE_SYMBOL.getValue() + AnalyticsEnum.STCOM_DFS_AEM_ANALYTIC_PREFIX.getValue();
		}

		if(null!= currPage.getTitle() && currPage.getTitle().equalsIgnoreCase("English"))
		{
			this.pageName = AnalyticsEnum.STCOM_DOTCOM_AEM_ANALYTIC_PREFIX.getValue() + dfsPrefix + GenericEnum.PIPE_SYMBOL.getValue() + AnalyticsEnum.STCOM_DOTCOM_AEM_HOMEPAGE.getValue();
		}

		else{
			try {				
				//start US37717
				relativeURLPath = relativeURLPath.replaceAll(AnalyticsEnum.CONTENT_SUNTRUST_DOTCOM_US_EN_LOCATIONS.getValue(), GenericEnum.EMPTY_STRING.getValue());
				relativeURLPath = relativeURLPath.replaceAll(AnalyticsEnum.CONTENT_SUNTRUST_DOTCOM_US_ES_LOCATIONS.getValue(), GenericEnum.EMPTY_STRING.getValue());
				//end US37717
				
				relativeURLPath = relativeURLPath.replaceAll(AnalyticsEnum.CONTENT_SUNTRUST_DOTCOM_US_EN.getValue(), GenericEnum.EMPTY_STRING.getValue());
				relativeURLPath = relativeURLPath.replaceAll(AnalyticsEnum.CONTENT_SUNTRUST_DOTCOM_US_ES.getValue(), GenericEnum.EMPTY_STRING.getValue());
				relativeURLPath = relativeURLPath.replaceAll(AnalyticsEnum.CONTENT_SUNTRUST_DOTCOM_US_CONFIGURATION.getValue(), GenericEnum.EMPTY_STRING.getValue());
				relativeURLPath = relativeURLPath.replace(GenericEnum.HTML_EXTENSION.getValue(), GenericEnum.EMPTY_STRING.getValue());

				this.pageName = relativeURLPath.replaceAll(GenericEnum.BACKWORD_SLASH_SYMBOL.getValue(), GenericEnum.PIPE_SYMBOL.getValue());
				
				if(StringUtils.contains(relativeURLPath, dfsPagePath)) { // DFS page
					
					if(StringUtils.endsWith(relativeURLPath, dfsPagePath)) { // /new-account-start
						this.pageName = AnalyticsEnum.STCOM_DOTCOM_AEM_ANALYTIC_PREFIX.getValue() + dfsPrefix;
					} else { // /new-account-start/firstdeposit
						if(!currPage.getTitle().isEmpty() && !currPage.getTitle().trim().equals(GenericEnum.EMPTY_STRING.getValue())) {
							this.pageName = AnalyticsEnum.STCOM_DOTCOM_AEM_ANALYTIC_PREFIX.getValue() + dfsPrefix + GenericEnum.PIPE_SYMBOL.getValue() + currPage.getTitle();
						}
					}
					this.pageName = this.pageName.replaceAll(GenericEnum.EMPTY_SPACE_STRING.getValue(), GenericEnum.EMPTY_STRING.getValue());
					this.pageName = this.pageName.replaceAll("and", "&");
				} else { // not DFS page 
					this.pageName = AnalyticsEnum.STCOM_DOTCOM_AEM_ANALYTIC_PREFIX.getValue() + dfsPrefix + this.pageName;
				}
			} catch (Exception e) {
				LOGGER.info("Current aem page url is not correct. Message: {}, Trace: {} ", e.getMessage(),e);
			}

		}

	}

	/**
	 * @return hier1 value
	 */
	public String getHier1() {
		return this.hier1;
	}

	/**
	 * @param pageName {@link String}
	 * @param currPage {@link Page}
	 */
	private void setHier1(final String pageName, final Page currPage) {

		String curentPageTitle = ""; //(StringUtils.defaultString(currPage.getTitle(), "")).trim();
		if(null!=currPage.getTitle())
			curentPageTitle=currPage.getTitle().trim();

		if (curentPageTitle.equalsIgnoreCase(GenericEnum.ENGLISH_LANGUAGE.getValue()) || curentPageTitle.equalsIgnoreCase(GenericEnum.SPEEDBUMP_PAGE_NAME.getValue())) {
			this.hier1 = AnalyticsEnum.STCOM_DOTCOM_AEM_ANALYTIC_PREFIX.getValue();
		}
		else{
			try
			{
				this.hier1 = pageName.substring(0, pageName.lastIndexOf(GenericEnum.PIPE_SYMBOL.getValue()));
			}
			catch(StringIndexOutOfBoundsException stringIndexOutOfBoundsException)
			{
				LOGGER.debug("Exception captured: Messgae: {}, Trace: {}",stringIndexOutOfBoundsException.getMessage(),stringIndexOutOfBoundsException);
				this.hier1 = pageName;
			}
			catch(Exception exception)
			{
				LOGGER.debug("Exception captured: Messgae: {}, Trace: {}",exception.getMessage(),exception);
				this.hier1 = pageName;
			}
		}
		LOGGER.debug("Analytics hier1 : " + this.hier1);
	}

	/**
	 * @return pageSet value.
	 */
	public String getPageSet() {
		return this.pageSet;
	}

	/**
	 * @param pageSet {@link String}
	 */
	private void setPageSet(String pageSet) {
		this.pageSet = pageSet;
	}

	/**
	 * @return taxonomy page class value.
	 */
	public String getTaxonomyPageClass() {
		return this.taxonomyPageClass;
	}

	/**
	 * @param taxonomyPageClass {@link String}
	 * @param currPage {@link Page}
	 */
	private void setTaxonomyPageClass(final String taxonomyPageClass,final Page currPage) {

		String currentPageTemplateName = taxonomyPageClass;
		currentPageTemplateName = currentPageTemplateName.replaceAll(GenericEnum.EMPTY_SPACE_STRING.getValue(), GenericEnum.DASH_SYMBOL.getValue());
		currentPageTemplateName = currentPageTemplateName.toLowerCase(currPage.getLanguage(false));
		this.taxonomyPageClass = currentPageTemplateName;
	}

	/**
	 * @return taxonomy products value.
	 */
	public String getTaxonomyProducts() {
		return this.taxonomyProducts;
	}

	/**
	 * @param taxonomyProducts {@link String}
	 */
	private void setTaxonomyProducts(String taxonomyProducts) {
		this.taxonomyProducts = taxonomyProducts;
	}

	/**
	 * @return taxonomy segment value
	 */
	public String getTaxonomySegment() {
		return this.taxonomySegment;
	}

	/**
	 * @param taxonomySegment {@link STring}
	 */
	private void setTaxonomySegment(final String taxonomySegment) {
		this.taxonomySegment = taxonomySegment;
	}


	/**
	 * @return taxonomy content type value
	 */
	public String getTaxonomyContentType() {
		return taxonomyContentType;
	}

	/**
	 * @param taxonomyContentType {@link String}
	 */
	public void setTaxonomyContentType(final String taxonomyContentType) {
		this.taxonomyContentType = taxonomyContentType;
	}


	/**
	 * @return taxonomy LOB value.
	 */
	public String getTaxonomyLOB() {
		return this.taxonomyLOB;
	}

	/**
	 * @param taxonomyLOB {@link String}
	 */
	private void setTaxonomyLOB(final String taxonomyLOB) {
		this.taxonomyLOB = taxonomyLOB;
	}

	/**
	 * @return taxonomyCircumstance value
	 */
	public String getTaxonomyCircumstance() {
		return this.taxonomyCircumstance;
	}

	/**
	 * @param taxonomyCircumstance {@link String}
	 */
	private void setTaxonomyCircumstance(final String taxonomyCircumstance) {
		this.taxonomyCircumstance = taxonomyCircumstance;
	}

	/**
	 * @return pageURL value.
	 */
	public String getPageURL() {
		return this.pageURL;
	}

	/**
	 * @param pageURL {@link String}
	 */
	private void setPageURL(final String pageURL) {

		String queryString = request.getQueryString();

		if (queryString == null) {
			this.pageURL = pageURL;
		} else {
			this.pageURL = pageURL + GenericEnum.QUERYSTRING_QUESTION_MARK.getValue() + queryString;
		}
	}

	/**
	 * @return siteLanguage value
	 */
	public String getSiteLanguage() {
		return this.siteLanguage;
	}

	/**
	 * @param siteLanguage {@link String}
	 */
	private void setSiteLanguage(final String siteLanguage) {
		this.siteLanguage = siteLanguage;
	}


	/**
	 * The purpose of this method is to set custom value to GDL taxonomy variables.
	 * @param tags
	 */
	private void analyticsTaxonomyValueGeneration(final List<Tag> tags , final GenericUtil genericUtil)
	{
		List<Tag> linkedTags = tags;
		StringBuffer taxonomyProductsBuffer = new StringBuffer();
		StringBuffer taxonomyLOBBuffer = new StringBuffer();
		StringBuffer taxonomyCircumstanceBuffer = new StringBuffer();
		StringBuffer taxonomySegmentBuffer = new StringBuffer();
		StringBuffer taxonomyContentTypeBuffer = new StringBuffer();
		String taxonomyProductsString;
		String taxonomyLOBString;
		String taxonomyCircumstanceString;
		String taxonomySegmentString;
		String taxonomyContentTypeString;

		for(Tag tag : linkedTags)
		{

			// Checking /etc/tags/Product namespace
			if (AnalyticsEnum.PRODUCTS_TAG_NAMESPACE_NAME.getValue().equalsIgnoreCase(tag.getNamespace().getName())) {
				taxonomyProductsBuffer.append(tag.getTitle()).append(GenericEnum.SEMI_COLON_SYMBOL.getValue());
			}

			// Checking /etc/tags/Line of Busines namespace
			else if (AnalyticsEnum.LOB_TAG_NAMESPACE_NAME.getValue().equalsIgnoreCase(tag.getNamespace().getName())) {
				taxonomyLOBBuffer.append(tag.getTitle()).append(GenericEnum.SEMI_COLON_SYMBOL.getValue());
			}

			// Checking /etc/tags/Resource Center namespace
			else if (AnalyticsEnum.RESOURCE_CENTER_TAG_NAMESPACE_NAME.getValue().equalsIgnoreCase(tag.getNamespace().getName())) {
				taxonomyCircumstanceBuffer.append(tag.getTitle()).append(GenericEnum.SEMI_COLON_SYMBOL.getValue());
			}

			// Checking /etc/tags/Advisor Specialty namespace
			else if (AnalyticsEnum.SEGMENT_TAG_NAMESPACE_NAME.getValue().equalsIgnoreCase(tag.getNamespace().getName())) {
				taxonomySegmentBuffer.append(tag.getTitle()).append(GenericEnum.SEMI_COLON_SYMBOL.getValue());
			}
			// Checking /etc/tags/content-type namespace
			else if (AnalyticsEnum.CONTENT_TYPE_TAG_NAMESPACE_NAME.getValue().equalsIgnoreCase(tag.getNamespace().getName())) {
				taxonomyContentTypeBuffer.append(tag.getTitle()).append(GenericEnum.SEMI_COLON_SYMBOL.getValue());
			}


		}

		taxonomyProductsString = genericUtil.removeLastSpecialCharacter(taxonomyProductsBuffer.toString(), GenericEnum.SEMI_COLON_SYMBOL.getValue());
		taxonomyLOBString = genericUtil.removeLastSpecialCharacter(taxonomyLOBBuffer.toString(), GenericEnum.SEMI_COLON_SYMBOL.getValue());
		taxonomyCircumstanceString = genericUtil.removeLastSpecialCharacter(taxonomyCircumstanceBuffer.toString(), GenericEnum.SEMI_COLON_SYMBOL.getValue());
		taxonomySegmentString = genericUtil.removeLastSpecialCharacter(taxonomySegmentBuffer.toString(), GenericEnum.SEMI_COLON_SYMBOL.getValue());
		taxonomyContentTypeString = genericUtil.removeLastSpecialCharacter(taxonomyContentTypeBuffer.toString(), GenericEnum.SEMI_COLON_SYMBOL.getValue());


		setTaxonomyProducts(taxonomyProductsString);
		setTaxonomyCircumstance(taxonomyCircumstanceString);
		setTaxonomyLOB(taxonomyLOBString);
		setTaxonomySegment(taxonomySegmentString);
		setTaxonomyContentType(taxonomyContentTypeString);
	}

	public String getPageThumbnailPath() {
		String pageThumbnail = "";
		String fullImagePath = "";
		try {
			pageThumbnail =  genericUtil.getPageThumbnailPath(getCurrentPage().getPath(),resourceResolver);
			String port = Integer.toString(request.getServerPort());
			fullImagePath = request.getScheme()+"://"+request.getServerName()+pageThumbnail;
			if(request.getServerName().equals("localhost")){
				fullImagePath = request.getScheme()+"://"+request.getServerName()+":"+port+pageThumbnail;
			}
		} catch (RepositoryException e) {
			LOGGER.debug("Exception captured: Messgae: {}, Trace: {}",e.getMessage(), e);
		}
		return fullImagePath;
	}
	
	public void setPageThumbnailImage(Page currentPage){
		try {
			Externalizer externalizer = getSlingScriptHelper().getService(Externalizer.class);
			SlingSettingsService slingSettings = getSlingScriptHelper().getService(SlingSettingsService.class);
			String pageThumbnail = genericUtil.getPageThumbnailPath(currentPage.getPath(), resourceResolver);
			LOGGER.info("Page Thumnail Path : {}",pageThumbnail);
			if (Utils.isPublishRunMode(slingSettings)) {
				this.absoluteImagePath = externalizer.publishLink(resourceResolver, pageThumbnail);
			} else {
				this.absoluteImagePath = externalizer.authorLink(resourceResolver, pageThumbnail);
			}
			LOGGER.info("Absolute Thumnail Image Path : {}",absoluteImagePath);
		} catch (Exception e) {
			LOGGER.error("Exception captured: Messgae: {}, Trace: {}", e.getMessage(), e);
		}		
	}

	/**
	 * @return absoluteImagePath value
	 */
	public String getPageThumbnailImage() {
		return absoluteImagePath;
	}
	
	/**
	 * @return caconicalURL value
	 */
	public String getCanonicalUrl() {
		return canonicalUrl;
	}

	/**
	 * @param canonicalUrl {@link String}
	 */
	public void setCanonicalUrl(String canonicalUrl) {
		this.canonicalUrl = canonicalUrl;
	}
	
	
}