package com.suntrust.dotcom.services;

/**
 * This AudioService is used to fetch the Audio files details
 * @author Cognizant
 * @version 1.0
 * @since 10 July 2017 
 */

public interface AudioService { 
	/**
	 * @param audioUrl
	 * @return audio transcript
	 */
	String getAudioTranscript(String audioUrl);
	/**
	 * @param audioUrl
	 * @return audio title
	 */
	String getAudioTitle(String audioUrl);
	/**
	 * @param audioUrl
	 * @return audio thumbnail
	 */
	String getAudioThumbnail(String audioUrl);
	/**
	 * @param disclaimerUrl
	 * @return audio disclaimer
	 */
	String getAudioDisclaimer(String disclaimerUrl);
	/**
	 * @param audioUrl
	 * @return audio description
	 */
	String getAudioDescription(String audioUrl);
	/**
	 * @param audioTranscript
	 * @param currentNodePath
	 */
	void setAudioTranscript(String audioTranscript, String currentNodePath);
	/**
	 * @param audioTitle
	 * @param currentNodePath
	 */
	void setAudioTitle(String audioTitle, String currentNodePath);	
	/**
	 * @param audioDisclaimer
	 * @param currentNodePath
	 */
	void setAudioDisclaimer(String audioDisclaimer, String currentNodePath);
	/**
	 * @param audiooDescription
	 * @param currentNodePath
	 */
	void setAudioDescription(String audiooDescription, String currentNodePath);
	 
}
 