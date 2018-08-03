package com.suntrust.dotcom.services;
/**
 * This VideoService is used to fetch the Video files details
 * @author Cognizant
 * @version 1.0
 * @since 10 June 2017 
 */
public interface VideoService { 
	/**
	 * @param videoUrl
	 * @return
	 */
	String getVideoTranscript(String videoUrl);
	/**
	 * @param videoUrl
	 * @return
	 */
	String getVideoTitle(String videoUrl);
	/**
	 * @param disclaimerUrl
	 * @return
	 */
	String getVideoDisclaimer(String disclaimerUrl);
	/**
	 * @param videoUrl
	 * @return
	 */
	String getVideoDescription(String videoUrl);
	/**
	 * @param videoTranscript
	 * @param currentNodePath
	 */
	void setVideoTranscript(String videoTranscript, String currentNodePath);
	/**
	 * @param videoTitle
	 * @param currentNodePath
	 */
	void setVideoTitle(String videoTitle, String currentNodePath);
	/**
	 * @param videoDisclaimer
	 * @param currentNodePath
	 */
	void setVideoDisclaimer(String videoDisclaimer, String currentNodePath);
	/**
	 * @param videoDescription
	 * @param currentNodePath
	 */
	void setVideoDescription(String videoDescription, String currentNodePath);
	 
}
 