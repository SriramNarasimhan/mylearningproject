package com.first.myproject.core;

import java.util.NoSuchElementException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.security.Privilege;

import org.apache.sling.jcr.api.SlingRepository;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ModifyPermissions {
	private static final String CONTENT_GEOMETRIXX_FR = "/content/geometrixx/fr";
	private static final Logger LOGGER=  LoggerFactory.getLogger(ModifyPermissions.class);
	
	@Reference
	private SlingRepository repo;
	
	@Activate
	protected void activate(){
		/*LOGGER.info("ModifyPermissions activated");
		modifyPermissions();*/
	}
	
	private void modifyPermissions() {
		Session adminSession = null;
		try{
			adminSession= repo.loginAdministrative(null);
			
			UserManager userMgr= ((org.apache.jackrabbit.api.JackrabbitSession)adminSession).getUserManager();
			AccessControlManager accessControlManager = adminSession.getAccessControlManager();
			
			Authorizable denyAccess = userMgr.getAuthorizable("deny-access");
			
			AccessControlPolicyIterator policyIterator =
					accessControlManager.getApplicablePolicies(CONTENT_GEOMETRIXX_FR);
			AccessControlList acl;
			try{
				acl=(JackrabbitAccessControlList) policyIterator.nextAccessControlPolicy();				
			}catch(NoSuchElementException nse){
				acl=(JackrabbitAccessControlList)  accessControlManager.getPolicies(CONTENT_GEOMETRIXX_FR)[0];
				
			}
			
			Privilege[] privileges = {accessControlManager.privilegeFromName(Privilege.JCR_READ)};
			acl.addAccessControlEntry(denyAccess.getPrincipal(), privileges);
			accessControlManager.setPolicy(CONTENT_GEOMETRIXX_FR, acl);
			adminSession.save();
		}catch (RepositoryException e){
			LOGGER.error("**************************Repo Exception", e);
		}finally{
			if (adminSession != null)
				adminSession.logout();
		}
	}
	
}
