package com.suntrust.dotcom.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.exec.WorkItem;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.Filter;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.commons.util.AssetReferenceSearch;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutConfig;
import com.day.cq.wcm.msm.api.RolloutConfigManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.day.cq.workflow.collection.ResourceCollection;
import com.day.cq.workflow.collection.ResourceCollectionManager;
import com.day.cq.workflow.collection.ResourceCollectionUtil;
import com.suntrust.dotcom.services.EmailService;

/**
 * Created by UGRR162 on 7/1/2017.
 */
public class Utils {
	
	/** Logger variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
	
	/** String constant to get pay load path */
	public static final String PAYLOADPATH = "paypath";
	
	/** String constant to get published path */
	public static final String PUBLISHEDPATH = "publishedpath";
	
	/** String constant to check for workflow package */
	public static final String ISWFPKG = "iswfpkg";
	
	/** String constant to get workflow package path */
	public static final String WFPKGPATH = "pkgpath";
	
	/** String constant to get inbox path */
	public static final String INBOXPATH = "inboxpath";
	
	/** String constant to get workflow item path */
	public static final String WORKITEMPATH = "workitempath";
	
	/** String constant toget environment */
	public static final String ENV_DETAILS = "envdetials";
	
	/** String constant of Domain name */
	public static final String CORP_DOMAIN="USCORP";
	
    public static String getPropertyValue(String key,Dictionary<String, Object> properties) {
        if (null != properties) {
            return PropertiesUtil.toString(properties.get(key), "");
        }
        return "";
    }

    public static ArrayList<String> getPropertyArray(String key,Dictionary<String, Object> properties) {
        ArrayList<String> arrLst = new ArrayList<String>();
        if (null != properties) {
            String[] value = PropertiesUtil.toStringArray(properties.get(key));
            if (value != null) {
                Collections.addAll(arrLst, value);
            }
        }
        return arrLst;
    }

    public static String removeSpecialChar(String strToReplace)
    {
        String  specialChar =", |. | / | - | |/|.|";
        String[] replaceCharStr = specialChar.split("\\|");
        for(int i=0;i<replaceCharStr.length;i++)
        {
            String str = replaceCharStr[i];
            if(strToReplace.contains(str))
            {
                strToReplace  =strToReplace.replace(str,"-");
            }
        }
        return strToReplace;
    }


    public static void setHideInNav(Page pageObj) throws javax.jcr.RepositoryException
    {
        if(pageObj != null)
        {
            //set the hide in nav
            Node jcrNode = pageObj.getContentResource().adaptTo(Node.class);
            if(!jcrNode.hasProperty("hideInNav"))
            {
                jcrNode.setProperty("hideInNav","true");
            }
        }
    }
    
    /**
     * Adds hide from internal search property to page
     * 
     * @param pageObj
     * @throws javax.jcr.RepositoryException
     */
    public static void setHideFromSearch(Page pageObj) throws javax.jcr.RepositoryException
    {
        if(pageObj != null)
        {
            //set the hide in nav
            Node jcrNode = pageObj.getContentResource().adaptTo(Node.class);
            if(!jcrNode.hasProperty("searchable"))
            {
                jcrNode.setProperty("searchable","true");
            }
        }
    }
    
    /**
     * Adds hide from external search property to page
     * 
     * @param pageObj
     * @throws javax.jcr.RepositoryException
     */
    public static void setHideFromExtSearch(Page pageObj) throws javax.jcr.RepositoryException
    {
        if(pageObj != null)
        {
            //set the hide in nav
            Node jcrNode = pageObj.getContentResource().adaptTo(Node.class);
            if(!jcrNode.hasProperty("externalsearch"))
            {
                jcrNode.setProperty("externalsearch","true");
            }
        }
    }
    
    
    public static Page checkPageExist(String srcPath, String destPath, PageManager pageManager) throws WCMException
    {
        Page page = pageManager.getPage(srcPath+"/"+destPath);
        if(null == page){
            return null;
        }
        return page;
    }

    public static Page createCustomPage(String srcPath, String destPath, String template, String title, PageManager pageManager, boolean isReplicate) throws WCMException
    {
        Page chainPage = pageManager.getPage(srcPath+"/"+destPath);
        if(null == chainPage){
            chainPage = pageManager.create(srcPath,destPath,template,title);
        }
        return chainPage;
    }

    public static void createLiveRelationShip(Page destPage, Page srcPage, ResourceResolver resolver, RolloutManager rolloutManager) throws WCMException
    {
        LiveRelationshipManager liveRelationshipManager = resolver.adaptTo(LiveRelationshipManager.class);
        RolloutConfigManager rolloutConfigManager = resolver.adaptTo(RolloutConfigManager.class);
        if(rolloutConfigManager != null) {
            RolloutConfig rConfig = rolloutConfigManager.getRolloutConfig("/etc/msm/rolloutconfigs/default");
            if (!liveRelationshipManager.hasLiveRelationship(destPage.getContentResource())) {
                liveRelationshipManager.establishRelationship(srcPage, destPage, false, true, rConfig);
            }
            LiveRelationship relationShip = liveRelationshipManager.getLiveRelationship(destPage.getContentResource(), true);
            rolloutManager.rollout(resolver, relationShip, false, true);
        }
    }
    
    public static void cancelPropertyRelationship(String destPage,ResourceResolver resolver,String[] propertyNames) throws WCMException
    {

        LiveRelationshipManager liveRelationshipManager = resolver.adaptTo(LiveRelationshipManager.class);
        LiveRelationship relationShip = liveRelationshipManager.getLiveRelationship(resolver.getResource(destPage), true);
        liveRelationshipManager.cancelPropertyRelationship(resolver,relationShip,propertyNames,true);
    }

    static class ProductPageFilter implements Filter{
        @Override
        public boolean includes(Object element) {
            Page p = (Page) element;
            return (p.getProperties().get("cq:template", "")
                    .equals("/conf/dotcom-project/settings/wcm/templates/resource-center-template"));
        }
    }

    public static String getWrapperPath(String tagtomatch,ResourceResolver resourceResolver){
        if(StringUtils.isNotBlank(tagtomatch)) {
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            Page rootPage = pageManager.getPage("/content/suntrust/dotcom/us/en/resource-center");
            if (rootPage != null) {
                Iterator<Page> pageItr = rootPage.listChildren(new ProductPageFilter());
                while (pageItr.hasNext()) {
                    Page lobPage = pageItr.next();
                    Tag[] tagsList = lobPage.getTags();
                    boolean isTagMatch = isLobTagMatch(tagtomatch, tagsList);
                    if (isTagMatch) {
                        Page wrapperpage = pageManager.getPage(lobPage.getPath() + "/wrapper");
                        if (wrapperpage != null) {
                            return wrapperpage.getPath();
                        }

                    }

                }
            }
        }
        return "";
       /* try {
            Resource res =  resourceResolver.getResource(articlePath);
            //find pages by tags
            if (res != null) {
                Page resPage = res.adaptTo(Page.class);
                if(resPage != null && resPage.getProperties().containsKey(tagFieldToCheck)) {
                    PageManager pageManager =resourceResolver.adaptTo(PageManager.class);
                    String primaryRCTag = (String)resPage.getProperties().get(tagFieldToCheck);
                    if(StringUtils.isNotBlank(primaryRCTag))
                    {
                        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
                        Page rootPage = pageManager.getPage("/content/suntrust/dotcom/us/en/resource-center");
                        if(rootPage != null)
                        {
                            Iterator<Page> pageItr = rootPage.listChildren(new PageFilter());
                            while(pageItr.hasNext())
                            {
                                Page lobPage = pageItr.next();
                                Tag[] tagsList = lobPage.getTags();
                                boolean isTagMatch = isLobTagMatch(primaryRCTag,tagsList);
                                if(isTagMatch)
                                {
                                    Page wrapperpage = pageManager.getPage(lobPage.getPath()+"/wrapper");
                                    if(wrapperpage != null)
                                    {
                                        return wrapperpage.getPath();
                                    }

                                }

                            }
                        }
                    }

                }
            }
            else
            {
                //redirect to error page
                return "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";*/
    }

    /**
     * 
     * 
     * @param primaryRCTag
     * @param tagsList
     * @return
     */
    public static boolean isLobTagMatch(String primaryRCTag , Tag[] tagsList)
    {
        if(StringUtils.isNotBlank(primaryRCTag) && !ArrayUtils.isEmpty(tagsList))
        {
            String[] tagStr = primaryRCTag.split("/");
            String tagToMatch = tagStr[0];
            for(int i=0;i<tagsList.length;i++)
            {
                Tag subTag = tagsList[i];
                if(subTag.getTagID().equalsIgnoreCase(tagToMatch))
                {
                    return true;
                }

            }
        }
        return false;
    }
    
     /**
     * This method is used to append the url params and anchor tag in the url browsed in the component dialog pathbrowser field.
     * This method will be called from other java methods.
     * NO SIGHTLY CALL TO THIS METHOD
     * @param strings
     * @return modefied url
     */
     public static String getModifyURL(String... strings)
     {
         StringBuffer updatedUrl = new StringBuffer();
         try
         {
             String urlParams = strings[1];
             String anchorTag = strings[2];
             //LOGGER.debug("Inside get modifyurl"+strings[0]+":"+strings[1]+":"+strings[2]);

             if (strings[0].isEmpty() == false) {
                 updatedUrl = updatedUrl.append(urlCheck(strings[0]));
                 if (urlParams !=null && StringUtils.isNotEmpty(urlParams)) {
                     if(!urlParams.startsWith(GenericEnum.QUERYSTRING_QUESTION_MARK.getValue())) {
                         updatedUrl = updatedUrl.append(GenericEnum.QUERYSTRING_QUESTION_MARK.getValue()).append(urlParams);
                     }
                     else{
                         updatedUrl = updatedUrl.append(urlParams);
                     }
                 }

                 if (anchorTag !=null && StringUtils.isNotEmpty(anchorTag)) {
                     if(!anchorTag.startsWith(GenericEnum.HASH_SYMBOL.getValue())) {
                         updatedUrl = updatedUrl.append(GenericEnum.HASH_SYMBOL.getValue()).append(anchorTag);
                     }
                     else
                     {
                         updatedUrl = updatedUrl.append(anchorTag);
                     }
                 }

             } else if(StringUtils.isNotEmpty(anchorTag)){
            	 if(anchorTag.startsWith(GenericEnum.HASH_SYMBOL.getValue()) == false) {
                     updatedUrl = updatedUrl.append(GenericEnum.HASH_SYMBOL.getValue()).append(anchorTag);
                 } else {
                     updatedUrl = updatedUrl.append(anchorTag);
                 }
            	 
             } else {
                 //updatedUrl = updatedUrl.append(GenericEnum.HASH_SYMBOL.getValue());
            	 updatedUrl = updatedUrl.append(GenericEnum.EMPTY_STRING.getValue());
             }
         }

         catch(Exception exception)
         {
             LOGGER.error("Exception cought in getModifyURL(String... strings). Message: {}, Trace: {} ", exception.getMessage(),exception);
         }

         //LOGGER.debug("URL: "+updatedUrl.toString());
         return updatedUrl.toString();
     }



     /**
     * This method is used to check the url browsed in the component dialog pathbrowser field
     *
     * @param url
     * @return URL String
     */
     public static String urlCheck(String url)
     {
         String stringURL = url.trim();
         if (stringURL.contains(GenericEnum.HTTP_PROTOCOL.getValue()) || stringURL.contains(GenericEnum.HTTPS_PROTOCOL.getValue()) || stringURL.contains(GenericEnum.TEL_SYMBOL.getValue()) || stringURL.contains(GenericEnum.DOT_SYMBOL.getValue())) {
             return stringURL;
         } else {
             return stringURL + GenericEnum.HTML_EXTENSION.getValue();
         }
     }

     /**
      * Returns Canonical Url
      * 
      * @param values
      * @param pageUrl
      * @param resolver
      * @return
      */
     public static String getCanonicalUrl(List<String> values,String pageUrl,ResourceResolver resolver)
     {
         if(values != null && StringUtils.isNotBlank(pageUrl))
         {
             //if its resourcecenter article urls , give the primary lob url
             PageManager pageMgr = resolver.adaptTo(PageManager.class);
             Page pg = pageMgr.getPage(pageUrl);
             if(pg != null && pg.getProperties().containsKey("sling:resourceType"))
             {
              String resourceType = (String)pg.getProperties().get("sling:resourceType");
              if(resourceType.contains("dotcom/components/page/resourcecentertemplate") || pageUrl.contains("/wrapper.html/"))
              {
                  pageUrl = getPrimaryWrapperPath(pageUrl, resolver);
              }
             }
             pageUrl = formatPageUrl(values,pageUrl);

             return pageUrl;
         }
         return pageUrl;
     }

     /**
      * Formated page URL
      * 
      * @param values
      * @param pageUrl
      * @return
      */
     public static String formatPageUrl(List<String> values,String pageUrl)
     {
         Iterator<String> itr = values.iterator();
         while(itr.hasNext())
         {
             String value = itr.next();
             if(StringUtils.isNotBlank(value))
             {
                 String[] strVal = value.split(":");
                 String key = strVal[0];
                 String valtoreplace = strVal[1];
                 pageUrl= pageUrl.replace(key,valtoreplace);
             }
         }
        return pageUrl;
     }

     /**
      * Returns full canonical url of given path
      * @param resolver
      * @param canonicalpath
      * @return
      */
     public static String getFullCanonicalUrl(ResourceResolver resolver, String canonicalpath)
     {
         Externalizer externalizer = resolver.adaptTo(Externalizer.class);
         return externalizer.publishLink(resolver, canonicalpath);
     }

     /**
      * Returns primary wrapper path of given article
      * 
      * @param articleRelativePath
      * @param resolver
      * @return
      */
     public static String getPrimaryWrapperPath(String articleRelativePath, ResourceResolver resolver)
     {

         Resource res =  resolver.getResource(articleRelativePath);
         String wrapperpath= "";
         String finalPath = articleRelativePath;
         //find pages by tags
         if (res != null) {
             Page resPage = res.adaptTo(Page.class);
             if (resPage != null) {
                 String primaryArticleTag = (resPage.getProperties().containsKey("primarytag")) ? (String) resPage.getProperties().get("primarytag") : "";
                 wrapperpath = Utils.getWrapperPath(primaryArticleTag,resolver);
             }
         }
         if(StringUtils.isNotBlank(wrapperpath))
         {
             finalPath = wrapperpath +".html"+ articleRelativePath;
         }
         return finalPath;
     }
     
     /**
      * Converts given array to list 
      * 
      * @param array
      * @return
      */
     public static ArrayList<String> convertArrayToList(String[] array) {
         ArrayList<String> arrayList = new ArrayList<String>();
         if (null != array) 
            Collections.addAll(arrayList, array);
        	
         return arrayList;
     }

     /**
      * Returns runmode
      * 
      * @param settingService
      * @return
      */
    public static boolean isPublishRunMode(SlingSettingsService settingService)
    {
        Set<String> runmodes= settingService.getRunModes();
        if (runmodes.contains("publish")){
            return true;
        }
        return false;
    }
    
   /**
    * Returns user name by user ID
    * 
    * @param userId
    * @param userManager
    * @return
    */
    public static String getUserNamebyId(String userId, UserManager userManager){
		String userName = null;
		try{
			if("pending".equalsIgnoreCase(userId)) {
				userName = "Pending";
			} else if(null != userId){
				Authorizable userAuthorizable=userManager.getAuthorizable(userId);
				String givenName = userAuthorizable.getProperty("./profile/givenName")!=null?userAuthorizable.getProperty("./profile/givenName")[0].getString():"";
				String familyName = userAuthorizable.getProperty("./profile/familyName")!=null?userAuthorizable.getProperty("./profile/familyName")[0].getString():"";
				
				userName = givenName+" "+familyName;
			}
		} catch (ValueFormatException e) {
			LOGGER.error("ValueFormatException captured. Message: {}, Trace: {} ", e.getMessage(),e);
		} catch (IllegalStateException e) {
			LOGGER.error("IllegalStateException captured. Message: {}, Trace: {} ", e.getMessage(),e);
		} catch (RepositoryException e) {
			LOGGER.error("RepositoryException captured. Message: {}, Trace: {} ", e.getMessage(),e);
		}
		return userName;
	}
    
    /**
     * Returns pages attached to package
     * 
     * @param payLoadNode
     * @param resourceCollectionManager
     * @param externalizer
     * @param resourceResolver
     * @return
     * @throws RepositoryException
     */
     public static Map<String,String> getWorkflowPkgPaths(Node payLoadNode, ResourceCollectionManager resourceCollectionManager, Externalizer externalizer, ResourceResolver resourceResolver, List<String> canonicalUrls) throws RepositoryException{
         String[] DEFAULT_WF_PACKAGE_TYPES = {"cq:Page", "cq:PageContent", "dam:Asset"};
     Map<String,String> payloadAbsolutePath=new HashMap<>();
     final ResourceCollection resourceCollection = ResourceCollectionUtil.getResourceCollection(payLoadNode, resourceCollectionManager);
     if(null!=resourceCollection){
        final List<Node> members = resourceCollection.list(DEFAULT_WF_PACKAGE_TYPES);
        StringJoiner payLoadJoiner = new StringJoiner("</li>","<ul>","</li></ul>");
        StringJoiner publishedPayLoadJoiner = new StringJoiner("</li>","<ul>","</li></ul>");
        members.stream()
              .forEach(member->
              {
                 try{
                    String payloadPath = member.getPath();
                    String absolutePagePath="";
                    String productionPagePath="";
                            if(payloadPath.toLowerCase().contains("content/suntrust")){
                                PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                                Page pg = pageManager.getPage(payloadPath);
                                if(pg != null && pg.getProperties().containsKey("sling:resourceType"))
                                {
                                    String resourceType = (String)pg.getProperties().get("sling:resourceType");
                                    if(resourceType.contains("dotcom/components/page/resourcecentertemplate"))
                                    {
                                        absolutePagePath=externalizer.authorLink(resourceResolver, Utils.getPrimaryWrapperPath(payloadPath, resourceResolver)+".html?wcmmode=disabled");
                                    }
                                    else
                                    {
                                        absolutePagePath=externalizer.authorLink(resourceResolver, payloadPath+".html?wcmmode=disabled");
                                    }
                                }
                                productionPagePath = externalizer.publishLink(resourceResolver,
                                        Utils.getCanonicalUrl(canonicalUrls,
                                                payloadPath, resourceResolver));
                            } else {
                                absolutePagePath=externalizer.authorLink(resourceResolver, payloadPath);
                                productionPagePath=externalizer.publishLink(resourceResolver, payloadPath);
                            }
                            payLoadJoiner.add("<li>"+absolutePagePath);
                            publishedPayLoadJoiner.add("<li>"+productionPagePath);
                }
                 catch(RepositoryException e){
                    LOGGER.error("Error in retrieving payloads from workflow package==>"+e);
                 }
                 
              });
           payloadAbsolutePath.put(PAYLOADPATH, payLoadJoiner.toString());
           payloadAbsolutePath.put(PUBLISHEDPATH, publishedPayLoadJoiner.toString());
        }
     return payloadAbsolutePath;
  }
    
    /**
     * Returns payload absolute path
     * 
     * @param workItem
     * @param payloadPath
     * @param session
     * @param externalizer
     * @param resourceCollectionManager
     * @param resourceResolver
     * @return
     * @throws Exception
     */
    public static Map<String, String> getAbsolutePaths(WorkItem workItem, String payloadPath, Session session, Externalizer externalizer, ResourceCollectionManager resourceCollectionManager, ResourceResolver resourceResolver, List<String> canonicalUrls) throws Exception{
    	String absolutePagePath = null;
    	String productionPagePath = null;
		StringBuffer workItemBuffer = new StringBuffer();
		Map<String, String> absolutePaths;
		Node payLoadNode = session.getNode(payloadPath).getNode("jcr:content");
		
		NodeType[] nodeType = payLoadNode.getMixinNodeTypes();
		boolean iswfPkg = Arrays.asList(nodeType).stream().anyMatch(node -> "vlt:Package".equalsIgnoreCase(node.getName()));
		
		if(iswfPkg) {
			absolutePaths = getWorkflowPkgPaths(payLoadNode, resourceCollectionManager, externalizer, resourceResolver, canonicalUrls);
		} else {
			absolutePaths = new HashMap<>();
		}

		if(payloadPath.toLowerCase().contains("content/suntrust")){
			absolutePagePath = externalizer.authorLink(resourceResolver, payloadPath+".html?wcmmode=disabled");
			productionPagePath = externalizer.publishLink(resourceResolver, getCanonicalUrl(canonicalUrls,payloadPath,resourceResolver));
		} else {
			absolutePagePath = externalizer.authorLink(resourceResolver, payloadPath);
			productionPagePath = externalizer.publishLink(resourceResolver, payloadPath);
		}
		
		workItemBuffer.append(externalizer.authorLink(resourceResolver, "/mnt/overlay/cq/inbox/content/inbox/details.html?item="));
		
		//create future work item inbox link
		String[] workSplitId = workItem.getId().split("_",3);
		int nodeNumber = Integer.parseInt(workSplitId[1].substring(4));
		String nodeName = workSplitId[2];
		String workflowId = workItem.getWorkflow().getId();
		
		workItemBuffer.append(workflowId + "/workItems/node"
				+ Integer.toString(nodeNumber + 1) + "_" + nodeName + "&type="
				+ workItem.getItemType() + "&_charset_=utf-8");
		
		if(absolutePaths.containsKey(PAYLOADPATH)){
			absolutePaths.put(ISWFPKG, "true");
			absolutePaths.put(WFPKGPATH,absolutePagePath);
		} else{
			absolutePaths.put(PAYLOADPATH, absolutePagePath);
			absolutePaths.put(PUBLISHEDPATH, productionPagePath);
		}
		absolutePaths.put(INBOXPATH, externalizer.authorLink(resourceResolver, "/aem/inbox"));
		absolutePaths.put(WORKITEMPATH, workItemBuffer.toString());
		absolutePaths.put(ENV_DETAILS, externalizer.authorLink(resourceResolver, ""));
		
		return absolutePaths;
	}

    /**
     * Returns full page url based on environment
     * 
     * @param pageUrl
     * @param resourceResolver
     * @param settingsService
     * @param canonicalUrls
     * @return
     */
	public static String getPayloadUrl(String pageUrl,ResourceResolver resourceResolver,SlingSettingsService settingsService,List<String> canonicalUrls)
    {
        boolean isPublishMode = Utils.isPublishRunMode(settingsService);
        if(isPublishMode)
        {
            pageUrl = Utils.getCanonicalUrl(canonicalUrls,pageUrl,resourceResolver);
        }
        else
        {
            if(pageUrl.endsWith(GenericEnum.HTML_EXTENSION.getValue()) == false && pageUrl.isEmpty() == false) {
                pageUrl = pageUrl + GenericEnum.HTML_EXTENSION.getValue();
            }
        }

        return pageUrl;
    }
    
 	public static Map<String, Asset> getReferencesInPage(final String pagePath,
			ResourceResolverFactory resourceResolverFactory) {
		//LOGGER.info("Assets references to be retrieved for page {} ", pagePath);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
		try {
			ResourceResolver resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);
			Resource resource = resourceResolver.getResource(pagePath + "/"
					+ JcrConstants.JCR_CONTENT);
			Node pageNode = resource.adaptTo(Node.class);
			AssetReferenceSearch referenceSearch = new AssetReferenceSearch(
					pageNode, DamConstants.MOUNTPOINT_ASSETS, resourceResolver);
			if (referenceSearch.search().size() > 0) {
				/*LOGGER.info(" {} assets references on page {} ",
						referenceSearch.search().size(), pagePath);*/
				return referenceSearch.search();
			}

		} catch (LoginException e) {		
			LOGGER.error("Exception captured in getReferencesInPage. Message: {}, Trace: {}",e.getMessage(), e);
		}
		return null;
	}
 	
 	
 	
	public static List<String> getAssetsList(Replicator replicator, String pagePath, ResourceResolverFactory resourceResolverFactory, Session session){
		List<String> assetsList = new ArrayList<String>();
		ReplicationStatus replicationStatus=null;
		Map<String, Asset> assetMap = getReferencesInPage(pagePath,resourceResolverFactory);
		if(assetMap != null){
			for (Map.Entry<String, Asset> damAsset : assetMap.entrySet()) {			
				replicationStatus=replicator.getReplicationStatus(session, damAsset.getKey().toString());
				if(replicationStatus!=null){
					if(replicationStatus.isDeactivated() || !replicationStatus.isActivated()){
						LOGGER.info("References New asset:"+damAsset.getKey());
						assetsList.add(damAsset.getKey());
					}else if(replicationStatus.getLastPublished().getTimeInMillis() < damAsset.getValue().getLastModified()){
						LOGGER.info("References Modified asset:"+damAsset.getKey());
						assetsList.add(damAsset.getKey());
					}else{
						LOGGER.info("References No change in asset"+damAsset.getKey());
					}
				}
			}
		}
		return assetsList;
	}
	public static List<String> getAbsolutePaths(String payloadPath, Session session, ResourceCollectionManager resourceCollectionManager)
			throws Exception {		
		List<String> absolutePaths = new ArrayList<String>();
		Node payLoadNode = session.getNode(payloadPath).getNode("jcr:content");		
		NodeType[] nodeType = payLoadNode.getMixinNodeTypes();
		boolean iswfPkg = Arrays
				.asList(nodeType)
				.stream()
				.anyMatch(
						node -> "vlt:Package".equalsIgnoreCase(node.getName()));
		if (iswfPkg) {
			absolutePaths = getWorkflowPkgPaths(payLoadNode,resourceCollectionManager);
		}else{
			absolutePaths.add(payloadPath);
		}		
		return absolutePaths;
	}

	private static List<String> getWorkflowPkgPaths(Node payLoadNode,ResourceCollectionManager resourceCollectionManager)
			throws RepositoryException {
		List<String> payloadAbsolutePath = new ArrayList<String>();
		String[] DEFAULT_WF_PACKAGE_TYPES = {"cq:Page", "cq:PageContent"};	
		final ResourceCollection resourceCollection = ResourceCollectionUtil
				.getResourceCollection(payLoadNode, resourceCollectionManager);
		if (null != resourceCollection) {
			final List<Node> members = resourceCollection
					.list(DEFAULT_WF_PACKAGE_TYPES);
			members.stream().forEach(member -> {
				try {
					payloadAbsolutePath.add(member.getPath());
				} catch (Exception e) {
					LOGGER.error("Exception captured. Message: {}, Trace: {} ", e.getMessage(),e);
				}
			});
		}
		return payloadAbsolutePath;
	}
	
	 public static void activateAsset(Replicator replicator,Session session,String assetPapth) {
		 try {
			replicator.replicate(session, ReplicationActionType.ACTIVATE, assetPapth);
		} catch (ReplicationException e) {
			LOGGER.error("ReplicationException captured. Message: {}, Trace: {} ", e.getMessage(),e);
		}			
	 }
	 
	 /**
	 * Method to check if a page is resource center content type
	 * @param pg
	 * @return
	 */
	 public static boolean checkIfResourceCenterContent(Page pg)
	 {
	     if(pg != null && pg.getProperties().containsKey("sling:resourceType"))
	     {
	         String resourceType = (String)pg.getProperties().get("sling:resourceType");
	         if(resourceType.contains("dotcom/components/page/resourcecentertemplate"))
	         {
	             return true;
	         }
	     }
	     return false;
	 }
	 
	 /**
	  * Method to check if a page is global alert content type
	  * 
	  * @param page
	  * @return
	  */
	 public static boolean checkIfGlobalAlertTemplate(Page page){		 
		 if(page != null && page.getProperties().containsKey("sling:resourceType"))
	     { 
	         String resourceType = (String)page.getProperties().get("sling:resourceType");
	         if(resourceType.contains("dotcom/components/page/globalalerttemplate"))
	         {
	             return true;
	         }
	     }
	     return false;		
	 }
	 
	 /**
		 * Send email to configured recipient when archival fails
		 */
		public static void sendEmail(EmailService emailService, String pagePath,List<String> emailRecipients, String templatePath, StringJoiner joiner) {

			Map<String, String> emailParams = new HashMap<>();
			String subject ="Profile pages that are to be unpublished/deleted"; 			 				
			emailParams.put("subject", subject);
			emailParams.put("senderEmailAddress",GenericEnum.SENDER_EMAIL_ADDRESS.getValue()); 
			emailParams.put("senderName",GenericEnum.SENDER_NAME.getValue());
			emailParams.put("profilePagePath", pagePath);	
			emailParams.put("profiles", joiner.toString());						
			emailService.sendEmail(templatePath, emailParams, emailRecipients);
		} 
		
		/**
		 * Sets too email recipients to list 
		 * @return 
		 */
		public static List<String> setRecipients(String approverGroup, UserManager userManager){ 
			List<String> emailRecipients = new ArrayList<String>();
			try {
				
				Authorizable authorGroupAuthorizable = userManager.getAuthorizable(approverGroup);
				if(null != authorGroupAuthorizable && authorGroupAuthorizable.isGroup()){
					Group group = (Group)authorGroupAuthorizable;
					Iterator<Authorizable> groupUsers = group.getMembers();					
					groupUsers.forEachRemaining(authorizable -> {
					try{						
						emailRecipients.add(authorizable.getProperty("./profile/email") != null? authorizable.getProperty("./profile/email")[0].getString() : "someone@SunTrust.com");
					}
					catch(Exception e){
						LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN WHILE ADDING TO USERS:::", e);
					}
					});
				}
				else
					throw new Exception("dotcom_people_finder_author group not found");
				
			} catch (Exception e) {
				LOGGER.error("EMAIL WORKFLOW -- EXCEPTION THROWN IN SET RECIPIENTS METHOD :::", e);
			}	
			return emailRecipients; 
		} 

		/**
		 * Send email to configured recipient when the profile pages are activated/deactivated.
		 */
		public static void sendEmail(EmailService emailService,
				List<String> emailRecipients,
				String templatePath, StringJoiner activateJoiner,
				StringJoiner inActivateJoiner) {
			Map<String, String> emailParams = new HashMap<>();
			String subject ="Profile pages that are published/unpublished based on NMLS Status"; 			 				
			emailParams.put("subject", subject);
			emailParams.put("senderEmailAddress",GenericEnum.SENDER_EMAIL_ADDRESS.getValue()); 
			emailParams.put("senderName",GenericEnum.SENDER_NAME.getValue());			
			emailParams.put("activatedProfiles", activateJoiner.toString());
			emailParams.put("inActiveProfiles", inActivateJoiner.toString());
			emailService.sendEmail(templatePath, emailParams, emailRecipients);
			
		}
		/**
		 * Generate unique id for page for analytics tracking
		 */
		public static String generatePageId() {
			Date currentDate = new Date();
			UUID uuid = UUID.randomUUID();
			SimpleDateFormat simpledateFormat = new SimpleDateFormat(
					"yyyy-MMdd-hhmm-ssMs");
			return simpledateFormat.format(currentDate)+"-"+String.valueOf(uuid).substring(0, 10);
		}		
		
		public static void setPageId(Node payLoadNode, Session jcrSession){
			
			try {
				payLoadNode.setProperty("pageid",generatePageId());
				LOGGER.info("PageId property:" + payLoadNode.getProperty("pageid").getString());
				jcrSession.save();
			} catch (RepositoryException e) {
				LOGGER.error("Exception occurred {} TRACE: {}" + e.getMessage(), e);
			}
		}
}
