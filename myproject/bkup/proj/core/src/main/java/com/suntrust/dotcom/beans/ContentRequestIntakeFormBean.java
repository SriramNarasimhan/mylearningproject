/**
 * 
 */
package com.suntrust.dotcom.beans;

/**
 * Purpose - This is a POJO class to hold Content Request Intake Form Data
 * 
 * @author UGRK104
 *
 */
public class ContentRequestIntakeFormBean {

	private String uniqueID;
	private String workflowType;
	private String jobType;
	private String userName;
	private String userEmailID;
	private String userID;
	private String complianceReviewer;
	private String legalReviewer;
	private String pageName;
	private String url;
	private String jobDescription;
	private String suggestedTag;
	private String uploadedAssetPath;
	private String replaceExistingAsset;
	private String assetExpirationDate;
	private String assetComments;
	private String PublishType;
	private String plannedPublishDate;

	public ContentRequestIntakeFormBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ContentRequestIntakeFormBean(String uniqueID, String workflowType,
			String jobType, String userName, String userEmailID, String userID,
			String complianceReviewer, String legalReviewer, String pageName,
			String url, String jobDescription, String suggestedTag,
			String uploadedAssetPath, String replaceExistingAsset,
			String assetExpirationDate, String assetComments,
			String publishType, String plannedPublishDate) {
		super();
		this.uniqueID = uniqueID;
		this.workflowType = workflowType;
		this.jobType = jobType;
		this.userName = userName;
		this.userEmailID = userEmailID;
		this.userID = userID;
		this.complianceReviewer = complianceReviewer;
		this.legalReviewer = legalReviewer;
		this.pageName = pageName;
		this.url = url;
		this.jobDescription = jobDescription;
		this.suggestedTag = suggestedTag;
		this.uploadedAssetPath = uploadedAssetPath;
		this.replaceExistingAsset = replaceExistingAsset;
		this.assetExpirationDate = assetExpirationDate;
		this.assetComments = assetComments;
		PublishType = publishType;
		this.plannedPublishDate = plannedPublishDate;
	}

	public String getUniqueID() {
		return uniqueID;
	}

	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}

	public String getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmailID() {
		return userEmailID;
	}

	public void setUserEmailID(String userEmailID) {
		this.userEmailID = userEmailID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getComplianceReviewer() {
		return complianceReviewer;
	}

	public void setComplianceReviewer(String complianceReviewer) {
		this.complianceReviewer = complianceReviewer;
	}

	public String getLegalReviewer() {
		return legalReviewer;
	}

	public void setLegalReviewer(String legalReviewer) {
		this.legalReviewer = legalReviewer;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getJobDescription() {
		return jobDescription;
	}

	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}

	public String getSuggestedTag() {
		return suggestedTag;
	}

	public void setSuggestedTag(String suggestedTag) {
		this.suggestedTag = suggestedTag;
	}

	public String getUploadedAssetPath() {
		return uploadedAssetPath;
	}

	public void setUploadedAssetPath(String uploadedAssetPath) {
		this.uploadedAssetPath = uploadedAssetPath;
	}

	public String getReplaceExistingAsset() {
		return replaceExistingAsset;
	}

	public void setReplaceExistingAsset(String replaceExistingAsset) {
		this.replaceExistingAsset = replaceExistingAsset;
	}

	public String getAssetExpirationDate() {
		return assetExpirationDate;
	}

	public void setAssetExpirationDate(String assetExpirationDate) {
		this.assetExpirationDate = assetExpirationDate;
	}

	public String getAssetComments() {
		return assetComments;
	}

	public void setAssetComments(String assetComments) {
		this.assetComments = assetComments;
	}

	public String getPublishType() {
		return PublishType;
	}

	public void setPublishType(String publishType) {
		PublishType = publishType;
	}

	public String getPlannedPublishDate() {
		return plannedPublishDate;
	}

	public void setPlannedPublishDate(String plannedPublishDate) {
		this.plannedPublishDate = plannedPublishDate;
	}

}
