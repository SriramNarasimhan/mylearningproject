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
--%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page session="false"
          import="com.adobe.cq.projects.ui.Breadcrumb,
                  com.adobe.cq.projects.ui.ProjectHelper,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ExpressionHelper,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.adobe.granite.ui.components.ds.SimpleDataSource,
                  com.adobe.granite.ui.components.ds.ValueMapResource,
                  org.apache.commons.lang3.StringUtils,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceMetadata,
                  org.apache.sling.api.resource.ValueMap,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  java.util.ArrayList,
                  java.util.Collections,
                  java.util.HashMap,
                  java.util.List" %>
<%
    Config cfg = cmp.getConfig();
    ExpressionHelper ex = cmp.getExpressionHelper();
    List<Resource> crumbs = new ArrayList<Resource>();

    String title = ex.getString(cfg.get("title", String.class));
    String projectPath = slingRequest.getRequestPathInfo().getSuffix();

    if (StringUtils.isNotBlank(projectPath)) {
        // add the breadcrumb root title
        String rootTitle = i18n.get("Projects");

        ValueMap crumbVM = new ValueMapDecorator(new HashMap<String, Object>());
        crumbVM.put("title", rootTitle);
        crumbVM.put("href", xssAPI.getValidHref(request.getContextPath() + "/projects.html"));
        crumbs.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured", crumbVM));

        // add the owning project title
        Resource projectResource = slingRequest.getResourceResolver().getResource(projectPath);
        String projectTitle = projectResource.getChild("jcr:content").getValueMap().get("jcr:title", String.class);

        ValueMap crumb2VM = new ValueMapDecorator(new HashMap<String, Object>());
        crumb2VM.put("title", i18n.getVar(projectTitle));
        crumb2VM.put("href", xssAPI.getValidHref(request.getContextPath() + "/projects/details.html" + projectPath));
        crumbs.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured", crumb2VM));

        // construct the current inbox title
        title = i18n.get("Inbox - {0}", "0 replaced with project title", projectTitle);
    }

    // add the current title on the details screen.
    if (StringUtils.isNotBlank(title)) {
        ValueMap titleVM = new ValueMapDecorator(new HashMap<String, Object>());
        titleVM.put("title", i18n.getVar(title));
        crumbs.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured", titleVM));
    }

    Collections.reverse(crumbs);
    request.setAttribute(DataSource.class.getName(), new SimpleDataSource(crumbs.iterator()));
%>
