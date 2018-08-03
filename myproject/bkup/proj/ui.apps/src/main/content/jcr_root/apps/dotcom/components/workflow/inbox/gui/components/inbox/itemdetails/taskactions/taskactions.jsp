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
--%><%@page session="false"
            import="com.adobe.granite.ui.components.Config, com.adobe.granite.ui.components.AttrBuilder"%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><ui:includeClientLib categories="cq.inbox.tasks.completedialog.actions]" /><%
    String[] actionsArray = cmp.getValue().get("actionNames", String[].class);
    Config cfg = new Config(resource);
    String rel = cfg.get("rel", String.class);

    AttrBuilder builder = new AttrBuilder(request, xssAPI);
    builder.addRel(rel);
    builder.addClass("hidden");
    if (actionsArray != null && actionsArray.length > 0) {
    %><div <%=builder.build() %>><%
        if (actionsArray != null && actionsArray.length > 0) {
            for (String taskAction : actionsArray) {
                %><input type="hidden" disabled name="<%= i18n.getVar(taskAction) %>" value="<%= taskAction %>"/><%
            }
        }
    %></div><%
    }
%>
