package com.suntrust.dotcom.services;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import aQute.bnd.annotation.ProviderType;

@ProviderType
public interface RequesterInboxItem {
	
	public abstract String getTitle();
	  
	  public abstract String getDescription();
	  
	  public abstract String getPath();
	  
	  public abstract String getResourceType();
	  
	  public abstract Map<String, String> getCurrentAssigneeInfo();
	  
	  public abstract String getPayloadLink();
	  
	  public abstract String getPayloadThumbnail();
	  
	  public abstract boolean isPayloadFolder();
	  
	  public abstract String getThumbnail();
	  
	  public abstract String getPriority();
	  
	  public abstract String getPriorityIcon();
	  
	  public abstract String getPriorityStyle();
	  
	  public abstract String getStatus();
	  
	  public abstract String getStatusIcon();
	  
	  public abstract String getStatusType();
	  
	  public abstract String getDetailsUrl();
	  
	  public abstract String getProjectId();
	  
	  public abstract String getProjectTitle();
	  
	  public abstract String getAssociatedProjectDetailsUrl();
	  
	  public abstract String getWorkflowTitle();
	  
	  public abstract String getWorkflowInitiator();
	  
	  public abstract Date getStartDate();
	  
	  public abstract Date getDueDate();
	  
	  public abstract String getDueDateColor();
	  
	  public abstract Set<String> getQuickactionsRel();
	  
	  public abstract String getTaskActions();

}
