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

    String contentPath = ex.getString(cfg.get("contentPath", String.class));

    Resource curResource = resourceResolver.getResource(contentPath);

    final List<Resource> crumbs = new ArrayList<Resource>();

    // create the breadcrumb to return to the Inbox list of items
    String inboxUrl = ex.getString(cfg.get("inboxUrl", String.class));
    String inboxTitle = ex.getString(cfg.get("inboxTitle", String.class));

    ValueMap crumbVM = new ValueMapDecorator(new HashMap<String, Object>());
    crumbVM.put("href", xssAPI.getValidHref(request.getContextPath() + inboxUrl));
    crumbVM.put("title", i18n.get(inboxTitle));
    crumbs.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured", crumbVM));

    // add the current title of the details screen.
    String title = ex.getString(cfg.get("title", String.class));
    if (StringUtils.isNotBlank(title)) {
        ValueMap titleVM = new ValueMapDecorator(new HashMap<String, Object>());
        titleVM.put("title", i18n.getVar(title));
        crumbs.add(new ValueMapResource(resourceResolver, new ResourceMetadata(), "nt:unstructured", titleVM));
    }

    Collections.reverse(crumbs);
    request.setAttribute(DataSource.class.getName(), new SimpleDataSource(crumbs.iterator()));
%>
