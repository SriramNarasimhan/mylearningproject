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
import com.suntrust.dotcom.services.VideoService;

@Component 
@Service
/**
 * This VideoComponent is used to fetch the Video files details
 * @author Cognizant
 * @version 1.0
 * @since 10 June 2017 
 */
public class VideoComponent  extends WCMUsePojo {
	/**
	 * This Logger is used log the Video component values
	*/
	private static final Logger LOGGER = LoggerFactory.getLogger(VideoComponent.class);
	/**
	 * This service used to get the videoTitle,
	 * videoTranscript,videoThumbnail,
	 * videoDisclaimer,videoDescription 
	 */
	private VideoService videoService = null;
	/**
	 * This is used to retrieve videoTranscript values
	 */
	private String videoTranscript = null;
	/**
	 * This is used to retrieve VideoTitle values
	 */
	private String videoTitle = null;
	/**
	 * This is used to retrieve VideooUrl values
	 */
	private String videoUrl = null;
	/**
	 * This is used to retrieve VideoDescription values
	 */
	private String videoDescription = null;	
	/**
	 * This is used to read the childProperty values
	 */	
	private ValueMap childProperties;
	   
	@Override
	/**
	 * Gets called on bundle activate.
	 * 
	 * @throws RepositoryException
	 */
	public void activate() {
		LOGGER.info("VideoComponent : activate() : Start");
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
		
		Resource childResource = getResource().getChild("videoContainer");
		if(childResource != null){
		childProperties = childResource.adaptTo(ValueMap.class);
		videoUrl = (String) childProperties.get("fileReference");
		}
		LOGGER.info("VideoComponent : activate() : VideoURL"+ videoUrl);
		if(videoUrl != null){
			videoTranscript = getVideoTranscript(videoUrl);
			setVideoTranscript(videoTranscript, currentNodePath);
			videoDescription = getVideoDescription(videoUrl);
			setVideoDescription(videoDescription, currentNodePath);
			videoTitle = getVideoTitle(videoUrl);
			setVideoTitle(videoTitle, currentNodePath);
		}
		
		disclaimerText = getVideoDisclaimer(disclaimerPath);
		setVideoDisclaimer(disclaimerText, currentNodePath);		
	}
	
	/**
	 * @param videoUrl
	 * @return videoTranscript
	 */
	public String getVideoTranscript(String videoUrl) {
		videoService = getSlingScriptHelper().getService(VideoService.class);
		videoTranscript = videoService.getVideoTranscript(videoUrl);
		return videoTranscript;
	}
 
	/**
	 * @param videoUrl
	 * @param currentNodePath
	 */
	public void setVideoTranscript(String videoUrl, String currentNodePath) {
		videoService = getSlingScriptHelper().getService(VideoService.class);
		videoService.setVideoTranscript(videoTranscript, currentNodePath);
	}

	/**
	 * @param disclaimerUrl
	 * @return videoDisclaimer
	 */
	public String getVideoDisclaimer(String disclaimerUrl){
		/**
		 * This is used to retrieve videoDisclaimer values
		 */
		String videoDisclaimer = null;
		videoService = getSlingScriptHelper().getService(VideoService.class);
		videoDisclaimer = videoService.getVideoDisclaimer(disclaimerUrl);
		return videoDisclaimer;
	}
	 
	
	/**
	 * @param videoUrl
	 * @return videoDescription
	 */
	public String getVideoDescription(String videoUrl){		
		videoService = getSlingScriptHelper().getService(VideoService.class);
		videoDescription = videoService.getVideoDescription(videoUrl);
		return videoDescription;
	}
	
	/**
	 * @param videoUrl
	 * @return videoTitle
	 */
	public String getVideoTitle(String videoUrl){
		videoService = getSlingScriptHelper().getService(VideoService.class);
		videoTitle = videoService.getVideoTitle(videoUrl);
		return videoTitle;
	}
	
	/**
	 * @param videoDisclaimer
	 * @param currentNodePath
	 */
	public void setVideoDisclaimer(String videoDisclaimer, String currentNodePath){
		videoService = getSlingScriptHelper().getService(VideoService.class);
		videoService.setVideoDisclaimer(videoDisclaimer, currentNodePath);	
	}
	
	/**
	 * @param videoDescription
	 * @param currentNodePath
	 */
	public void setVideoDescription(String videoDescription, String currentNodePath){
		videoService = getSlingScriptHelper().getService(VideoService.class);
		videoService.setVideoDescription(videoDescription, currentNodePath);
	}
	
	/**
	 * @param videoTitle
	 * @param currentNodePath
	 */
	public void setVideoTitle(String videoTitle, String currentNodePath){
		videoService = getSlingScriptHelper().getService(VideoService.class);
		videoService.setVideoTitle(videoTitle, currentNodePath);
	}
}


