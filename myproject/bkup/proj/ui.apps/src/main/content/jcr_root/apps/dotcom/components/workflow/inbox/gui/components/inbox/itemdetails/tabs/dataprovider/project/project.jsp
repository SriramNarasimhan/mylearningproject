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
                  java.util.HashMap" %><%

    Config cfg = cmp.getConfig();
    Project project = (Project) request.getAttribute("cq.inbox.projectinfo");

    if (project == null) {
        return;
    }

    // setup the project info
    // and then switch to granite/ui/components/foundation/section

    try {
        ValueMapDecorator vm = new ValueMapDecorator(new HashMap<String, Object>());

        vm.put("projectTitle", project.getTitle());
        vm.put("jcr:description", project.getDescription());
        vm.put("projectStatus", project.isActive() ? i18n.get("Active") : i18n.get("Inactive"));
        vm.put("project.dueDate", getDueDate(project));

        request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, vm);

%><section  class="coral-TabPanel-pane content-container taskdetails-tab task-tab" data-tabid="f84cc8d6-60ab-474e-bdf8-a0b7a434f289">
    <sling:include resource="<%= resource %>" resourceType="granite/ui/components/foundation/container"/>
</section><%
    } finally {
        request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, null);
    }
%>
<%!
    private Calendar getDueDate(Project project) {
        Resource jcrContent = project.adaptTo(Resource.class).getChild("jcr:content");
        if (jcrContent != null) {
            return jcrContent.getValueMap().get("project.dueDate", Calendar.class);
        }
        return null;
    }
%>
