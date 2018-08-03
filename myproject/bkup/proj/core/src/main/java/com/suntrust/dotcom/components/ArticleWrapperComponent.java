package com.suntrust.dotcom.components;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.engine.SlingRequestProcessor;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.commons.Externalizer;
import com.day.cq.contentsync.handler.util.RequestResponseFactory;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.WCMMode;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.Utils;

public class ArticleWrapperComponent extends WCMUsePojo{
    Logger logger = LoggerFactory.getLogger(ArticleWrapperComponent.class);
    protected String articlePath;
    /** Externalizer class reference variable */
	private Externalizer externalizer=null;
	private final String ROOT_PATH ="/content/suntrust/jcr:content";
	String publishDomain = null;
	
    @Override
    public void activate(){
        articlePath=getRequest().getRequestPathInfo().getSuffix();
        Resource res= null;
        if(StringUtils.isNotBlank(articlePath) && articlePath.endsWith(".html"))
        {
            articlePath = articlePath.substring(0,articlePath.indexOf(".html"));
        	externalizer=getResourceResolver().adaptTo(Externalizer.class);
        	publishDomain = externalizer.publishLink(getResourceResolver(), "/");
            res = getResourceResolver().getResource(articlePath);
            if(res == null)
            {
            	logger.error("Res not found " + articlePath);
                try {
                	getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
                	/*ValueMap vMap = getResourceResolver().getResource(ROOT_PATH).adaptTo(ValueMap.class);
                	if(vMap.containsKey("errorPages")){
                		getResponse().sendRedirect(vMap.get("errorPages",String.class)+"/404.html");
                	}*/
                	
                }
                catch (Exception io)
                {
                    logger.error("IO exception occured",io);
                }
            }
        }
        logger.info("article suffix path "+articlePath);
        if(res==null || articlePath == null){
            articlePath = "error";
        }
        else {
        /* Setup request */
            try {
                RequestResponseFactory requestResponseFactory = getSlingScriptHelper().getService(RequestResponseFactory.class);
                SlingRequestProcessor requestProcessor = getSlingScriptHelper().getService(SlingRequestProcessor.class);
                if(!articlePath.endsWith(".html"))
                {
                    articlePath = articlePath+".html";
                }
                HttpServletRequest req = requestResponseFactory.createRequest("GET", articlePath);
                WCMMode.DISABLED.toRequest(req);

        /* Setup response */
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                HttpServletResponse resp = requestResponseFactory.createResponse(out);
        /* Process request through Sling */
                requestProcessor.processRequest(req, resp, getResourceResolver());
                logger.info("==========================> out.toString(): "+out.toString());
                articlePath = out.toString("UTF-8");
                articlePath = articlePath.replaceAll("href=\"/", "href=\""+publishDomain);
                articlePath = articlePath.replaceAll("href=\""+publishDomain+"content/dam", "href=\"/content/dam");
                logger.info("==========================> RC article path: "+articlePath);
            }
            catch(Exception e)
            {
                logger.error("Error getting page html",e);
                articlePath="error";
            }
        }
    }


    public void bindArticle()
    {
    	try{
        String suffix = getRequest().getRequestPathInfo().getSuffix();
        if(StringUtils.isNotBlank(suffix))
        {
            if(!suffix.endsWith(".html"))
            {
                getResponse().sendRedirect(suffix + ".html");
            }
            {
                getResponse().sendRedirect(suffix );
            }
        }
    }
    	catch(Exception e){
    		logger.error("error encountered in bindArticle method",e);
    	}
    }
    public String getSuffix()
    {
        String suffix = getRequest().getRequestPathInfo().getSuffix();
        if(StringUtils.isBlank(suffix))
        {

            return "nosuffix";
        }
        return suffix;
    }

    public void bindArticleWrapper()
    {
        logger.info("inside article wrapper bind");
        //String pathNoExtension="";
        //1. Retrieve the article path
        //articleRelativePath= request.getRequestURI();
        try{
        String suffix = getRequest().getRequestPathInfo().getSuffix();
        String wrapperpath = "";
        if(StringUtils.isBlank(suffix)) {
            String articleRelativePath = getCurrentPage().getPath();
            //pathNoExtension=articleRelativePath.substring(0,articleRelativePath.lastIndexOf("."));
            //2. Retrieve the article wrapper path
            Resource res =  getResourceResolver().getResource(articleRelativePath);
            //find pages by tags
            if (res != null) {
                Page resPage = res.adaptTo(Page.class);
                if (resPage != null) {
                    String primaryArticleTag = (resPage.getProperties().containsKey("primarytag")) ? (String) resPage.getProperties().get("primarytag") : "";
                    wrapperpath = Utils.getWrapperPath(primaryArticleTag,getResourceResolver());
                }
            }
            logger.info("wrapper path" + wrapperpath);
            boolean isPublish = Utils.isPublishRunMode(getSlingScriptHelper().getService(SlingSettingsService.class));
            SuntrustDotcomService dotcomService = getSlingScriptHelper().getService(SuntrustDotcomService.class);
            if (StringUtils.isNotBlank(wrapperpath)) {
                String finalPath = wrapperpath +".html"+ articleRelativePath;
                if(isPublish && getRequest().getRequestParameterMap().containsKey("pdfoutput") == false)
                {
                    List<String> values = dotcomService.getPropertyArray("canonical.urls");
                    finalPath = Utils.formatPageUrl(values,finalPath);
                    logger.debug("final article url"+finalPath);
                    //finalPath = Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),finalPath,getResourceResolver());
                }
                else
                {
                    finalPath = finalPath+".html";
                }
                getResponse().sendRedirect(finalPath);
            } else {
                String rcurl = dotcomService.getPropertyValue("resourcecenter.url");
                if(isPublish)
                {
                    rcurl = Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),rcurl,getResourceResolver());
                }
                else
                {
                    rcurl = rcurl+".html";
                }
                getResponse().sendRedirect(rcurl);
            }
        }
    }
        catch(Exception e){
        	logger.error("Exception caught in bindArticleWrapper method.",e);
        }
        
    }
    private boolean isExactTagMatch(String primaryRCTag , Tag[] tagsList)
    {
        if(StringUtils.isNotBlank(primaryRCTag) && !ArrayUtils.isEmpty(tagsList))
        {
            for(int i=0;i<tagsList.length;i++)
            {
                Tag subTag = tagsList[i];
                if(subTag.getTagID().equalsIgnoreCase(primaryRCTag))
                {
                    return true;
                }

            }
        }
        return false;
    }

    public String getArticlePath(){
        return articlePath;
    }


    //once he is landed in wrapper
    public void setTagsForHeroHeadline()
    {
        String tagTitle = "";
        String pathForMenu ="";
        //current page is wrapper page
        Tag[] tags = getCurrentPage().getParent().getTags();
        //get resource center lob tag incase if the lob page has other tags too
        if(tags != null)
        {
            //check in the wrapper page which tag is matching it cuold be primary or secondary
            String articlepagepath=getRequest().getRequestPathInfo().getSuffix();
            logger.info("article page path "+articlepagepath);
            if(StringUtils.isNotBlank(articlepagepath) && articlepagepath.endsWith(".html"))
            {
                articlepagepath = articlepagepath.substring(0,articlepagepath.indexOf(".html"));
            }
            Resource res=getResourceResolver().getResource(articlepagepath);
            if(res==null || articlepagepath == null){
                articlepagepath = "error";
            }
            else
            {
                Page resPage = res.adaptTo(Page.class);
                TagManager tagManager = getResourceResolver().adaptTo(TagManager.class);
                if (resPage != null && resPage.getProperties().containsKey("primarytag")) {
                    String primaryRCTag = (String) resPage.getProperties().get("primarytag");
                    boolean isAttrSet  = setAttributes(primaryRCTag,tags,tagManager);
                    //go for secondary tag
                    if(!isAttrSet)
                    {
                        Tag[] cqTags = resPage.getTags();
                        for(int len=0;len<cqTags.length;len++)
                        {
                            Tag secTag = cqTags[len];
                            boolean isSecAttrSet = setAttributes(secTag.getTagID(),tags,tagManager);
                            if(isSecAttrSet)
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean setAttributes(String tagId,Tag[] tags,TagManager tagManager)
    {
        boolean isSecMatch = Utils.isLobTagMatch(tagId,tags);
        String tagTitle ="";
        if(isSecMatch)
        {
            Tag internalTag = tagManager.resolve(tagId);
            tagTitle= internalTag.getTitle();
            getRequest().setAttribute("heroheadlineTitle", tagTitle);
            getRequest().setAttribute("articleTag",tagId);
            Page lobPage = getCurrentPage().getParent();
            //first check if lob page matches
            //then loop thor
            boolean isTagsMatch = isExactTagMatch(tagId,lobPage.getTags());
            if(isTagsMatch)
            {
                getRequest().setAttribute("internalNavPage", lobPage.getPath());
                getRequest().setAttribute("headerNavPage", lobPage.getPath());
                 getRequest().setAttribute("mobileHeaderNavPage", lobPage.getPath());
            }
            else
            {
                Iterator<Page> pgItr = lobPage.listChildren(new PageFilter(getRequest()),true);
                while(pgItr.hasNext())
                {
                    Page childPage = pgItr.next();

                    boolean isSubTagsMatch = isExactTagMatch(tagId,childPage.getTags());
                    if(isSubTagsMatch)
                    {
                        getRequest().setAttribute("internalNavPage", childPage.getPath());
                        if(childPage.getDepth() == 9) //means its a level 4 page
                        {
                            getRequest().setAttribute("mobileHeaderNavPage", childPage.getPath());
                            getRequest().setAttribute("headerNavPage", childPage.getParent().getPath());
                        }
                        else if (childPage.getDepth() > 9 )
                        {
                          getRequest().setAttribute("mobileHeaderNavPage", childPage.getParent().getPath());
                          getRequest().setAttribute("headerNavPage", childPage.getParent().getPath());
                                
                        }
                        else
                        {
                            getRequest().setAttribute("headerNavPage", childPage.getPath());
                            getRequest().setAttribute("mobileHeaderNavPage", childPage.getPath());
                        }
                        break;
                    }
                }
            }
            return true;
        }
        return false;
    }
	}
