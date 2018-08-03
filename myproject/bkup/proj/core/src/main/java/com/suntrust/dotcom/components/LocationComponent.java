package com.suntrust.dotcom.components;

import java.util.ArrayList;
import java.util.List;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;

import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocationComponent extends WCMUsePojo {
	Session session = null;
	protected static Logger log = LoggerFactory
			.getLogger(LocationComponent.class);	
	ArrayList<String>tagTitles = new ArrayList<String>();
	ArrayList<String>tagIds = new ArrayList<String>();
	String tagId=null;
	String tagName=null;
	String stateUrl=null;
	String cityUrl=null;
	@Override
	public void activate() {		
		session = this.getResourceResolver().adaptTo(Session.class);
		String fullUrl = getResourcePage().getPath();
		stateUrl=getResourcePage().getParent(3).getPath().toString();
		cityUrl=getResourcePage().getParent(2).getPath().toString();
		Resource resource = getResourceResolver().getResource(
				fullUrl + "/jcr:content");
		tagName = get("tagName", String.class);
		TagManager tagManager = this.getResourceResolver().adaptTo(
				TagManager.class);
		try {
			if (resource != null) {
				ValueMap properties = resource.adaptTo(ValueMap.class);
				String[] tags = properties.get(tagName, new String[] {});
				for (String eachTag : tags) {					
					Tag tmp = tagManager.resolve(eachTag);
					tagTitles.add(tmp.getTitle());
					tagIds.add(tmp.getLocalTagID());					
				}
			}
		} catch (Exception e) {
			log.error("Exception, {}", e.getMessage());
		}
	}
	public int getTagsLength() {
		return tagTitles.size();		
	}
	public List <String> getTagsTitle() {		
		return tagTitles;
	}
	public List <String> getTagsId() {		
		return tagIds;
	}
	public String getTagId() {		
		return tagIds.get(0);
	}
	public String getStateUrl() {
		return stateUrl;	
	}
	public String getCityUrl() {
		return cityUrl;	
	}
	public String getTagTitle() {		
		return tagTitles.get(0);
	}
	public String getTagIdToUpperCase() {		
		return tagIds.get(0).toUpperCase();
	}
	public String getRootTagId() {		
		if(tagIds.get(0).contains("/")){
			return tagIds.get(0).split("/")[0];
		}else{
			return tagIds.get(0);
		}		
	}
}
