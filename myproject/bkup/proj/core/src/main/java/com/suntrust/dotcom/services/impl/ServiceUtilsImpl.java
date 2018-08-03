/**
 * 
 */
package com.suntrust.dotcom.services.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.suntrust.dotcom.services.ServiceUtils;
import com.suntrust.dotcom.utils.GenericEnum;

/**
 * Service class returning user list for given group
 * 
 * @author UGRK104
 *
 */
@Component(label = "Common Service Utility", description = "Exposes common services", metatype = true, immediate = false, enabled = true)
@Properties({ @Property(label = "Vendor", name = Constants.SERVICE_VENDOR, value = "SunTrust", propertyPrivate = true) })
@Service
public class ServiceUtilsImpl implements ServiceUtils {
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUtilsImpl.class);
	
	
	/**
     * {@inheritDoc}
     */
	public Map<String , String> getAEMGroupUsers(ResourceResolver resourceResolver , String groupName)
	{
		UserManager userManager = null;
		Map<String , String> userListMap = new HashMap<String, String>();
		
		try {		
			LOGGER.debug("Inside getAEMReviewrList method - ");	
			userManager=resourceResolver.adaptTo(UserManager.class);
			Authorizable authorGroupAuthorizable=userManager.getAuthorizable(groupName);
			
			LOGGER.debug("groupName - " + groupName);
			
			if(null!=authorGroupAuthorizable && authorGroupAuthorizable.isGroup()){
				
				Group group=(Group)authorGroupAuthorizable;
				Iterator<Authorizable> groupUsers=group.getMembers();
				
				while(groupUsers.hasNext()) 
				{
					String userName = null;
					
					Authorizable authorizable = groupUsers.next();
					
					String givenName = authorizable.getProperty("./profile/givenName")==null?GenericEnum.EMPTY_STRING.getValue():authorizable.getProperty("./profile/givenName")[0].getString();
					
					String familyName = authorizable.getProperty("./profile/familyName")==null?GenericEnum.EMPTY_STRING.getValue():authorizable.getProperty("./profile/familyName")[0].getString();
					
					if(givenName.isEmpty() && familyName.isEmpty())
					{
						userName = authorizable.getID();
					}
					else
					{
						userName = givenName.trim() + GenericEnum.EMPTY_SPACE_STRING.getValue() + familyName.trim();
					}
					
					userListMap.put(authorizable.getID(), userName);	
				}				
			}
			} catch(RepositoryException exception) {
				LOGGER.error("Exception cought in getAEMReviewrList method : ", exception);
			}
		return userListMap;
	}
	@SuppressWarnings("finally")
	@Override
	public String[] getLoginPageOfProtectedPage(String accessedPath){
 		String pagePath[] = new String[2];
 		pagePath[1] = accessedPath;
 		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
		try {
			ResourceResolver resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);
			Resource resource = resourceResolver.getResource(pagePath[1]);
			if(resource != null && resource.getResourceType().equalsIgnoreCase("cq:Page")){
				InheritanceValueMap iProperties = new HierarchyNodeInheritanceValueMap(resource);
				pagePath[0] = iProperties.getInherited("granite:loginPath",String.class);
				if(StringUtils.isNotBlank(pagePath[0])){
					pagePath[0] = pagePath[0]+".html";
					pagePath[1] = pagePath[1] +".html";
					pagePath[0] = resourceResolver.map(pagePath[0]);
					pagePath[1] = resourceResolver.map(pagePath[1]);
				}
			}else if(resource != null && !resource.getResourceType().equalsIgnoreCase("cq:Page")){
				pagePath[0] = resource.getParent().getValueMap().get("granite:loginPath",String.class);
				if(StringUtils.isNotBlank(pagePath[0])){
					pagePath[0] = pagePath[0]+".html";
					pagePath[0] = resourceResolver.map(pagePath[0]);
					pagePath[1] = pagePath[1];
				}
			}
		} catch (LoginException e) {		
			LOGGER.error("Exception captured in getLoginPageOfProtectedPage. Message: {}, Trace: {}",e.getMessage(), e);
		}
		finally{
			return pagePath;
		}
 	}

}
