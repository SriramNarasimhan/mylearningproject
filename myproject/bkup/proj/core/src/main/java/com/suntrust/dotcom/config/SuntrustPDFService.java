package com.suntrust.dotcom.config;

public interface SuntrustPDFService {

	public abstract boolean isArchivalEnabled();
	public abstract String getHelperPath();
	public abstract String getPhantomJsPath();
	public abstract String getHelperJSName();
	public abstract String getPdfOutputPath();
	public abstract String getArchivalPath();
	public abstract String getPaperSize();
	public abstract String getJsEval();
	public abstract boolean isPngEnabled();
	public abstract boolean ispdfMetadataEnabled();
	public abstract boolean isDAMUploadEnabled();
	public abstract String getImageDimension();
	public abstract String[] getEmailCC();
	public abstract String[] getEmailTo();
	public abstract String getEnviroment();
	public abstract String getUsername();
	public abstract String getPassword();
}
