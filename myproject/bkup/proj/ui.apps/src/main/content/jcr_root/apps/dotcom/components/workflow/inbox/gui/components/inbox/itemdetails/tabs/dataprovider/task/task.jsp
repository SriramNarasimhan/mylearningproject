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
                  java.util.HashMap,
                  com.adobe.granite.taskmanagement.TaskManager,
                  com.adobe.granite.taskmanagement.Task,
                  org.apache.commons.lang.StringUtils, java.util.Iterator, com.adobe.granite.taskmanagement.TaskAction, java.util.List, java.util.ArrayList" %><%

    Config cfg = cmp.getConfig();

    String itemId = request.getParameter("item");
    if (StringUtils.isBlank(itemId)) {
        // no item specified -> fail fast.
        return;
    }

    TaskManager taskManager = resourceResolver.adaptTo(TaskManager.class);
    try {
        Task task = taskManager.getTask(itemId);

        ValueMapDecorator vm = new ValueMapDecorator(new HashMap<String, Object>());

        Iterator<String> propertyNamesIterator = task.getPropertyNames();
        if (propertyNamesIterator != null) {
            while (propertyNamesIterator.hasNext()) {
                String propertyName = propertyNamesIterator.next();
                Object propertyValue = task.getProperty(propertyName);
                vm.put(propertyName, propertyValue);
            }
        }

        vm.put("id", task.getId());
        vm.put("parentId", task.getParentId());
        vm.put("name", task.getName());
        vm.put("assignee", task.getCurrentAssignee());
        vm.put("contentPath", task.getContentPath());
        vm.put("description", task.getDescription());
        vm.put("status", task.getStatus());
        vm.put("tasktype", task.getTaskTypeName());
        vm.put("instructions", task.getInstructions());

        vm.put("taskDueDate", getFormattedDateString(task.getDueTime()));
        vm.put("taskStartDate", getFormattedDateString(task.getProgressBeginTime()));
        vm.put("taskPriority", task.getPriority().getPriorityValue());

        vm.put("startTime", task.getTimeStarted());
        vm.put("createdBy", task.getCreatedBy());
        vm.put("lastModified", task.getLastModified());
        vm.put("lastModifiedBy", task.getLastModifiedBy());
        vm.put("endTime", task.getTimeEnded());
        vm.put("completedBy", task.getCompletedBy());
        List<TaskAction> taskActionsList = task.getActions();
        if (taskActionsList != null) {
            List<String> actionNames = new ArrayList<String>();
            for(TaskAction action : taskActionsList) {
                actionNames.add(action.getActionID());
            }
            vm.put("actionNames", actionNames.toArray());
        }
        vm.put("selectedAction", task.getSelectedAction());

        request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, vm);

%><section  class="coral-TabPanel-pane content-container taskdetails-tab task-tab" data-tabid="f84cc8d6-60ab-474e-bdf8-a0b7a434f289">
    <sling:include resource="<%= resource %>" resourceType="granite/ui/components/foundation/container"/>
</section><%
    } finally {
        request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, null);
    }
%>
<%!
    private String getFormattedDateString(Date date) {
        String result = null;
        if (date != null) {
            SimpleDateFormat sdfiso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            result = sdfiso.format(date);
        }
        return result;
    }
    private Calendar getDueDate(Project project) {
        Resource jcrContent = project.adaptTo(Resource.class).getChild("jcr:content");
        if (jcrContent != null) {
            return jcrContent.getValueMap().get("project.dueDate", Calendar.class);
        }
        return null;
    }
%>