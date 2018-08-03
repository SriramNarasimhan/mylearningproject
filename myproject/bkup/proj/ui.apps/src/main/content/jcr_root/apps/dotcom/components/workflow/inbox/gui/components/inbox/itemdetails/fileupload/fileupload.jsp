<%--
  ADOBE CONFIDENTIAL

  Copyright 2016 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%%><%@page session="false" contentType="text/html; charset=utf-8"%><%
      %><%@page	import="org.apache.sling.api.resource.Resource,org.apache.sling.api.resource.ResourceUtil,
                        com.day.cq.dam.commons.util.UIHelper,
                        javax.jcr.security.AccessControlManager,
                        javax.jcr.Session,
                        javax.jcr.security.Privilege"%><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>
<cq:defineObjects />
 <%
 /**
 * If the create/modify permissions are there for the currentResource,
 * then include the granite fileupload component, otherwise return;
 */
 AccessControlManager acm = resourceResolver.adaptTo(Session.class).getAccessControlManager();
 String item = null;
 if (request.getParameter("item") != null) {
        item = request.getParameter("item");
        Resource task = resourceResolver.getResource(item);
        if (task!=null && !ResourceUtil.isNonExistingResource(task)){
        String contentPath = ResourceUtil.getValueMap(task).get("contentPath","");
        if (!"".equals(contentPath)){
             Resource currentResource = resourceResolver.getResource(contentPath);
             boolean canUploadAssets = UIHelper.hasPermission(acm, currentResource, Privilege.JCR_ADD_CHILD_NODES)
                  && UIHelper.hasPermission(acm, currentResource, Privilege.JCR_MODIFY_PROPERTIES);
             if (canUploadAssets) {%>
             <cq:include path = "<%=resource.getPath()%>" resourceType="granite/ui/components/foundation/form/fileupload"/>
             <%}
         }
}}%>



