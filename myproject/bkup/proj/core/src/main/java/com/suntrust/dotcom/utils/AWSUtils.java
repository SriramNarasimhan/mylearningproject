package com.suntrust.dotcom.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;
import com.amazonaws.services.cloudfront.model.Paths;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.workflow.AWSClient;


/**
 * Purpose - The AWS util code provides the common AWS related utility methods which
 * can be used in other classes based on the needs.
 */

public class AWSUtils{

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AWSUtils.class);
	private static final String AWSDISTRIBUTIONID="aws-distribution-id";

	String cqHandle="Activate";
	

	/**
	 * Method to flush aws cache - in sets of 10 with delay of 3 mins
	 * @param awsurls
	 * @return
	 */
	public static boolean flushAWSCache(Set<String> awsurls, SuntrustDotcomService dotcomService) { 		

		AmazonCloudFrontClient awsClient = null;
		try {
			// get it from STDotcom service
			int awsflushlimit = Integer.parseInt(dotcomService.getPropertyValue("awsflushlimit"));
			LOGGER.debug("awsflushlimit::"+awsflushlimit);
			long awsflushwaittime = Long.parseLong(dotcomService.getPropertyValue("awsflushwaittime"));
			LOGGER.debug("awsflushwaittime::"+awsflushwaittime);
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
	}

	/**
	 * Method to flush the dispatcher urls
	 * @param dispUrls
	 * @param cqAction
	 */
	public static void flushDispatcher(Set<String> dispUrls,String cqAction,SuntrustDotcomService dotcomService)
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
	}

	/**
	 * Method to initiate the flush request for dispatcher
	 * @param dispatcherURL
	 * @param cqAction
	 * @param pagePath
	 * @return
	 */
	private static boolean flushDispatcherCache(String dispatcherURL,String cqAction,String pagePath) {
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
	}

	}