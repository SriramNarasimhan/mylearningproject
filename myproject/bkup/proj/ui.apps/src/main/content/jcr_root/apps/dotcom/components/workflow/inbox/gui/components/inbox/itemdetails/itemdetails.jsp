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
%><%@page session="false" %><%
%><%@page import="com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Value,
                  com.adobe.cq.projects.api.Project,
                  org.apache.jackrabbit.util.Text,
                  org.apache.commons.lang3.StringUtils,
                  org.apache.sling.api.resource.Resource" %><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
    Config cfg = new Config(resource);

    AttrBuilder attrs = new AttrBuilder(request, xssAPI);

    attrs.add("id", cfg.get("id", String.class));
    attrs.addClass("foundation-form mode-edit vertical coral-Form--vertical task coral-FixedColumn");
    attrs.add("method", "post");
    attrs.add("action", "/libs/granite/taskmanager/updatetask");

    String xssRedirectUrl = cfg.get("redirect-url", String.class);

    Project project = (Project) request.getAttribute("cq.inbox.projectinfo");
	String projectPath = null;
    if (project != null) {
        Resource projectResource = project.adaptTo(Resource.class);
        if (projectResource != null) {
            projectPath = projectResource.getPath();
            attrs.addOther("project", projectPath);
        }
    }

    if (StringUtils.isNotBlank(xssRedirectUrl)) {
        if (projectPath != null) {
            xssRedirectUrl = xssRedirectUrl + Text.escapePath(projectPath);
        }
            attrs.addOther("redirect", xssRedirectUrl);
    }

    attrs.addOthers(cfg.getProperties(), "id", "redirect");

    String[] items = request.getParameterValues("item");
    try {
        request.setAttribute(Value.CONTENTPATH_ATTRIBUTE, items[0]);
    %><form <%= attrs.build() %>  >
        <sling:include resource="<%= resource %>" resourceType="granite/ui/components/foundation/container"/>
    </form><%
    if (items != null) {
        for (String taskPath : items) {
        %><div class="hidden task-path" data-task-path="<%= xssAPI.encodeForHTMLAttr(taskPath) %>"></div><%
        }
    }
    } finally {
        request.removeAttribute(Value.CONTENTPATH_ATTRIBUTE);
    }
%>
