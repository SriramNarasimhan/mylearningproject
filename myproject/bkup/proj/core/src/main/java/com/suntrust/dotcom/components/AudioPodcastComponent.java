package com.suntrust.dotcom.components;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.dam.api.Asset;
import com.suntrust.dotcom.services.AudioService;


@Component 
@Service
/**
 * This AudioPodcastComponent is used to fetch the Audio files details
 * @author Cognizant
 * @version 1.0
 * @since 10 July 2017 
 */
public class AudioPodcastComponent  extends WCMUsePojo {
	/**
	 * This Logger is used log the Audio component values
	*/	
	private static final Logger LOGGER = LoggerFactory.getLogger(AudioPodcastComponent.class);	
	/**
	 * This service used to get the AudioTitle,
	 * AudioTranscript,AudioThumbnail,
	 * AudioDisclaimer,AudioDescription 
	 */
	private AudioService audioService = null;	
	/**
	 * This is used to retrieve audioTranscript values
	 */
	private String audioTranscript = null;
	/**
	 * This is used to retrieve AudioTitle values
	 */
	private String audioTitle = null;
	/**
	 * This is used to retrieve AudioThumbnail values
	 */
	private String audioThumbnail = null;
	/**
	 * This is used to retrieve audioUrl values
	 */
	private String audioUrl = null;	
	/**
	 * This is used to retrieve AudioDescription values
	 */
	private String audioDescription = null;		
	/**
	 * This is used to read the childProperty values
	 */
	private ValueMap childProperties;
	/**
	 * This is used to retrieve audioDuration values
	 */
	private String audioDuration = "";	
	
	   
	/**
	 * @return audioUrl
	 */
	public String getAudioUrl() {
		return audioUrl;
	}

	/**
	 * @param audioUrl
	 */
	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}
	
	@Override  
	/**
	 * Gets called on bundle activate.
	 * 
	 * @throws RepositoryException
	 */
	public void activate() {
		LOGGER.info("AudioPodcastComponent : activate() : Start");
	
		/**
		 * This is used to retrieve currentnode values
		 */
		Node currentNode = null;
		/**
		 * This is used to retrieve currentNodePath values
		 */
		String currentNodePath = null;
		
		/**
		 * This is used to assign the path for disclaimer text
		 */
		String disclaimerPath = "/content/dam/stcom/documents/disclaimer/disclaimer.txt";
		
		/**
		 * This is used to retrieve disclaimerText values
		 */
		String disclaimerText = null;
		
		currentNode = get("currentnode", Node.class);
		//LOGGER.info("current Node path "+  currentNode.getPath());
		try {
			currentNodePath = currentNode.getPath();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			LOGGER.error("current Node path "+  e.getMessage(), e);
		}
		
		Resource childResource = getResourceResolver().getResource(currentNodePath);				
		if(childResource != null){
		childProperties = childResource.adaptTo(ValueMap.class);		
		audioUrl = (String) childProperties.get("fileReference");
		setAudioUrl(audioUrl);
		}
		LOGGER.info("AudioPodcastComponent : activate() : audioUrl"+ audioUrl);
		Resource audioResource = getResourceResolver().getResource(audioUrl); 
		
        Asset asset = audioResource.adaptTo(Asset.class);
        if(asset != null && audioUrl.contains(".mp3")) {           
        	setAudioDuration(asset.getMetadataValue("dam:Length"));
		}
	
		if(audioUrl != null){
			audioTranscript = getAudioTranscript(audioUrl);
			setAudioTranscript(audioTranscript, currentNodePath);
			audioDescription = getAudioDescription(audioUrl);
			setAudioDescription(audioDescription, currentNodePath);
			audioTitle = getAudioTitle(audioUrl);			
			setAudioTitle(audioTitle, currentNodePath);
			audioThumbnail = getAudioThumbnail(audioUrl);
			LOGGER.info("AudioPodcastComponent : activate() audioThumbnail" + audioThumbnail);			
		}
		
		disclaimerText = getAudioDisclaimer(disclaimerPath);
		LOGGER.info("AudioPodcastComponent : activate() disclaimerText" + disclaimerText);
		setAudioDisclaimer(disclaimerText, currentNodePath);		
	}

	/**
	 * @param audioDuration
	 */
	public void setAudioDuration(String audioDuration){                       
	    this.audioDuration=audioDuration;
	}
	/**
	 * @return audioDuration
	 */
	public String getAudioDuration(){                             
	    return this.audioDuration;
	}
	
	/**
	 * @return audioThumbnail
	 */
	public String getThumbnailImage(){		
		return this.audioThumbnail;
	}
	
	
	/**
	 * @param audioUrl
	 * @return audioTranscript
	 */
	public String getAudioTranscript(String audioUrl) {
		audioService = getSlingScriptHelper().getService(AudioService.class);
		audioTranscript = audioService.getAudioTranscript(audioUrl);
		return audioTranscript;
	} 
	
	
	/**
	 * @param audioUrl
	 * @param currentNodePath
	 */
	public void setAudioTranscript(String audioUrl, String currentNodePath) {
		audioService = getSlingScriptHelper().getService(AudioService.class);
		audioService.setAudioTranscript(audioTranscript, currentNodePath);
	}

	
	/**
	 * @param disclaimerUrl
	 * @return audioDisclaimer
	 */
	public String getAudioDisclaimer(String disclaimerUrl){
		/**
		 * This is used to retrieve AudioDisclaimer values
		 */
		String audioDisclaimer = null; 
		audioService = getSlingScriptHelper().getService(AudioService.class);
		audioDisclaimer = audioService.getAudioDisclaimer(disclaimerUrl);
		return audioDisclaimer;
	}
	 
	
	/**
	 * @param audioUrl
	 * @return audioDescription
	 */
	public String getAudioDescription(String audioUrl){		
		audioService = getSlingScriptHelper().getService(AudioService.class);
		audioDescription = audioService.getAudioDescription(audioUrl);
		return audioDescription;
	}
	
	/**
	 * @param audioUrl
	 * @return audioTitle
	 */
	public String getAudioTitle(String audioUrl){
		audioService = getSlingScriptHelper().getService(AudioService.class);
		audioTitle = audioService.getAudioTitle(audioUrl);
		return audioTitle;
	}
	
	/**
	 * @param audioUrl
	 * @return audioThumbnail
	 */
	public String getAudioThumbnail(String audioUrl){
		audioService = getSlingScriptHelper().getService(AudioService.class);
		audioThumbnail = audioService.getAudioThumbnail(audioUrl);
		return audioThumbnail;
	}
	
	/**
	 * @param audioDisclaimer
	 * @param currentNodePath
	 */
	public void setAudioDisclaimer(String audioDisclaimer, String currentNodePath){
		audioService = getSlingScriptHelper().getService(AudioService.class);
		audioService.setAudioDisclaimer(audioDisclaimer, currentNodePath);	
	}
	
	/**
	 * @param audioDescription
	 * @param currentNodePath
	 */
	public void setAudioDescription(String audioDescription, String currentNodePath){
		audioService = getSlingScriptHelper().getService(AudioService.class);
		audioService.setAudioDescription(audioDescription, currentNodePath);
	}
	
	/**
	 * @param audioTitle
	 * @param currentNodePath
	 */
	public void setAudioTitle(String audioTitle, String currentNodePath){
		audioService = getSlingScriptHelper().getService(AudioService.class);
		audioService.setAudioTitle(audioTitle, currentNodePath);
	}	
	
}