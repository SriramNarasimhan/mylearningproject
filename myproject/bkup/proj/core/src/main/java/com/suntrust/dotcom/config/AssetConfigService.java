package com.suntrust.dotcom.config;

public interface AssetConfigService { 
	public abstract String getAssetOutputPath();
	public abstract String[] getEmailCC();
	public abstract String[] getEmailTo();	
	public abstract String[] getMimeType();	
}
