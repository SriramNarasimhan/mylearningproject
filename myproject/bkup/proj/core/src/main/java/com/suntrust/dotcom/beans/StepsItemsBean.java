package com.suntrust.dotcom.beans;

/**
 * Bean Class for Step Component
 * @author 
 *
 */
public class StepsItemsBean {
	
	private String stepsImage;
	private String stepsHeading;
	private String stepsDescription;
	private String stepsAlttext;
	private String stepsImageAlttext = "";

	public String getStepsImage() {
		return stepsImage;
	}

	public void setStepsImage(String stepsImage) {
		this.stepsImage = stepsImage;
	}

	public String getStepsHeading() {
		return stepsHeading;
	}

	public void setStepsHeading(String stepsHeading) {
		this.stepsHeading = stepsHeading;
	}

	public String getStepsDescription() {
		return stepsDescription;
	}

	public void setStepsDescription(String stepsDescription) {
		this.stepsDescription = stepsDescription;
	}

	public String getStepsAlttext() {
		return stepsAlttext;
	}

	public void setStepsAlttext(String stepsAlttext) {
		this.stepsAlttext = stepsAlttext;
	}

	public String getStepsImageAlttext() {
		return stepsImageAlttext;
	}

	public void setStepsImageAlttext(String stepsImageAlttext) {
		this.stepsImageAlttext = stepsImageAlttext;
	}

}