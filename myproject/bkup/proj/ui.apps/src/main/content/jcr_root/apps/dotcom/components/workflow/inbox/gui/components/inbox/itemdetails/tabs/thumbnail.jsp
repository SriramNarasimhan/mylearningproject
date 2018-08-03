<%--

  ADOBE CONFIDENTIAL
  __________________

   Copyright 2017 Adobe Systems Incorporated
   All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@page session="false"
          pageEncoding="utf-8"
          contentType="text/html"
          import="org.apache.sling.api.resource.Resource,
                  org.apache.commons.lang.StringUtils,
                  org.apache.sling.api.resource.ResourceResolver" %><%

%><%!
    String getThumbnailUrlForPath(ResourceResolver resourceResolver, String payloadPath) {
        if (StringUtils.isNotBlank(payloadPath)) {
            Resource payloadResource = resourceResolver.getResource(payloadPath);
            if (payloadResource != null) {
                if (isPayloadFolder(payloadResource)) {
                    return "/libs/cq/inbox/content/inbox/images/folder319.png";
                } else {
                    return payloadPath + ".thumb.319.319.jpg";
                }
            }
        }
        return null;
    }

    private boolean isPayloadFolder(Resource payloadRes) {
        if (payloadRes != null) {
            return (payloadRes.isResourceType("sling:Folder")
                    || payloadRes.isResourceType("sling:OrderedFolder")
                    || payloadRes.isResourceType("nt:folder"));
        }
        return false;
    }

%>
