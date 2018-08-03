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
%><%@page session="false"%><%
%><%@page import="java.util.Calendar,
                  java.util.Date,
                  java.text.SimpleDateFormat,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ValueMap,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ValueMapResourceWrapper,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.security.user.util.AuthorizableUtil,
                  com.adobe.cq.projects.api.Project,
                  com.adobe.granite.ui.components.Value,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  java.util.HashMap
                  , com.adobe.granite.ui.components.Tag, com.adobe.granite.workflow.exec.Workflow, com.adobe.granite.workflow.model.WorkflowModel, com.adobe.granite.workflow.metadata.MetaDataMap, com.adobe.granite.workflow.exec.WorkflowData, com.adobe.granite.workflow.PayloadMap, org.apache.commons.lang.StringUtils" %><%

    Config cfg = cmp.getConfig();

    ValueMap values = (ValueMap) request.getAttribute(Value.FORM_VALUESS_ATTRIBUTE);
    String name = cfg.get("name", String.class);
    String resourceType = cfg.get("includeResourceType", String.class);
    String value = values.get(name, String.class);

    if ( StringUtils.isNotBlank(value) ) {
        %><sling:include resource="<%= resource %>" resourceType="<%= resourceType %>"/><%
    }
%>
