package com.suntrust.dotcom.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetManager;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.commons.WCMUtils;
import com.suntrust.dotcom.beans.SearchBean;
import com.suntrust.dotcom.config.STELConfigService;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.ServiceUtils;


/**
 * Purpose - The GenericUtil program provides the common utility methods which
 * can be used in other classes based on the needs.
 *
 * @author UGRK104
 */

public class GenericUtil extends WCMUsePojo {

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericUtil.class);
	
	/** Image constant */
	public static final String IMAGE = "image";
	
	//private ResourceResolver resourceResolver = null;
	
	/** ServiceUtils class reference variable */
	private static ServiceUtils serviceUtils = null;
	
	
	/**
	 * Returns child list for given page
	 * 
	 * @return List<Page>
	 */
	public List<Page> getChildPagesList(){
		Page iterativePage = get("iterativePage", Page.class);
		List<Page> pages;
		if (iterativePage != null) {
			pages = getChildPagesList(iterativePage,getRequest());
			return pages;
		}
		return null;
	}
	
	/**
     * This method is used to check the url browsed in the component dialog pathbrowser field
     *
     * @param url
     * @return URL String
     */
     public String getUrlCheck()
     {
         String stringURL = get("helpCenterLink", String.class);
         return Utils.urlCheck(stringURL);
     }
	
	/**
	 * Returns child list count for given page
	 * 
	 * @return int
	 */
	public int getChildPagesListCount(){
		Page iterativePage = get("iterativePage", Page.class);
		List<Page> pages;
		if (iterativePage != null) {
			pages = getChildPagesList(iterativePage,getRequest());
			return pages.size();
		}
		return 0;
	}
	

	/**
	 * @param page
	 *            - aem page reference.
	 * @param slingHttpServletRequest
	 * @return list of child pages of the page passed as argument in the method.
	 */
	public List<Page> getChildPagesList(Page page,
			SlingHttpServletRequest slingHttpServletRequest) {
		List<Page> childPages = new ArrayList<Page>();
		Page childPage = null;
		try {
			if (page != null) {
				Iterator<Page> pageItr = page.listChildren(new PageFilter(
						slingHttpServletRequest));
				while (pageItr.hasNext()) {
					childPage = pageItr.next();
					childPages.add(childPage);
				}
			}

		} catch (Exception e) {
			LOGGER.error("Error while getting child pages :: ", e);
		}
		return childPages;
	}
	
	/**
	 * Returns odd index childs for given page 
	 * 
	 * @return List<Page>
	 */
	public List<Page> getOddIndexChildPages()
	{
		Page iterativePage = get("iterativePage", Page.class);
		List<Page> childPages = getChildPagesList(iterativePage, getRequest());
		List<Page> pages = getOddIndexChildPages(childPages);
		return pages;		
	}
	
	/**
	 * Returns odd index childs for given page list 
	 * 
	 * @return List<Page>
	 */
	public List<Page> getOddIndexChildPages(List<Page> pages)
	{
		List<Page> oddIndexChildPages = new ArrayList<Page>();
		int count = 1;
		for (Page page : pages) {
			if (count % 2 != 0) {
				oddIndexChildPages.add(page);
			}
			count++;
		}
		
		return oddIndexChildPages;		
	}

	/**
	 * Purpose - Get the Page came from sightly call and then get the next
	 * sibling page of it.
	 * 
	 * @return List<Page>
	 */
	public Page getSiblingPage() {
		Page iterativePage = get("iterativePage", Page.class);
		Page siblingPage;
		if (iterativePage != null) {
			siblingPage = getSiblingPage(iterativePage);
			return siblingPage;
		}
		return getCurrentPage();

	}

	/**
	 * @param page
	 *            - aem page reference.
	 * @param slingHttpServletRequest
	 * @return the next sibling page to the page passed as argument in the
	 *         method. If there is no next sibling than the method will return the page which is passed as argument to the method.
	 */
	public Page getSiblingPage(Page page) {
		Page siblingPage;
		Page parrentPage = page.getParent();

		List<Page> pages = new ArrayList<Page>();
		pages = getChildPagesList(parrentPage, getRequest());
		
		
		try
		{
		int i = pages.indexOf(page);

		siblingPage = pages.get(i + 1);
		}
		catch(IndexOutOfBoundsException indexOutOfBoundsException)
		{
			LOGGER.debug("Passed page in the method getSiblingPage() is not the children of parent page : " + indexOutOfBoundsException.getMessage());
			siblingPage = page;
		}
		catch(Exception exception)
		{
			LOGGER.debug("Exception cought in getSiblingPage() : " + exception.getMessage());
			siblingPage = page;
		}

		return siblingPage;

	}
	
	/**
	 * Returns Hyphen seperated value
	 * 
	 * @return String
	 */
	public String getHyphenSeperatedValue()
	{
		String pagePath = get("pagePath", String.class);
		String string2 = pagePath;
		string2 = string2.replaceFirst("/", "");
		string2 = string2.replaceAll("/", "-");
		return string2;
	}
	
	/**
	 * Returns roots page child list
	 * 
	 * @return String
	 */
	public String getHyphenSeperatedParentPagePath()
	{
		Page iterativePage = get("iterativePage", Page.class);
		String pagePath = iterativePage.getParent().getPath();
		String string2 = pagePath;
		string2 = string2.replaceFirst("/", "");
		string2 = string2.replaceAll("/", "-");
		return string2;
	}
	
	/**
	 * Returns roots page child list
	 * 
	 * @return List<Page>
	 */
	public List<Page> getRootChildPageList()
	{
		Page iterativePage = get("iterativePage", Page.class);
		Page rootPage;
		List<Page> rootL1ChildPages = new ArrayList<Page>();
		
		if(iterativePage != null)
		{
			rootPage = iterativePage.getAbsoluteParent(4);
			
			if (rootPage == null) {
				rootPage = getCurrentPage();
			}
		}
		
		else{
			rootPage = getCurrentPage();
		}
		
		rootL1ChildPages = getChildPagesList(rootPage, getRequest());
		
		return rootL1ChildPages;
		
	}
	
	/**
	 * Returns page title for given page (component use)
	 * 
	 * @param page
	 * @return String
	 */
	public String getPageTitle()
	{
		
		Page iterativePage = get("iterativePage", Page.class);
		String pageTitle;
		if (iterativePage != null) {
			pageTitle = getPageTitle(iterativePage);
			return pageTitle;
		}
		
		return "page title text is not passed.";
		
		//return "Test Title";
	}
	
	/**
	 * Returns page title for given page
	 * 
	 * @param page
	 * @return String
	 */
	public String getPageTitle(Page page) {
		if (page != null) {
			String navTitle;
			String pageTitle;
			String jcrTitle;
			String fallbackTitle;

			navTitle = page.getNavigationTitle();
			pageTitle = page.getPageTitle();
			jcrTitle = page.getTitle();

			if (navTitle != null && !navTitle.isEmpty()) {
				fallbackTitle = navTitle;
			}
			else {
				fallbackTitle = (pageTitle != null && !pageTitle.isEmpty()) ? pageTitle
						: jcrTitle;
			}
			return fallbackTitle;
		}
		return "";
	}
	
	/**
	 * Returns root page path (level 4)
	 * 
	 * @return String
	 */
	public String getRootPagePath() {
		Page rootPage = getCurrentPage().getAbsoluteParent(4);
		String rootPagePath;

		if (rootPage == null) {
			rootPage = getCurrentPage();
		}

		rootPagePath = rootPage.getPath();
		return rootPagePath;
	}
	
	/**
	 * Returns the inherited sign on page configured
	 * 
	 * @return String
	 */
	public String getInheritedSignOnPage()
	{
		String signonpage = WCMUtils.getInheritedProperty(getCurrentPage(), getResourceResolver(),"signonpage");
		SlingSettingsService slingSettingsService =  getSlingScriptHelper().getService(SlingSettingsService.class);
		if(Utils.isPublishRunMode(slingSettingsService))
		{
			SuntrustDotcomService dotcomService = getSlingScriptHelper().getService(SuntrustDotcomService.class);
			signonpage = Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),signonpage,getResourceResolver());
		}
	   return signonpage;
	}

	/**
	 * Returns hero sign on page for given page
	 * 
	 * @return String
	 */
	public String getHeroSignOnPage()
	{

		String signonPage = get("signon", String.class);
		SlingSettingsService slingSettingsService =  getSlingScriptHelper().getService(SlingSettingsService.class);
		if(Utils.isPublishRunMode(slingSettingsService))
		{
			SuntrustDotcomService dotcomService = getSlingScriptHelper().getService(SuntrustDotcomService.class);
			signonPage = Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),signonPage,getResourceResolver());
		}
		return signonPage;
	}
	
	/**
	 * Generates unique ID
	 * 
	 * @return int
	 */
	public int getUniqueId(){
		long timeSeed = System.nanoTime();
		double randSeed = Math.random() * 1000;
		long midSeed = (long) (timeSeed * randSeed);
		String strMidSeed = midSeed + "";
		String strUniqueId = strMidSeed.substring(0, 9);
		return Integer.parseInt(strUniqueId);
	}
	
	
	/**
	 * This method could be called from sightly code.
	 * 
	 * @return currentPage Language
	 */
	public String getPageLanguage()
	{
		Page iterativePage = get("iterativePage", Page.class);
		
		Page page = (iterativePage == null) ? getCurrentPage() : iterativePage;
		
		String pageLanguageString = getPageLanguage(page);
		return pageLanguageString;
	}
	
	/**
	 * This method will be called from other java methods. 
	 * NO SIGHTLY CALL TO THIS METHOD
	 * 
	 * @param page
	 * @return currentPage Language
	 */
	public String getPageLanguage(Page page)
	{
		String pageLanguageLocale = page.getLanguage(false).toString();
		if (pageLanguageLocale.equals(GenericEnum.ENGLISH_LANGUAGE_CODE.getValue())) {
			return GenericEnum.ENGLISH_LANGUAGE.getValue();
		}
		
		else if (pageLanguageLocale.equals(GenericEnum.SPANISH_LANGUAGE_CODE.getValue())) {
			return GenericEnum.SPANISH_LANGUAGE.getValue();
		}
		
		return GenericEnum.ENGLISH_LANGUAGE.getValue();
	}
	
	
	/**
	 * This method could be called from sightly code.
	 * 
	 * @return list of all tags linked to a resource.
	 */
	public List<Tag> getLinkedTagsToResorce()
	{
		Page iterativePage = get("iterativePage", Page.class);
		Page page = (iterativePage == null) ? getCurrentPage() : iterativePage;
		List<Tag> linkedTagList = new ArrayList<Tag>();
		
		linkedTagList = getLinkedTagsToResorce(page , getResourceResolver());
		
		return linkedTagList;
		
	}
	
	
	/**
	* This method will be called from other java methods.
	* NO SIGHTLY CALL TO THIS METHOD
	*
	* @param resPage
	* @param resolver
	* @return
	*/
	public List<Tag> getLinkedTagsToResorce(Page resPage , ResourceResolver resolver)
	{

	   List<Tag> linkedTagList = new ArrayList<Tag>();
	   /*String resourcePath = page.getPath();
	   resourcePath = resourcePath + GenericEnum.BACKWORD_SLASH_SYMBOL.getValue() + GenericEnum.JCR_CONTENT.getValue();
	   Resource res =  resolver.getResource(page.getPath());
	   Page resPage = res.adaptTo(Page.class);*/
	   TagManager tagManager = resolver.adaptTo(TagManager.class);
	   String contenttype = (resPage.getProperties().containsKey("contenttype")) ? (String) resPage.getProperties().get("contenttype") : "";
	   if(StringUtils.isNotBlank(contenttype))
	   {
	      linkedTagList.add(tagManager.resolve(contenttype));
	   }
	   String primaryrctag = (resPage.getProperties().containsKey("primarytag")) ? (String) resPage.getProperties().get("primarytag") : "";
	   if(StringUtils.isNotBlank(primaryrctag))
	   {
	      linkedTagList.add( tagManager.resolve(primaryrctag));
	   }
	   String advisorspeciality = (resPage.getProperties().containsKey("adv_specialty")) ? (String) resPage.getProperties().get("adv_specialty") : "";
	   if(StringUtils.isNotBlank(advisorspeciality))
	   {
	      linkedTagList.add( tagManager.resolve(advisorspeciality));
	   }
	   // Getting the tags of a Resource:
	   Tag[] tagArray = tagManager.getTags(resPage.getContentResource());
	   linkedTagList.addAll( Arrays.asList(tagArray));
	   return linkedTagList;
	}
	
	/**
	 * @param string - This string is a input string on which operation is performed.
	 * @param specialCharacter - This string is a input specialCharacter which is used to perform operation on above input string
	 * @return String
	 */
	public String removeLastSpecialCharacter(String string , String specialCharacter)
	{
		String updatedString = string;
		try
		{
			updatedString = updatedString.substring(0, updatedString.lastIndexOf(specialCharacter));
		}
		catch(StringIndexOutOfBoundsException stringIndexOutOfBoundsException)
		{
			LOGGER.debug(stringIndexOutOfBoundsException.getMessage());
		}
		catch(Exception exception)
		{
			LOGGER.debug(exception.getMessage());
		}
		return updatedString;
	}
	
	
	/**
	 * @return fieldNameArray - This is a array of dialog tabs identifier.
	 */
	public String[] getDialogTabsIdentifier()
	{
		int index = Integer.parseInt(GenericEnum.DIALOG_TAB_COUNT.getValue());
		String[] fieldNameArray = new String[index];
		
		try
		{
			for(int i = 0 ; i < index ; i++)
			{
				fieldNameArray[i] = i + GenericEnum.ITEMS.getValue();
			}
			
			/*for(String fieldName : fieldNameArray)
			{
				LOGGER.debug("Field Name : " + fieldName);
			}*/
		}
		
		catch(ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException)
		{
			LOGGER.debug("Exception cought in getDialogTabsIdentifier() : " + arrayIndexOutOfBoundsException.getMessage());
		}
		
		catch(Exception exception)
		{
			LOGGER.debug("Exception cought in getDialogTabsIdentifier() : " + exception.getMessage());
		}
		
		return fieldNameArray;
	}
	
	/**
	 * This method is used to append the url params and anchor tag in the url browsed in the component dialog pathbrowser field.
	 * This method could be called from sightly code.
	 * 
	 * @return modefied url
	 */
	public String getModifyURL()
	{
		String url = get(GenericEnum.LINK_URL.getValue(), String.class);
		String urlParams = get(GenericEnum.URL_PARAMS.getValue(), String.class);
		String anchorTag = get(GenericEnum.ANCHOR_TAG.getValue(), String.class);
		
		url = (url == null)?GenericEnum.EMPTY_STRING.getValue():url.trim();
		urlParams = (urlParams == null)?GenericEnum.EMPTY_STRING.getValue():urlParams.trim();
		anchorTag = (anchorTag == null)?GenericEnum.EMPTY_STRING.getValue():anchorTag.trim();
		
		String updatedUrl = Utils.getModifyURL(url , urlParams , anchorTag);
		//LOGGER.debug("updatedUrl new  : " + updatedUrl);
		return updatedUrl;
	}
	
	
	/**
	 * Purpose - The method will trim white space from string passed from sightly call.
	 * @return string
	 */
	public String truncateWhiteSPace()
	{
		String text = get(GenericEnum.TEXT.getValue(), String.class);		
		text = (text != null)?text.trim():GenericEnum.EMPTY_STRING.getValue();
		return text;
	}
	
	/**
	 * Method to get Page Thumbnail image path
	 * 
	 * @param pagePath
	 * @param resourceResolver
	 * @return
	 * @throws RepositoryException
	 */
	public String getPageThumbnailPath(String pagePath, ResourceResolver resourceResolver) throws RepositoryException {
		String imagePath = "";
		final PageManager pm = resourceResolver.adaptTo(PageManager.class);
		final Page page = pm.getPage(pagePath);
		if (page != null && page.getContentResource() != null) {
			try {
				Resource resource = page.getContentResource(IMAGE);
				if(resource != null) {
					Node img = (Node) resource.adaptTo(Node.class);
					if(img != null)
					{
						if(img.hasProperty("fileReference"))
						{
							imagePath=img.getProperty("fileReference").getString();
							LOGGER.info("fileReference path >"+imagePath);
							final AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
							if(assetManager.assetExists(imagePath))
							{
								Asset asset = assetManager.getAsset(imagePath);
								/*Rendition rendition =asset.getRendition("cq5dam.thumbnail.319.319.png");//if this rendition is present its path will be returned else original image path will be returned
								if(rendition != null)
								{
									return  rendition.getPath();
								}*/								
								imagePath = asset.getPath(); 
								LOGGER.info("thumbnail image >"+imagePath);
							}
						}
					}
				}

			} catch (Exception e) {
				LOGGER.error("Exception, {}", e.getMessage());
			}

		} else {
			LOGGER.info("Page is null");
		}

		return imagePath;
	}
	
	
	/**
	 * Purpose - The method will remove html element from string passed from sightly call.
	 * @return string
	 */
	public String getTruncatedHTMLString()
	{
		String text = get(GenericEnum.TEXT.getValue(), String.class);		
		text = (text == null)?GenericEnum.EMPTY_SPACE_STRING.getValue():text;
		String truncatedHTMLString = getTruncatedHTMLString(text);
		return truncatedHTMLString;
	}
	
	/**
	 * Purpose - The method will remove html element from string passed as argument.
	 * NO SIGHTLY CALL TO THIS METHOD
	 * @return string
	 */
	public String getTruncatedHTMLString(String htmlString)
	{
		String text = (htmlString == null)?GenericEnum.EMPTY_SPACE_STRING.getValue():htmlString;
		return Jsoup.parse(text).text();		
		
	}
	
	
	/**
	 * Purpose - The method will return list of users belongs to user group name passed from sightly call..
	 * @return map collection of users
	 */
	
	public Map<String , String> getAEMGroupUsers()
	{
		String groupName = get(GenericEnum.TEXT.getValue(), String.class);			
		//LOGGER.info("groupName passed from sightly call to this method getAEMGroupUsers() - " + groupName);
		
		Map<String , String> userListMap = serviceUtils.getAEMGroupUsers(getResourceResolver() , groupName);
		
		/*if (LOGGER.isDebugEnabled()) {
			userListMap.forEach((k,v)->LOGGER.debug("Key : " + k + " Value : " + v));
		}		*/
		
		return userListMap;
	}

	/**
	 * Purpose - The method will return logged in AEM user ID
	 * @return Logged in AEM User ID
	 */
	
	public String getLoggedInUserID()
	{
		User currentUser = null;
		Session jcrSession = null;
		UserManager userManager=null;
		String loggedInUserID = null;
		try{			
			jcrSession = getResourceResolver().adaptTo(Session.class);
			userManager=getResourceResolver().adaptTo(UserManager.class);
			currentUser = (User) userManager.getAuthorizable(jcrSession.getUserID());		
			//LOGGER.info("Logged in userID : " + currentUser.getID());
			loggedInUserID = currentUser.getID();
			
			}
			catch(Exception exception)
			{
				LOGGER.info("Exception cought in getLoggedInUserID() ::: " + exception.getMessage());
				exception.printStackTrace();
			}
		
		return loggedInUserID;
		
	}
	
	
	/**
	 * Purpose - The method will return logged in AEM user Email ID
	 * @return Logged in AEM User Email ID
	 */
	
	public String getLoggedInUserEmail()
	{
		User currentUser = null;
		Session jcrSession = null;
		UserManager userManager=null;
		String email = null;
		try{
			jcrSession = getResourceResolver().adaptTo(Session.class);
			
			userManager=getResourceResolver().adaptTo(UserManager.class);
			
			currentUser = (User) userManager.getAuthorizable(jcrSession.getUserID());	
			
			email = currentUser.getProperty("./profile/email")!=null?currentUser.getProperty("./profile/email")[0].getString():GenericEnum.EMPTY_STRING.getValue();

			//LOGGER.info("Logged in user email id : " + email);

			
			}
			catch(Exception exception)
			{
				LOGGER.info("Exception cought in getLoggedInUserEmail() ::: " + exception.getMessage());
				exception.printStackTrace();
			}
			
		return email;
	}
	
	
	/**
	 * Purpose - The method will return logged in AEM user name
	 * @return Logged in AEM User name
	 */
	
	public String getLoggedInUserName()
	{
		String userName = null;
		User currentUser = null;
		Session jcrSession = null;
		UserManager userManager=null;
		try{			
			jcrSession = getResourceResolver().adaptTo(Session.class);
			userManager=getResourceResolver().adaptTo(UserManager.class);
			currentUser = (User) userManager.getAuthorizable(jcrSession.getUserID());
			
			String givenName = currentUser.getProperty("./profile/givenName")!=null?currentUser.getProperty("./profile/givenName")[0].getString():GenericEnum.EMPTY_STRING.getValue();
			
			String familyName = currentUser.getProperty("./profile/familyName")!=null?currentUser.getProperty("./profile/familyName")[0].getString():GenericEnum.EMPTY_STRING.getValue();
		
			userName = givenName.trim() + GenericEnum.EMPTY_SPACE_STRING.getValue() + familyName.trim();
			
			//LOGGER.info("Logged in userName : " + userName);
			
			return userName;

			}
			catch(Exception exception)
			{
				LOGGER.info("Exception cought in getLoggedInUserName() ::: " + exception.getMessage());
				exception.printStackTrace();
			}
		return userName;
	}
	
	/**
	 * Purpose - The method will return the request attribute set as tag title for RC wrapper
	 * NO SIGHTLY CALL TO THIS METHOD
	 * @return string
	 */
	public String getHeroTagForRC()
	{
	   return (String) getRequest().getAttribute("heroheadlineTitle");
	}


	/**
	    * Purpose - The method will return the request attribute set as current
	    * page path for RC wrapper NO SIGHTLY CALL TO THIS METHOD
	    *
	    * @return string
	    */
	    public String getMobileHeaderCurrentPage() {
	        String template = getCurrentPage().getTemplate().getName();
	        if (template.contains("wrapper")) {
	            String pageUrl = (String) getRequest().getAttribute("mobileHeaderNavPage");
	            if (StringUtils.isNotBlank(pageUrl)) {
	                 return pageUrl;
	            }
	       return getCurrentPage().getPath();
	        } else {
	            if (getCurrentPage().getDepth() > 9) // means its a level 4 page
	            {
	                 return getCurrentPage().getAbsoluteParent(8).getPath();
	            } else {
	                 return getCurrentPage().getPath();
	            }
	        }
	       
	    }
	    public String getHeaderCurrentPage() {
	        String template = getCurrentPage().getTemplate().getName();
	        if (template.contains("wrapper")) {
	            String pageUrl = (String) getRequest().getAttribute("headerNavPage");
	            if (StringUtils.isNotBlank(pageUrl)) {
	                 return pageUrl;
	            }
	       return getCurrentPage().getPath();
	        } else {
	            if (getCurrentPage().getDepth() > 8) // means its a level 4 page
	            {
	                 return getCurrentPage().getAbsoluteParent(7).getPath();
	            } else {
	                 return getCurrentPage().getPath();
	            }
	        }
	       
	    }

	    /**
	    * Purpose - The method will return the request attribute set as current
	    * page path for RC wrapper NO SIGHTLY CALL TO THIS METHOD
	    *
	    * @return string
	    */
	    public Page getMobileIterativePage() {
	        String template = getCurrentPage().getTemplate().getName();
	        if (template.contains("wrapper")) {
	            String pageUrl = (String) getRequest().getAttribute("mobileHeaderNavPage");
	            if (StringUtils.isNotBlank(pageUrl)) {
	               return getPageManager().getPage(pageUrl);
	            }
	       return getCurrentPage();
	        } else {
	            if (getCurrentPage().getDepth() > 9) // means its a level 4 page
	            {
	                return getCurrentPage().getAbsoluteParent(8);
	            } else {
	                return getCurrentPage();
	            }
	        }
	       
	    }
	    
	    public Page getIterativePage() {
	        String template = getCurrentPage().getTemplate().getName();
	        if (template.contains("wrapper")) {
	            String pageUrl = (String) getRequest().getAttribute("headerNavPage");
	            if (StringUtils.isNotBlank(pageUrl)) {
	               return getPageManager().getPage(pageUrl);
	            }
	       return getCurrentPage();
	        } else {
	            if (getCurrentPage().getDepth() > 8) // means its a level 4 page
	            {
	                return getCurrentPage().getAbsoluteParent(7);
	            } else {
	                return getCurrentPage();
	            }
	        }
	       
	    }

	
	/**
	* Purpose - The method will return the page that belongs to the relative path passed by sightly call
	* @return aem page
	*/
	public Page getPageReference()
	{
		String pagePath = get(GenericEnum.PAGE_PATH.getValue(), String.class);	
		Resource resource = getResourceResolver().getResource(pagePath);
		
		Page page = resource.adaptTo(Page.class);		
		
		return page;
		
	}
	
	/**
	 * Returns tag list for given tag
	 * 
	 * @return
	 */
	public List<SearchBean> getTagsList() {
		
		String[] tagsValues = get("tagArr", String[].class);
		ArrayList<SearchBean> tagList = new ArrayList<SearchBean>();
		TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);

		if(tagsValues != null){
 			
 			for (String tagValue : tagsValues) {
 				SearchBean searchBean = new SearchBean();
 				String tagID = tagValue;
 				Tag tag = tagManager.resolve(tagID);
 				if(tag != null) {
 					//tagList.add(tag.getName());
 			 		searchBean.setTagName(tag.getName());
 			 		searchBean.setTagTitle(tag.getTitle());
 			 		searchBean.setTagId(tag.getTagID());
 			 		tagList.add(searchBean);
 				}
 			}
 		}
 		
 		return tagList;
 	}

	
	/**
	 * Purpose - This method will check if loop index value is multiple of countValue or not.indexValue and countValue are passed by sightly call.
	 * @return boolean - true if indexValue is multiple of countValue.
	 */
	public Boolean getCountSelectorFlag()
	{
		
		String indexValueString = get(GenericEnum.INDEX_VALUE.getValue(), String.class).trim();
		String countValueString = get(GenericEnum.COUNT_VALUE.getValue(), String.class).trim();
		Boolean flag = false;
		
		int indexValue = (indexValueString != null && !indexValueString.isEmpty()) ? Integer.parseInt(indexValueString) : 1;
		int countValue = (countValueString != null && !countValueString.isEmpty()) ? Integer.parseInt(countValueString) : 1;
		
		flag = (countValue != 0 && (indexValue % countValue == 0)) ? true : false;

		
		return flag;
		
	}
	
	/**
	 * Returns pages collection in column view
	 * 
	 * @return
	 */
	public Map<Integer, List<Page>> getPagesCollectionInColumnView()
	{
	
		Page iterativePage = get(GenericEnum.ITERATIVE_PAGE.getValue(), Page.class);
		Map<Integer, List<Page>> L2PagesCollection = new LinkedHashMap <Integer, List<Page>>();

		List<Page> coulmn1List = new ArrayList<Page>();
		List<Page> coulmn2List = new ArrayList<Page>();
		List<Page> coulmn3List = new ArrayList<Page>();
		List<Page> coulmn4List = new ArrayList<Page>();

		L2PagesCollection.put(1, coulmn1List);
		L2PagesCollection.put(2, coulmn2List);
		L2PagesCollection.put(3, coulmn3List);
		L2PagesCollection.put(4, coulmn4List);
		
		List<Page> L2Pages = getChildPagesList(iterativePage,getRequest());
		
		
		int i = 0;
		for (Page L2Page : L2Pages) {
			
			i++;
			
			try {
				if (i % 5 == 0) {
					i = 1;
					List<Page> list = L2PagesCollection.get(i);
					list.add(L2Page);
					L2PagesCollection.put(i, list);

				}

				else {
					List<Page> list = L2PagesCollection.get(i);
					list.add(L2Page);
					L2PagesCollection.put(i, list);
				}

			}

			catch (Exception exception) {
				LOGGER.error("Exception cought in getL2PagesCollection : " + exception);

				break;
			}

		}
		
		Iterator<Map.Entry<Integer, List<Page>>> iter = L2PagesCollection.entrySet().iterator();
		while (iter.hasNext()) {
		    Map.Entry<Integer, List<Page>> entry = iter.next();
		    List<Page> pageList = entry.getValue();
		    
		    if(pageList.size() == 0){
		        iter.remove();
		    }
		}

		
		return L2PagesCollection;
		
	}
	
	
	/**
	 * This method could be called from sightly code.
	 * @return canonicalUrls of the passed aem page.
	 */
	public String getFullPageUrl() {
		
		String pagePath = get(GenericEnum.PAGE_PATH.getValue(), String.class);		
		pagePath = (pagePath == null) ? getCurrentPage().getPath() : pagePath;
		
		return getFullPageUrl(pagePath);
	}
	
	/**
	 * This method could be called from sightly code.
	 * @return resolved Urls of the passed aem page.
	 */
	public String getResolvedPageUrl() {
		
		String pagePath = get(GenericEnum.PAGE_PATH.getValue(), String.class);	
		pagePath = (pagePath == null) ? getCurrentPage().getPath() : pagePath;
		String urlParam = get("urlParam", String.class);
		LOGGER.debug("urlParam:"+urlParam);
		if(StringUtils.isNotBlank(urlParam)) {
			STELConfigService stelServiceconfig = getSlingScriptHelper()
					.getService(STELConfigService.class);
			String rpxArchivalPath = stelServiceconfig.getPropertyValue("rpx.archived.path");
			LOGGER.debug("rpxArchivalPath:"+rpxArchivalPath);
			if(StringUtils.isNotBlank(rpxArchivalPath)) {
				Resource resource = getResourceResolver().getResource(rpxArchivalPath);
				if(resource.getChild(urlParam) != null) {
					pagePath = (ResourceUtil.isNonExistingResource(resource.getChild(urlParam))) ? pagePath : resource.getChild(urlParam).getPath();
				}
			}
		}
		LOGGER.debug("pagePath:"+pagePath);
		
		return getResourceResolver().map(getRequest(), pagePath);
	}
	
	
	/**
	 * This method will be called from other java methods. NO SIGHTLY CALL TO
	 * THIS METHOD
	 *
	 * @param pagePath
	 * @return currentPage Language
	 */
	public String getFullPageUrl(String pagePath) {
		SuntrustDotcomService dotcomService = getSlingScriptHelper()
				.getService(SuntrustDotcomService.class);
		
		return Utils.getPayloadUrl(pagePath, getResourceResolver(),
				getSlingScriptHelper().getService(SlingSettingsService.class),
				dotcomService.getPropertyArray("canonical.urls"));
	}
	
	/**
	 * This method will be called from other java methods. NO SIGHTLY CALL TO
	 * THIS METHOD
	 *
	 * @param pagePath
	 * @return currentPage Language
	 */
	public String getFullPageUrl(String pagePath, SuntrustDotcomService dotcomService, ResourceResolver resolver, SlingSettingsService slingService) {

		return Utils.getPayloadUrl(pagePath, resolver,
				slingService,
				dotcomService.getPropertyArray("canonical.urls"));
	}
	
	/**
	 * Overrided method
	 * 
	 */
	@Override
	public void activate() throws Exception {

		serviceUtils = getSlingScriptHelper().getService(ServiceUtils.class);				
		
	}
}