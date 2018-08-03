package com.suntrust.dotcom.utils;

import com.adobe.granite.workflow.exec.Status;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InboxItemTypeDefinition {

	public static final String DEFAULT_SUBTYPE_NAME = "default";
	  public static final String KEY_ID = "typeId";
	  public static final String KEY_TYPE = "type";
	  public static final String KEY_SUBTYPE = "subtype";
	  public static final String KEY_URL = "url";
	  public static final String KEY_TITLE = "title";
	  public static final String KEY_DEFINITION_SOURCE_PATH = "definitionSourcePath";
	  public static final String KEY_ACTIONRELS_ACTIVE = "actionRels.ACTIVE";
	  public static final String KEY_ACTIONRELS_COMPLETE = "actionRels.COMPLETE";
	  public static final String KEY_ACTIONRELS_TERMINATED = "actionRels.TERMINATED";
	  private Map<String, Object> properties;
	  
	  public InboxItemTypeDefinition(String type, String subType)
	  {
	    this.properties = new HashMap();
	    
	    this.properties.put("type", type);
	    this.properties.put("subtype", subType);
	    
	    String id = InboxItemsUtils.constructInboxTypeId(type, subType);
	    this.properties.put("typeId", id);
	  }
	  
	  public String getId()
	  {
	    return (String)this.properties.get("typeId");
	  }
	  
	  public String getType()
	  {
	    return (String)this.properties.get("type");
	  }
	  
	  public String getSubType()
	  {
	    return (String)this.properties.get("subtype");
	  }
	  
	  public void setURL(String url)
	  {
	    putIfNotEmpty("url", url);
	  }
	  
	  public String getURL()
	  {
	    return getPropertyAsString("url");
	  }
	  
	  public void setTitle(String title)
	  {
	    putIfNotEmpty("title", title);
	  }
	  
	  public String getTitle()
	  {
	    return getPropertyAsString("title");
	  }
	  
	  public void setTypeDefinitionSourcePath(String sourceLocation)
	  {
	    putIfNotEmpty("definitionSourcePath", sourceLocation);
	  }
	  
	  public String getTypeDefinitionSourcePath()
	  {
	    return getPropertyAsString("definitionSourcePath");
	  }
	  
	  public void setActionRels(String[] actionRels, Status status)
	  {
	    switch (status)
	    {
	    case ACTIVE: 
	      putIfNotEmpty("actionRels.ACTIVE", actionRels);
	      break;
	    case COMPLETE: 
	      putIfNotEmpty("actionRels.COMPLETE", actionRels);
	      break;
	    case TERMINATED: 
	      putIfNotEmpty("actionRels.TERMINATED", actionRels);
	      break;
	    default: 
	      putIfNotEmpty("actionRels.ACTIVE", actionRels);
	    }
	  }
	  
	  public String[] getActionsRels(Status status)
	  {
	    Object result;
	    switch (status)
	    {
	    case ACTIVE: 
	      result = this.properties.get("actionRels.ACTIVE");
	      break;
	    case COMPLETE: 
	      result = this.properties.get("actionRels.COMPLETE");
	      break;
	    case TERMINATED: 
	      result = this.properties.get("actionRels.TERMINATED");
	      break;
	    default: 
	      result = this.properties.get("actionRels.ACTIVE");
	    }
	    if ((result instanceof String[])) {
	      return (String[])result;
	    }
	    return null;
	  }
	  
	  public Map<String, Object> getProperties()
	  {
	    return Collections.unmodifiableMap(this.properties);
	  }
	  
	  private void putIfNotEmpty(String key, Object value)
	  {
	    if (value != null) {
	      this.properties.put(key, value);
	    }
	  }
	  
	  private String getPropertyAsString(String keyUrl)
	  {
	    Object result = this.properties.get(keyUrl);
	    if ((result instanceof String)) {
	      return (String)result;
	    }
	    return null;
	  }
}
