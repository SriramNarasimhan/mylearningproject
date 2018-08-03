package com.suntrust.dotcom.services;

import org.apache.sling.api.resource.ResourceResolver;

import java.util.Map;

/**
 * 
 */
public interface VanityReportService {

    public Map<String, String> getSEOUrlMap(ResourceResolver resolver, String rootpath, String propertyName);

}
