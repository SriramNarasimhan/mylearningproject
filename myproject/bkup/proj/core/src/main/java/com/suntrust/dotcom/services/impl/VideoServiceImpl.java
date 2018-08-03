package com.suntrust.dotcom.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.ServiceAgentService;
import com.suntrust.dotcom.services.VideoService;

/**
 * This VideoServiceImpl is used to fetch the Video files details
 * @author Cognizant
 * @version 1.0
 * @since 10 July 2017 
 */
@Component(immediate=true, metatype=true, label="Disclaimer Content Path Configuration Service")
@Service
public class VideoServiceImpl implements VideoService{	
	 
	@Property(unbounded=PropertyUnbounded.DEFAULT, label="Disclaimer Text", description="Path for disclaimer Text")
	/**
	 * This Logger is used log the Video component
	*/
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoServiceImpl.class);
	/**
	 * This is used to retrieve disclaimer path values
	 */
	private String disclaimerPathStr= null;
	
	/**
	 * This is used to retrieve serviceAgent
	 */
	@Reference
	private ServiceAgentService serviceAgent;
	
	/**
	 * This is used to retrieve suntrust DotcomService
	 */
	@Reference
	private SuntrustDotcomService suntrustDotcomService;
	 
	/**
	 * Gets called on bundle activate.
	 * 
	 * @throws RepositoryException
	 */
	@Activate
	protected void activate(Map<String, Object> properties)
	{ 
	    LOGGER.info("[*** AEM Config : activating configuration service : Disclaimer Path");
	    if (suntrustDotcomService != null) {
	    	disclaimerPathStr = suntrustDotcomService.getPropertyValue("video.disclaimer.content.path");
	    	LOGGER.info("VideoServiceImpl activate disclaimerPathStr "+disclaimerPathStr);	
        }
	}
	
	
	/**
	 * @param videoUrl
	 * @return Video transcript
	 */	
	@Override
	public String getVideoTranscript(String videoUrl) {
		LOGGER.info("VideoServiceImpl : getVideoTranscript() : START");	
		ResourceResolver resourceResolver = null;
		String videoTranscript = null;
		String fullUrl = null;
		fullUrl = videoUrl+"/jcr:content/metadata"; 
		 
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(fullUrl);
			if(res != null){
				ValueMap properties = res.adaptTo(ValueMap.class);
				videoTranscript = properties.get("dc:transcript", String.class);
				return videoTranscript;
			}
		}
		catch (LoginException |RepositoryException e) {
			// TODO Auto-generated catch block
			LOGGER.error("VideoServiceImpl : getVideoTranscript() : Exception{}:",e.getMessage(), e);
		}
		finally{
			resourceResolver.close();
			LOGGER.info("VideoServiceImpl : getVideoTranscript() : ENDS :" + videoTranscript);			
		}
		return videoTranscript;
	}
	
	/**
	 * @param disclaimerUrl
	 * @return Video disclaimer
	 */
	@Override
	public String getVideoDisclaimer(String disclaimerUrl) {
		LOGGER.info("VideoServiceImpl : getVideoDisclaimer() : START");
		String videoDisclaimer = disclaimerPathStr+"/jcr:content/renditions/original/jcr:content";
		ResourceResolver resourceResolver = null;
		String disclaimerText = null;
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(videoDisclaimer);
			if(res != null){
				Node disclaimerNode = res.adaptTo(Node.class);
				InputStream inputStream = disclaimerNode.getProperty("jcr:data").getBinary().getStream();
				StringWriter writer = new StringWriter();
				IOUtils.copy(inputStream, writer, "UTF-8");
				disclaimerText = writer.toString();
				LOGGER.info("disclaimer from Content Fragment : "+ disclaimerText);
				return disclaimerText;
			}   
		}catch (LoginException | RepositoryException | IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("VideoServiceImpl : getVideodisclaimer() :Exception, {}",e.getMessage(), e);
		}
		finally{
			resourceResolver.close();
			LOGGER.info("VideoServiceImpl : getVideodisclaimer(): End" + videoDisclaimer);			
		}
		return null;
	}
	
	/**
	 * @param videoUrl
	 * @return Video description
	 */
	@Override
	public String getVideoDescription(String videoUrl) {
		LOGGER.info("VideoServiceImpl : getVideoDescription() : START");	
		ResourceResolver resourceResolver = null;
		String videoDescription = null;
		String fullUrl = null;
		fullUrl = videoUrl+"/jcr:content/metadata";
		
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(fullUrl);
			if(res != null){
				ValueMap properties = res.adaptTo(ValueMap.class);
				videoDescription = properties.get("dc:description", String.class);
				
				return videoDescription;
			}
		}catch (LoginException | RepositoryException e) {
			LOGGER.error("VideoServiceImpl : getVideoDescription() :Exception, {}",e.getMessage(), e);
		}
		finally{
			resourceResolver.close();
			LOGGER.info("VideoServiceImpl : getVideoDescription() : END" + videoDescription);			
		}
		return videoDescription;
	}
	
	/**
	 * @param videoUrl
	 * @return Video title
	 */
	@Override
	public String getVideoTitle(String videoUrl) {
		LOGGER.info("VideoServiceImpl : getVideoTitle() : START");	
		ResourceResolver resourceResolver = null;
		String videoTitle = null;
		String fullUrl = null;
		fullUrl = videoUrl+"/jcr:content/metadata";
		
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(fullUrl);
			if(res != null){
				ValueMap properties = res.adaptTo(ValueMap.class);
				videoTitle = properties.get("dc:title", String.class);
				return videoTitle;
			}
		} catch (LoginException | RepositoryException e) {
			LOGGER.error("VideoServiceImpl : getVideoTitle() : Exception{}:",e.getMessage(), e);
		}
		finally{
			resourceResolver.close();
			LOGGER.info("VideoServiceImpl : getVideoTitle() : ENDS" + videoTitle);			
		}
		return videoTitle;
	}
	
	/**
	 * Sets the Video Disclaimer
	 */
	
	@Override
	public void setVideoDisclaimer(String assetData, String currentNodePath) {
		LOGGER.info("VideoServiceImpl : setVideoDisclaimer() : START");	
		ResourceResolver resourceResolver = null;
		String nodeAssetData = null;
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(currentNodePath);
			if(res != null){
				Node currentNode = res.adaptTo(Node.class);
				if(currentNode.hasProperty("node_asset_data_disclaimer")){
					nodeAssetData = currentNode.getProperty("node_asset_data_disclaimer").getString();
				}		
				currentNode.setProperty("node_asset_data_disclaimer", assetData);
				if(nodeAssetData==null){
					nodeAssetData = currentNode.getProperty("node_asset_data_disclaimer").getString();
				}
				if(!currentNode.hasProperty("disclaimer")){
					currentNode.setProperty("disclaimer", assetData);
					resourceResolver.commit();
				}else if(assetData != null && nodeAssetData != null && currentNode.hasProperty("disclaimer")){
					if(!assetData.equals(nodeAssetData)){
						currentNode.setProperty("disclaimer", assetData);
						resourceResolver.commit();
					}
				}				
			}
		} catch (LoginException | RepositoryException | PersistenceException e) {
			LOGGER.error("VideoServiceImpl : setVideoDisclaimer() :Exception, {}",e.getMessage(), e);
		}
		finally{
			resourceResolver.close();
			LOGGER.info("VideoServiceImpl : setVideoDisclaimer() : END" + assetData);
		}		
	}

	/**
	 * Sets the Video Description
	 */
	@Override
	public void setVideoDescription(String assetData, String currentNodePath) {		
		LOGGER.info("VideoServiceImpl : setVideoDescription() CurrentNodePath : START"+currentNodePath);	
		ResourceResolver resourceResolver = null;		
		String nodeAssetData = null;
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(currentNodePath);
			if(res != null){
				Node currentNode = res.adaptTo(Node.class);
				if(currentNode.hasProperty("node_asset_data_description")){
					nodeAssetData = currentNode.getProperty("node_asset_data_description").getString();
				}		
				currentNode.setProperty("node_asset_data_description", assetData);	
				if(nodeAssetData==null){
					nodeAssetData = currentNode.getProperty("node_asset_data_description").getString();
				} 
				if(!currentNode.hasProperty("description")){
					currentNode.setProperty("description", assetData);
					resourceResolver.commit();
				}else if(assetData != null && nodeAssetData != null && currentNode.hasProperty("description")){
					if(!assetData.equals(nodeAssetData)){
						currentNode.setProperty("description", assetData);
						resourceResolver.commit();
					}
				}				
			}else{
				LOGGER.error("VideoServiceImpl : setVideoDescription() : Resource is Null()");
			}
		}catch (LoginException | RepositoryException | PersistenceException e) {
			LOGGER.error("VideoServiceImpl : setVideoDescription() : Exception, {}",e.getMessage(), e);
			}
		finally{
			resourceResolver.close();
			LOGGER.info("VideoServiceImpl : setVideoDescription() : END" + assetData);
		}		
	}  

	/**
	 * Sets the Video Title
	 */
	@Override
	public void setVideoTitle(String assetData, String currentNodePath) {		
		LOGGER.info("VideoServiceImpl : setVideoTitle() : START");	
		ResourceResolver resourceResolver = null;		
		String nodeAssetData = null;
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(currentNodePath);
			if(res != null){
				Node currentNode = res.adaptTo(Node.class);
				if(currentNode.hasProperty("node_asset_data_title")){
					nodeAssetData = currentNode.getProperty("node_asset_data_title").getString();
				}		
				currentNode.setProperty("node_asset_data_title", assetData);	
				if(nodeAssetData==null){
					nodeAssetData = currentNode.getProperty("node_asset_data_title").getString();
				}
				if(!currentNode.hasProperty("title")){
					currentNode.setProperty("title", assetData);
					resourceResolver.commit();
				}else if(assetData != null && nodeAssetData != null && currentNode.hasProperty("title")){
					if(!assetData.equals(nodeAssetData)){
						currentNode.setProperty("title", assetData);
						resourceResolver.commit();
					}
				}				
			}
		}catch (LoginException | RepositoryException | PersistenceException e) {
			LOGGER.error("VideoServiceImpl : setVideoTitle() : Exception, {}",e.getMessage(), e);
		}
		finally{
			resourceResolver.close();
			LOGGER.info("VideoServiceImpl : setVideoTitle() : Ends" + assetData);
		}		
	}
	
	/**
	 * Sets the Video Transcript
	 */
	@Override
	public void setVideoTranscript(String assetData, String currentNodePath) {		
		LOGGER.info("VideoServiceImpl : setVideoTranscript() : START");	
		ResourceResolver resourceResolver = null;		
		String nodeAssetData = null;
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(currentNodePath);
			if(res != null){
				Node currentNode = res.adaptTo(Node.class);
				if(currentNode.hasProperty("node_asset_data_transcript")){
					nodeAssetData = currentNode.getProperty("node_asset_data_transcript").getString();
				}		
				currentNode.setProperty("node_asset_data_transcript", assetData);	
				if(nodeAssetData==null && currentNode.hasProperty("node_asset_data_transcript")){
					nodeAssetData = currentNode.getProperty("node_asset_data_transcript").getString();
				}
				if(!currentNode.hasProperty("transcript")){
					currentNode.setProperty("transcript", assetData);
					resourceResolver.commit();
				}else if(assetData != null && nodeAssetData != null && currentNode.hasProperty("transcript")){
					if(!assetData.equals(nodeAssetData)){
						currentNode.setProperty("transcript", assetData);
						resourceResolver.commit();
					}
				}				
			}
		}catch (LoginException | RepositoryException | PersistenceException e) {
			LOGGER.error("VideoServiceImpl : setVideoTranscript() : Exception, {}",e.getMessage(), e);
		}
		finally{
			resourceResolver.close();
			LOGGER.info("VideoServiceImpl : setVideoTranscript() : Ends :" + assetData);
		}		
	}
	
}
