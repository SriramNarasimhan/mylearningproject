/**
 * 
 */
package com.suntrust.dotcom.services;

import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * @author UGRK104
 *
 */
public interface ServiceUtils {

	/**
	 * Returns user id and name in map for given group 
	 * 
	 * @param resourceResolver
	 * @param groupName
	 * @return
	 */
	public Map<String , String> getAEMGroupUsers(ResourceResolver resourceResolver , String groupName);

	public String[] getLoginPageOfProtectedPage(String pagePath);


}
