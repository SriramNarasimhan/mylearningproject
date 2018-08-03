package com.suntrust.dotcom.services;

import java.util.ArrayList;

import org.apache.sling.api.resource.ResourceResolver;

public interface RewriteMapService {


	public void getRewriteMap(ResourceResolver resolver, String path, String payLoadPath, boolean isUnPubWf);
    public String[] getMultiPaths();
    public String getDestinationPath();
    public ArrayList<String> getfullResourcePaths();

}
