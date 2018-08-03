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

import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.Rendition;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.AudioService;
import com.suntrust.dotcom.services.ServiceAgentService;


@Component(immediate=true, metatype=true, label="Disclaimer Content Path Configuration Service")
@Service
/**
 * This AudioServiceImpl is used to fetch the Audio files details
 * @author Cognizant
 * @version 1.0
 * @since 10 July 2017 
 */
public class AudioServiceImpl implements AudioService{
		 
	@Property(unbounded=PropertyUnbounded.DEFAULT, label="Disclaimer Text", description="Path for disclaimer Text")
	/**
	 * This Logger is used log the Audio component values
	*/
    private static final Logger LOGGER = LoggerFactory.getLogger(AudioServiceImpl.class);
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
	    	disclaimerPathStr = suntrustDotcomService.getPropertyValue("audio.disclaimer.content.path");
	    	LOGGER.info("AudioServiceImpl activate disclaimerPathStr "+disclaimerPathStr);	
        }
	}
	/**
	 * @return serviceAgent
	 * @throws LoginException
	 * @throws RepositoryException
	 */
	public ResourceResolver getResolver() throws LoginException, RepositoryException{
		return serviceAgent.getServiceResourceResolver("dotcomreadservice");		
	}
	
	/**
	 * @param audioUrl
	 * @return audio transcript
	 */
	@Override
	public String getAudioTranscript(String audioUrl) {
		LOGGER.info("AudioServiceImpl : getAudioTranscript() : START");	
		ResourceResolver resourceResolver = null;
		String audioTranscript = null;
		String fullUrl = null;
		fullUrl = audioUrl+"/jcr:content/metadata"; 
		 
		try {		
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(fullUrl);
			if(res != null){
				ValueMap properties = res.adaptTo(ValueMap.class);
				audioTranscript = properties.get("dc:transcript", String.class);
				return audioTranscript;
			}
		} catch (LoginException |RepositoryException e) {
			// TODO Auto-generated catch block
			LOGGER.error("AudioServiceImpl : getAudioTranscript() : Exception{}:",e.getMessage(), e);
		} 		
		finally{
			resourceResolver.close();
			LOGGER.info("AudioServiceImpl : getAudioTranscript() : ENDS :" + audioTranscript);			
		}
		return audioTranscript;
	}
	
	/**
	 * @param disclaimerUrl
	 * @return audio disclaimer
	 */
	@Override
	public String getAudioDisclaimer(String disclaimerUrl) {
		LOGGER.info("AudioServiceImpl : getAudioDisclaimer() : START");
		LOGGER.info("AudioServiceImpl : getAudioDisclaimer()"+disclaimerPathStr);	
		String audioDisclaimer = disclaimerPathStr+"/jcr:content/renditions/original/jcr:content";
		LOGGER.info(" getAudioDisclaimer audioDisclaimer**** : "+ audioDisclaimer);
		ResourceResolver resourceResolver = null;
		String disclaimerText = null;
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");			
			Resource res = resourceResolver.getResource(audioDisclaimer);
			if(res != null){
				Node disclaimerNode = res.adaptTo(Node.class);
				InputStream inputStream = disclaimerNode.getProperty("jcr:data").getBinary().getStream();
				StringWriter writer = new StringWriter();
				IOUtils.copy(inputStream, writer, "UTF-8");
				disclaimerText = writer.toString();
				LOGGER.info(" Audio disclaimer from Content Fragment : "+ disclaimerText);
				return disclaimerText;
			}   
		} catch (LoginException | RepositoryException | IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("AudioServiceImpl : getAudiodisclaimer() :Exception, {}",e.getMessage(), e);
		}  
		finally{
			resourceResolver.close();
			LOGGER.info("AudioServiceImpl : getAudiodisclaimer(): End" + audioDisclaimer);			
		}
		return null;
	}
	
	/**
	 * @param audioUrl
	 * @return audio description
	 */
	@Override
	public String getAudioDescription(String audioUrl) {
		LOGGER.info("AudioServiceImpl : getAudioDescription() : START");	
		ResourceResolver resourceResolver = null;
		String audioDescription = null;
		String fullUrl = null;
		fullUrl = audioUrl+"/jcr:content/metadata";
		
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(fullUrl);
			if(res != null){
				ValueMap properties = res.adaptTo(ValueMap.class);
				audioDescription = properties.get("dc:description", String.class);
				
				return audioDescription;
			}
		} catch (LoginException | RepositoryException e) {
			LOGGER.error("AudioServiceImpl : getAudioDescription() :Exception, {}",e.getMessage(), e);
		} 
		finally{
			resourceResolver.close();
			LOGGER.info("AudioServiceImpl : getAudioDescription() : END" + audioDescription);			
		}
		return audioDescription;
	}
	
	/**
	 * @param audioUrl
	 * @return audio title
	 */
	@Override
	public String getAudioTitle(String audioUrl) {
		LOGGER.info("AudioServiceImpl : getAudioTitle() : START");	
		ResourceResolver resourceResolver = null;
		String audioTitle = null;
		String fullUrl = null;
		fullUrl = audioUrl+"/jcr:content/metadata";		
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(fullUrl);
			if(res != null){
				ValueMap properties = res.adaptTo(ValueMap.class);
				audioTitle = properties.get("dc:title", String.class);				
				return audioTitle;
			}
		} catch (LoginException | RepositoryException e) {
			LOGGER.error("AudioServiceImpl : getAudioTitle() : Exception{}:",e.getMessage(), e);
		} 
		finally{
			resourceResolver.close();
			LOGGER.info("AudioServiceImpl : getAudioTitle() : ENDS" + audioTitle);			
		}
		return audioTitle;
	}
	
	/**
	 * @param audioUrl
	 * @return audio thumbnail
	 */
	@Override
	public String getAudioThumbnail(String audioUrl) {
		LOGGER.info("AudioServiceImpl : getAudioThumbnail() : START");	
		ResourceResolver resourceResolver = null;
		String audioThumbnail = null;
		String fullUrl = null;		
		fullUrl = audioUrl;
		LOGGER.info("AudioServiceImpl : getAudioThumbnail() fullUrl" + fullUrl);
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
			Resource res = resourceResolver.getResource(fullUrl);			
			if(res != null){	   
				Asset asset = res.adaptTo(Asset.class);		
				if(asset != null) {
				    Rendition rendition = asset.getRendition("cq5dam.thumbnail.319.319.png");
				    if(rendition != null)
				    	audioThumbnail= rendition.getPath();	
				    else
				    	audioThumbnail = asset.getPath();
				}
			}
		} catch (LoginException | RepositoryException e) {
			LOGGER.error("AudioServiceImpl : getAudioThumbnail() : Exception{}:",e.getMessage(), e);
		} 
		finally{
			resourceResolver.close();
			LOGGER.info("AudioServiceImpl : getAudioThumbnail() : ENDS" + audioThumbnail);			
		}
		return audioThumbnail;
	}
	
	/**
	 * Sets the Audio Disclaimer
	 */
	@Override
	public void setAudioDisclaimer(String assetData, String currentNodePath) {
		LOGGER.info("AudioServiceImpl : setAudioDisclaimer() : START");	
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
				LOGGER.info("AudioServiceImpl : setAudioDisclaimer() : nodeAssetData**********"+nodeAssetData);
				LOGGER.info("AudioServiceImpl : setAudioDisclaimer() : assetData**********"+assetData);
				if(!currentNode.hasProperty("disclaimer")){
					currentNode.setProperty("disclaimer", assetData);
					resourceResolver.commit();
				}else if(currentNode.hasProperty("disclaimer")){
					if(!assetData.equals(nodeAssetData)){
						LOGGER.info("AudioServiceImpl : setAudioDisclaimer() Entering"+assetData+"**nodeAssetData***"+nodeAssetData);	
						currentNode.setProperty("disclaimer", assetData);
						resourceResolver.commit();
					}
				}				
			}
		} catch (LoginException | RepositoryException | PersistenceException e) {
			LOGGER.error("AudioServiceImpl : setAudioDisclaimer() :Exception, {}",e.getMessage(), e);
		} 
		finally{
			resourceResolver.close();
			LOGGER.info("AudioServiceImpl : setAudioDisclaimer() : END" + assetData);
		}		
	}

	/**
	 * Sets the Audio Description
	 */
	@Override
	public void setAudioDescription(String assetData, String currentNodePath) {		
		LOGGER.info("AudioServiceImpl : setAudioDescription() CurrentNodePath : START"+currentNodePath);	
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
				}else if(currentNode.hasProperty("description")){
					if(!assetData.equals(nodeAssetData)){
						currentNode.setProperty("description", assetData);
						resourceResolver.commit();
					}
				}				
			}else{
				LOGGER.error("AudioServiceImpl : setAudioDescription() : Resource is Null()");
			}
		}catch (LoginException | RepositoryException | PersistenceException e) {
			 LOGGER.error("AudioServiceImpl : setAudioDescription() : Exception, {}",e.getMessage(), e);
			}		
		finally{
			resourceResolver.close();
			LOGGER.info("AudioServiceImpl : setAudioDescription() : END" + assetData);
		}		
	} 
	
	/**
	 * Sets the Audio Title
	 */
	@Override
	public void setAudioTitle(String assetData, String currentNodePath) {		
		LOGGER.info("AudioServiceImpl : setAudioTitle() : START");	
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
				}else if(currentNode.hasProperty("title")){
					if(!assetData.equals(nodeAssetData)){
						currentNode.setProperty("title", assetData);
						resourceResolver.commit();
					}
				}				
			}
		}catch (LoginException | RepositoryException | PersistenceException e) {
			LOGGER.error("AudioServiceImpl : setAudioTitle() : Exception, {}",e.getMessage(), e);
			}
		finally{
			resourceResolver.close();
			LOGGER.info("AudioServiceImpl : setAudioTitle() : Ends" + assetData);
		}		
	}
	
	/**
	 * Sets the Audio Transcript
	 */
	@Override
	public void setAudioTranscript(String assetData, String currentNodePath) {		
		LOGGER.info("AudioServiceImpl : setAudioTranscript() : START");	
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
				if(nodeAssetData==null){
					nodeAssetData = currentNode.getProperty("node_asset_data_transcript").getString();
				}
				if(!currentNode.hasProperty("transcript")){
					currentNode.setProperty("transcript", assetData);
					resourceResolver.commit();
				}else if(currentNode.hasProperty("transcript")){
					if(!assetData.equals(nodeAssetData)){
						currentNode.setProperty("transcript", assetData);
						resourceResolver.commit();
					}
				}				
			}
		}catch (LoginException | RepositoryException | PersistenceException e) {
			LOGGER.error("AudioServiceImpl : setAudioTranscript() : Exception, {}",e.getMessage(), e);
			}
		finally{
			resourceResolver.close();
			LOGGER.info("AudioServiceImpl : setAudioTranscript() : Ends :" + assetData);
		}		
	}
	
}
