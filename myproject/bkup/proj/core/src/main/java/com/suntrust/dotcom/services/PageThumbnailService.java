package com.suntrust.dotcom.services;

import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

public interface PageThumbnailService {
	String getPageThumbnail(String pagePath) throws RepositoryException;	
	String getPageLink(String pagePath) throws RepositoryException;	
	List<Map<String, Object>> getDetails(String pagePath,String pageParam) throws RepositoryException;
}
