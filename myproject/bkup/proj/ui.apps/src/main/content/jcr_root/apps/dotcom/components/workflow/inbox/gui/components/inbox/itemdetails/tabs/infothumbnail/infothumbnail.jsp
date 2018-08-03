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
%><%@page session="false"
          import="com.adobe.granite.ui.components.Tag,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.AttrBuilder,
                  org.apache.commons.lang.StringUtils,
                  org.apache.jackrabbit.util.Text,
                  com.adobe.cq.projects.api.Project,
                  org.apache.sling.api.resource.Resource"%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%

    Project project = (Project) request.getAttribute("cq.inbox.projectinfo");
    if (project == null) {
        return;
    }
    Resource projectResource = project.adaptTo(Resource.class);
    if (projectResource == null) {
        return;
    }
    String projectPath = projectResource.getPath();

    Tag tag = cmp.consumeTag();

    AttrBuilder attrs = tag.getAttrs();

    attrs.addClass("cq-inbox-details--infoimage");

    String xssTitle = "";
    String xssDescription = "";
    String thumbnailUrl = "";
    if (cmp.getValue() != null) {
        if (StringUtils.isNotBlank(projectPath)) {
            thumbnailUrl = request.getContextPath() + Text.escapePath(projectPath) + ".thumb.319.319.jpg";
        }
        xssDescription = xssAPI.encodeForHTML(cmp.getValue().get("jcr:description", ""));
        xssTitle = xssAPI.encodeForHTML(cmp.getValue().get("jcr:title", ""));
    }
%>

<div class="foundation-layout-thumbnail">
    <coral-card <%= attrs %>>
        <coral-card-asset>
            <img src="<%= xssAPI.getValidHref(thumbnailUrl) %>"/>
        </coral-card-asset><%
        if (StringUtils.isNotBlank(xssTitle) && StringUtils.isNotBlank(xssDescription)) {
            %><coral-card-content>
                <coral-card-title><%= i18n.get(xssTitle) %></coral-card-title>
                <coral-card-propertylist>
                    <coral-card-property title="<%= i18n.get("Description") %>">
                        <%= StringUtils.isNotEmpty(xssDescription) ? i18n.get(xssDescription) : "&nbsp;" %>
                    </coral-card-property>
                </coral-card-propertylist>
            </coral-card-content><%
        }
%></coral-card>
</div>
