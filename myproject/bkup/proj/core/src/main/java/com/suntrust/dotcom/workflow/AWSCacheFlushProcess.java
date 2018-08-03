package com.suntrust.dotcom.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.xf.ExperienceFragmentVariation;
import com.adobe.cq.xf.ExperienceFragmentsService;
import com.adobe.cq.xf.ExperienceFragmentsServiceFactory;
import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.asset.api.Rendition;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.collection.ResourceCollectionManager;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;
import com.suntrust.dotcom.config.AdvisorConfigService;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.AWSUtils;
import com.suntrust.dotcom.utils.GenericEnum;
import com.suntrust.dotcom.utils.Utils;

/*
 * Revision history: 
 * 
 * 2 Feb 2018	Nanda	US49302 Refactor Global Alert process
 * 
 */

/**
 * @author uiam82
 *
 */
@Component
@Service
@Properties({
		@Property(name = Constants.SERVICE_DESCRIPTION, value = "Suntrust AWS Cache Flush Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(name = "process.label", value = "Suntrust AWS Cache Flush Service") })
public class AWSCacheFlushProcess implements WorkflowProcess {
	@Reference
	ResourceResolverFactory resourceResolverFactory;
	@Reference
	private ResourceCollectionManager resourceCollectionManager;
	@Reference
	private AdvisorConfigService configService;
	@Reference
	private SuntrustDotcomService dotcomService;
	@Reference
	ExperienceFragmentsServiceFactory experienceFragmentsServiceFactory;
	@Reference
	private Replicator replicator;


	private static final String RATETYPEPARAM="ratetype";
	private String rateType = null;
	ResourceResolver resourceResolver = null;
	PageManager pageManager = null;
	Session session = null;
	private String pagePath = null;
	String advisorPath= null;
	String locationPath =null;
	String cdRatesPath =null;
	String equityRatesPath =null;
	List<String> canonicalUrl = null;
	String momentumpagePath = null;
	int expFragmentPageMaxSize = 100;
	int awsflushlimit= 10;
	long awsflushwaittime = 180000;
	String atmLocationPath = null;
	String branchLocationPath = null;
	String cqHandle="Activate";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AWSCacheFlushProcess.class);

	@Override
	/**
	 *
	 * Default method that executes on respective process step of the workflow.
	 */
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
						MetaDataMap metaDataMap) {
		try {
			pagePath = workItem.getWorkflowData().getPayload().toString();
			session = workflowSession.getSession();
			Node payLoadNode=session.getNode(pagePath).getNode("jcr:content");
			/** Resetting variable */
			rateType = null;
			if(null != payLoadNode && payLoadNode.hasProperty(RATETYPEPARAM)){
				rateType = payLoadNode.getProperty(RATETYPEPARAM).getValue().toString();
			}
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);
			pageManager = resourceResolver.adaptTo(PageManager.class);
			advisorPath=configService.getPropertyValue("ROOT_PATH")+"/profile";
			locationPath=configService.getPropertyValue("ROOT_PATH")+"/locations";
			atmLocationPath = locationPath+"/atm";
			branchLocationPath=locationPath+"/branch";
			cdRatesPath=dotcomService.getPropertyValue("cdrates.url");
			equityRatesPath=dotcomService.getPropertyValue("eqrates.url");
			canonicalUrl = dotcomService.getPropertyArray("canonical.urls");
			momentumpagePath = configService.getPropertyValue("ROOT_PATH")+"/momentumonup";
			String threadwaittime =  dotcomService.getPropertyValue("awsflushwaittime");
			try
			{
				awsflushwaittime =  Long.parseLong(threadwaittime);
			}
			catch(NumberFormatException nfe)
			{
				LOGGER.error("Number format exception thrown"+nfe.getMessage());
				awsflushwaittime=180000;
			}
			List<String> pathDetails = Utils.getAbsolutePaths(pagePath,session,resourceCollectionManager);
			if(metaDataMap.containsKey("PROCESS_ARGS")){
				cqHandle = (metaDataMap.get("PROCESS_ARGS","string").toString()).trim();
			}
			Iterator<String> iterator = pathDetails.iterator();
			Set<String> awsflushpath = new HashSet<String>();
			Set<String> dispatcherflushpath = new HashSet<String>();
			while (iterator.hasNext()) {
				setUrlPattern(iterator.next(),awsflushpath,dispatcherflushpath);
			}
			//do the dispatcher flush and aws flush
			LOGGER.debug("aws count"+awsflushpath.size());
			LOGGER.debug("disp count"+dispatcherflushpath.size());
			Iterator<String> awsiterator = awsflushpath.iterator();
			Iterator<String> dispiterator = dispatcherflushpath.iterator();
			while(awsiterator.hasNext())
			{
				LOGGER.debug("aws flush url:"+awsiterator.next());
			}
			while(dispiterator.hasNext())
			{
				LOGGER.debug("disp flush url:"+dispiterator.next());
			}
			//flush dispatcher cache
			AWSUtils.flushDispatcher(dispatcherflushpath,cqHandle,dotcomService);
			//flushDispatcher(dispatcherflushpath,cqHandle);
			//flush aws cache
			//flushAWSCache(awsflushpath);
			AWSUtils.flushAWSCache(awsflushpath,dotcomService);
			


		} catch (Exception e) {
			LOGGER.error("Exception captured in execute method of AWS Cache Flush Process. Message: {}, Trace: {} "+e.getMessage(), e);
		}
	}

	/**
	 * This method  colllects aws urls for different use cases and set the pattern
	 * @param urlPath
	 * @param awsurl
	 * @param dispflushurl
	 */
	private void setUrlPattern(String urlPath,Set<String> awsurl,Set<String> dispflushurl){
		LOGGER.debug("Inside setUrlPattern(). Actual Page Path: "+urlPath);
		/** Advisor */
		if(urlPath.toLowerCase().contains(advisorPath.toLowerCase())){
			awsurl.add(Utils.getCanonicalUrl(canonicalUrl,advisorPath,resourceResolver)+"*");
			awsurl.add(Utils.getCanonicalUrl(canonicalUrl,advisorPath,resourceResolver)+"/*");
		}/** Atm Location */
		else if(urlPath.toLowerCase().contains(atmLocationPath.toLowerCase())){
			awsurl.add(Utils.getCanonicalUrl(canonicalUrl,atmLocationPath,resourceResolver)+"*");
			awsurl.add(Utils.getCanonicalUrl(canonicalUrl,atmLocationPath,resourceResolver)+"/*");
		}/** Branch Location */
		else if(urlPath.toLowerCase().contains(branchLocationPath.toLowerCase())){
			awsurl.add(Utils.getCanonicalUrl(canonicalUrl,branchLocationPath,resourceResolver)+"*");
			awsurl.add(Utils.getCanonicalUrl(canonicalUrl,branchLocationPath,resourceResolver)+"/*");
		}/** RC pages */
		else if(Utils.checkIfResourceCenterContent(pageManager.getPage(urlPath))){
			String rcpath = dotcomService.getPropertyValue("resourcecenter.url");
			updateWrapperPaths(urlPath,rcpath,awsurl,dispflushurl);
		}/** CD rates path */
		else if(urlPath.toLowerCase().contains(cdRatesPath.toLowerCase())){
			awsurl.add(Utils.getCanonicalUrl(canonicalUrl,cdRatesPath,resourceResolver)+"*");
			dispflushurl.add(cdRatesPath+".html"); //need to check
		}/** Equity rates path */
		else if(urlPath.toLowerCase().contains(equityRatesPath.toLowerCase())){
			awsurl.add(Utils.getCanonicalUrl(canonicalUrl,equityRatesPath,resourceResolver)+"*");
			dispflushurl.add(equityRatesPath+".html"); //need to check
		}/** CD/Equity rates workflow */
		else if(rateType!=null){
			if("cdrates".equalsIgnoreCase(rateType)){
				awsurl.add(Utils.getCanonicalUrl(canonicalUrl,cdRatesPath,resourceResolver)+"*");
				dispflushurl.add(cdRatesPath+".html"); //need to check
				LOGGER.debug("inside rates if cdRatesPath");
			}else if("equityrates".equalsIgnoreCase(rateType)){
				awsurl.add(Utils.getCanonicalUrl(canonicalUrl,equityRatesPath,resourceResolver)+"*");
				dispflushurl.add(equityRatesPath+".html"); //need to check
				LOGGER.debug("inside rates if equity rates path");
			}
		}/** Exerience fragments */
		else if (urlPath.toLowerCase().startsWith("/content/experience-fragments"))
		{
			setExperienceFragmentPages(urlPath,awsurl,dispflushurl);
		}/** Dam assets */
		else if(urlPath.toLowerCase().startsWith("/content/dam"))
		{
			awsurl.add(urlPath);
			awsurl.add(urlPath+"*");
			awsurl.add(urlPath+"/*");
		}/** GlobalAlert pages */
		else if(Utils.checkIfGlobalAlertTemplate(pageManager.getPage(urlPath))){
			LOGGER.debug("======> GlobalAlertTemplate page");
			LOGGER.debug("======> Actual Page Path: "+urlPath);
			awsurl.add(Utils.getCanonicalUrl(canonicalUrl,urlPath,resourceResolver)+"*");
			dispflushurl.add(urlPath+".html");
			 Page CurrentPage = pageManager.getPage(urlPath);
			 if(null != CurrentPage.getContentResource("globalalert")){
				 Node globalNode = CurrentPage.getContentResource("globalalert").adaptTo(Node.class);
				 Value[] value = null;
				 if(globalNode != null)
				 {
					try {
						if(globalNode.hasProperty("globalalert"))
						{
							javax.jcr.Property currentProperty;
							try {
								currentProperty = globalNode.getProperty("globalalert");
								if (currentProperty.isMultiple()) {
									value = currentProperty.getValues();
								} else {
									value = new Value[1];
									value[0] = currentProperty.getValue();
								}
							} catch (PathNotFoundException e) {
								LOGGER.error("PathNotFoundException : Message: {}, Trace: {}",e.getMessage(), e);
								//e.printStackTrace();
							} catch (RepositoryException e) {
								LOGGER.error("RepositoryException1 : Message: {}, Trace: {}",e.getMessage(), e);
								//e.printStackTrace();
							}
						}
					} catch (RepositoryException e) {
						LOGGER.error("RepositoryException2 : Message: {}, Trace: {}",e.getMessage(), e);
					}
				 }
				 if(value != null){
					 JSONObject jObj = null;
					 for (int i = 0; i < value.length; i++) {
						 try {
							jObj = new JSONObject(value[i].getString());
							String globalAlertPath  = jObj.getString("alertpath");
							LOGGER.debug("globalAlertPath is::"+globalAlertPath);
							String pageCanonicalUrl = Utils.getCanonicalUrl(canonicalUrl,globalAlertPath,resourceResolver);
							// US49302 - Start
							// if en page clear en page cache alone and then get all lob page and clear
							if(GenericEnum.BACKWORD_SLASH_SYMBOL.getValue().equalsIgnoreCase(pageCanonicalUrl))
							{
								// EN page added to aws list
								awsurl.add(pageCanonicalUrl);
								//Getting all LOB and adding those to aws and dispatcher list
								Page pageObject = pageManager.getPage(globalAlertPath);
								if(pageObject != null){
									Iterator<Page> itr = pageObject.listChildren(new PageFilter(),false);
									while(itr.hasNext())
									{
										Page lobPage = itr.next();
											//All page under 
											dispflushurl.add(lobPage.getPath()+".html");
											awsurl.add(Utils.getCanonicalUrl(canonicalUrl,lobPage.getPath(),resourceResolver)+"*");
											awsurl.add(Utils.getCanonicalUrl(canonicalUrl,lobPage.getPath(),resourceResolver)+"/*");
									}
								}
							} else {
								// Not en page
								awsurl.add(pageCanonicalUrl+"*");
								awsurl.add(pageCanonicalUrl+"/*");
							}
							// US49302 - End
							dispflushurl.add(globalAlertPath +".html");
						} catch (ValueFormatException e) {
							LOGGER.error("ValueFormatException : Message: {}, Trace: {}",e.getMessage(), e);
						} catch (IllegalStateException e) {
							LOGGER.error("IllegalStateException : Message: {}, Trace: {}",e.getMessage(), e);
						} catch (JSONException e) {
							LOGGER.error("JSONException : Message: {}, Trace: {}",e.getMessage(), e);
						} catch (RepositoryException e) {
							LOGGER.error("RepositoryException3 : Message: {}, Trace: {}",e.getMessage(), e);
						}
					 }
				 }
			 }
		}/** Other content */
		else{
			String finalurl = Utils.getCanonicalUrl(canonicalUrl,urlPath,resourceResolver);
			if(!GenericEnum.BACKWORD_SLASH_SYMBOL.getValue().equalsIgnoreCase(finalurl))
			{
				finalurl=finalurl+"*";
			}
			awsurl.add(finalurl);
		}
		/** Set sitemap for non asset ,non experience fragment path */
		if( !(urlPath.startsWith("/content/dam") || urlPath.startsWith("/content/experience-fragments")))
		{
			setSitemapUrl(urlPath,awsurl,dispflushurl);
		}
		/** Set referenced assets */
		if(!urlPath.startsWith("/content/dam") && "Activate".equalsIgnoreCase(cqHandle))
		{
			setReferencedAssets(urlPath,awsurl);
		}

	}

	/**
	 * This method collects the referenced assets (modified/added/deactivated) from each payload
	 * @param urlPath
	 * @param awsurl
	 */
	private void setReferencedAssets(String urlPath,Set<String> awsurl)
	{
		List<String> assetList = new ArrayList<String>();
		assetList = Utils.getAssetsList(replicator, urlPath,
				resourceResolverFactory, session);
		awsurl.addAll(assetList);
	}

	/**
	 * This method sets all the referenced page from the experience fragment payload.
	 * @param xfUrl
	 * @param awsurl
	 * @param dispflushurl
	 */
	private void setExperienceFragmentPages(String xfUrl,Set<String> awsurl,Set<String> dispflushurl)
	{
		Resource resource=resourceResolver.getResource(xfUrl);
		Page page = resource.adaptTo(Page.class);
		ArrayList<Page> pageList  = new ArrayList<Page>();
		ExperienceFragmentsService xfService = experienceFragmentsServiceFactory.getExperienceFragmentsService(resourceResolver);
		if(page.getContentResource().isResourceType("dotcom/components/page/xfpage"))
		{
			page = page.getParent();
		}
		if (page.getContentResource().isResourceType("cq/experience-fragments/components/experiencefragment")) { //Execute if payload is XF
			Iterator<Page> fragmentVariation = page.listChildren(new PageFilter(), false);
			while (fragmentVariation.hasNext()) {
				Page fragmentPage = fragmentVariation.next();
				if (null != fragmentPage) {
					ExperienceFragmentVariation var = fragmentPage.adaptTo(ExperienceFragmentVariation.class);
					if (null != var) {
						pageList.addAll(xfService.listPagesUsingVariation(var));
						LOGGER.debug("page list size" + pageList.size());
					} else {
						LOGGER.warn("The page at path {} cannot be adapted to an experience fragment variation", fragmentPage.getPath());
					}
				}
			}
		}

		// set the aws and dispatcher url
		Iterator<Page> pageItr = pageList.iterator();
		int size = pageList.size();
		while(pageItr.hasNext()) {
			Page contentPage = pageItr.next();
			dispflushurl.add(contentPage.getPath() + ".html");
			String canonicalurl = Utils.getCanonicalUrl(canonicalUrl, contentPage.getPath(), resourceResolver);
			if (contentPage.getPath().toLowerCase().contains(advisorPath.toLowerCase())) {
				awsurl.add(Utils.getCanonicalUrl(canonicalUrl,advisorPath,resourceResolver)+"*");
				awsurl.add(Utils.getCanonicalUrl(canonicalUrl,advisorPath,resourceResolver)+"/*");
			} else if (contentPage.getPath().toLowerCase().contains(atmLocationPath.toLowerCase())) {
				awsurl.add(Utils.getCanonicalUrl(canonicalUrl,atmLocationPath,resourceResolver)+"*");
				awsurl.add(Utils.getCanonicalUrl(canonicalUrl,atmLocationPath,resourceResolver)+"/*");
			}
			else if (contentPage.getPath().toLowerCase().contains(branchLocationPath.toLowerCase())) {
				awsurl.add(Utils.getCanonicalUrl(canonicalUrl,branchLocationPath,resourceResolver)+"*");
				awsurl.add(Utils.getCanonicalUrl(canonicalUrl,branchLocationPath,resourceResolver)+"/*");
			}
			else if (contentPage.getPath().toLowerCase().contains(momentumpagePath.toLowerCase())) {
				awsurl.add(Utils.getCanonicalUrl(canonicalUrl,momentumpagePath,resourceResolver)+"*");
				awsurl.add(Utils.getCanonicalUrl(canonicalUrl,momentumpagePath,resourceResolver)+"/*");
			}
			else if (!GenericEnum.BACKWORD_SLASH_SYMBOL.getValue().equalsIgnoreCase(canonicalurl)) {
				if(size > expFragmentPageMaxSize)
				{
					//create requests only on thelob page level
					if(contentPage.getDepth() > 5) {
						String level1page = contentPage.getAbsoluteParent(5).getPath();
						awsurl.add(Utils.getCanonicalUrl(canonicalUrl,level1page,resourceResolver)+"*");
						awsurl.add(Utils.getCanonicalUrl(canonicalUrl,level1page,resourceResolver)+"/*");
					}
					else
					{
						awsurl.add(canonicalurl + "*");
					}
				}
				else {
					awsurl.add(canonicalurl + "*");
				}
			} else {
				awsurl.add(canonicalurl);
			}
		}
	}


	/**
	 * This method sets the sitemap url to be flushed
	 * @param pageUrl
	 * @param awsurl
	 * @param dispflushurl
	 */
	private void setSitemapUrl(String pageUrl,Set<String> awsurl,Set<String> dispflushurl)
	{
		List<String> lobTypes = dotcomService.getPropertyArray("lob.Types");
		Page pg = pageManager.getPage(pageUrl);
		if(pg.getDepth() == 6)
		{
			//site map lob page is getting update,so flush the sitemap index page
			String indexpage = dotcomService.getPropertyValue("sitemap.index.url");
			awsurl.add(Utils.getCanonicalUrl(canonicalUrl,indexpage+".index.xml",resourceResolver));
			dispflushurl.add(indexpage+".index.xml");
		}
		if(pageUrl.contains(locationPath) && pg.getDepth() > 6)
		{
			String level1page = pg.getAbsoluteParent(6).getPath();
			if(lobTypes.contains(level1page))
			{
				dispflushurl.add(level1page+".index.xml");
			}
		}
		else
		{
			if(pg.getDepth() > 5) {
				String level1page = pg.getAbsoluteParent(5).getPath();
				if (lobTypes.contains(level1page)) {
					if (!(level1page.contains(locationPath) || level1page.contains(advisorPath))) {
						awsurl.add(Utils.getCanonicalUrl(canonicalUrl, level1page + ".index.xml", resourceResolver));
					}
					//we dont need to add the url to flush if its lob page is in the level1
					dispflushurl.add(level1page + ".index.xml");
				}
			}

		}
	}


	/**
	 * Method to collect wrapper paths for article urls
	 * @param articleurl
	 * @param rcUrl
	 * @param awsurl
	 * @param dispflushurl
	 */
	private void updateWrapperPaths(String articleurl,String rcUrl,Set<String> awsurl,Set<String> dispflushurl)
	{
		Page pg = pageManager.getPage(rcUrl);
		if(pg != null){
			Iterator<Page> itr = pg.listChildren(new PageFilter(),false);
			while(itr.hasNext())
			{
				Page lobPage = itr.next();
				if(lobPage.hasChild("wrapper"))
				{
					//this is a lob page
					String dispatcherurl = lobPage.getPath()+"/wrapper.html"+articleurl;
					dispflushurl.add(dispatcherurl+".html");
					awsurl.add(Utils.getCanonicalUrl(canonicalUrl,dispatcherurl,resourceResolver)+"*");
				}
			}
		}
	}

	/**
	 * Method to flush aws cache - in sets of 10 with delay of 3 mins
	 * @param awsurls
	 * @return
	 */
	/*private boolean flushAWSCache(Set<String> awsurls) {

		AmazonCloudFrontClient awsClient = null;
		try {
			Set<String> set1 = null;
			List<String> arrayList = new ArrayList<String>();
			arrayList.addAll(awsurls);
			int i1 = (int) Math.ceil(arrayList.size()/awsflushlimit);
			List<String> sublist = new ArrayList<String>();
			int x = 0;
			List<Set<String>> finalSet = new ArrayList<Set<String>>();
			if(arrayList.size() > awsflushlimit) {
				for (int p = 0; p < i1; p++) {
					if (arrayList.size() >= (x + awsflushlimit)) {
						sublist = new ArrayList<String>(arrayList.subList(x, x + awsflushlimit));
						set1 = new HashSet<String>(sublist);
						x += awsflushlimit;
					} else {
						sublist = new ArrayList<String>(arrayList.subList(x, arrayList.size()));
						set1 = new HashSet<String>(sublist);
					}
					finalSet.add(set1);
					LOGGER.debug("sublist" + sublist);
				}
			}
			else
			{
				finalSet.add(awsurls);
			}
			Iterator<Set<String>> itr =  finalSet.iterator();
			awsClient = new AWSClient().getAWSClient(dotcomService);
			while(itr.hasNext())
			{
				Set<String> urlSet = itr.next();
				Paths invalidatePath = new Paths();
				invalidatePath.withItems(urlSet);
				invalidatePath.withQuantity(urlSet.size());
				LOGGER.debug("set size"+urlSet.size());
				InvalidationBatch invalidateBatch = new InvalidationBatch(
						invalidatePath, UUID.randomUUID().toString());
				if(dotcomService !=null){
					CreateInvalidationRequest invalidation = new CreateInvalidationRequest(
							dotcomService.getPropertyValue(AWSDISTRIBUTIONID), invalidateBatch);
					if(awsClient != null) {
						CreateInvalidationResult invalidationResult = awsClient.createInvalidation(invalidation);
						LOGGER.debug("Cleared the CDN @Location " + invalidationResult.getLocation());
						if(finalSet.size() > 1) {
							Thread.sleep(awsflushwaittime);
							LOGGER.debug("After thread sleep");
						}
					} else {
						LOGGER.error("AmazonCloudFrontClient credential creation returned null");
					}
				}
			}
			return true;
		} catch (AmazonServiceException ase) {
			LOGGER.error("AmazonServiceException captured: "+ase.getStackTrace()+":"+ase.getErrorMessage());
			LOGGER.error("AmazonServiceException error code"+ase.getErrorCode());
			return false;
		} catch (AmazonClientException ace) {
			LOGGER.error("AmazonClientException captured: "+ace.getMessage());
			return false;
		} catch (Exception e){
			LOGGER.error("Exception captured: "+e.getMessage());
			return false;
		}
		finally {
			if(awsClient != null)
			{
				awsClient.shutdown();
			}
		}
	}*/

	/**
	 * Method to flush the dispatcher urls
	 * @param dispUrls
	 * @param cqAction
	 */
	/*public void flushDispatcher(Set<String> dispUrls,String cqAction)
	{
		List<String> dispatcherUrls = dotcomService.getPropertyArray("dispatcher-urls");
		Iterator<String> dispatcheritr = dispUrls.iterator();
		while(dispatcheritr.hasNext())
		{
			String pageurl = dispatcheritr.next();
			Iterator<String> itr = dispatcherUrls.iterator();
			while (itr.hasNext()) {
				String dispatcherUrl = itr.next();
				boolean status  = flushDispatcherCache(dispatcherUrl,cqAction,pageurl);
				if(!status) {
					LOGGER.error("Page url not flushed in dispatcher: "+ pageurl);
				}
			}
		}
	}*/

	/**
	 * Method to initiate the flush request for dispatcher
	 * @param dispatcherURL
	 * @param cqAction
	 * @param pagePath
	 * @return
	 */
	/*public boolean flushDispatcherCache(String dispatcherURL,String cqAction,String pagePath) {
		PostMethod post = null;
		HttpClient client = new HttpClient();
		try {
			post = new PostMethod(dispatcherURL);
			post.setRequestHeader("CQ-Action", cqAction);
			post.setRequestHeader("CQ-Handle",pagePath);
			int status = client.executeMethod(post);
			LOGGER.debug("Client execute method status: "+status);
			if(status==200){
				return true;
			}

		} catch (Exception e) {
			LOGGER.error("Exception captured in flushDispatcherCache method: "+e.getMessage());
			return false;
		} finally{
			if(post != null){
				post.releaseConnection();
			}
		}
		return false;
	}*/


}

