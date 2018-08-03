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
%><%@page import="com.adobe.granite.taskmanagement.TaskManager,
                  com.adobe.granite.ui.components.Config,
                  org.apache.commons.lang.StringUtils,
                  com.adobe.granite.taskmanagement.Task,
                  com.adobe.granite.taskmanagement.TaskManagerException,
                  com.adobe.granite.workflow.exec.Workflow,
                  com.adobe.granite.workflow.WorkflowSession,
                  com.adobe.granite.workflow.WorkflowException,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.api.resource.ResourceUtil,
                  com.adobe.cq.projects.api.Project,
                  com.adobe.cq.projects.ui.ProjectConstants, com.adobe.granite.workflow.exec.WorkItem"%><%

    Config cfg = cmp.getConfig();

    String itemId = request.getParameter("item");
    if (StringUtils.isBlank(itemId)) {
        // no item specified -> fail fast.
        return;
    }

    TaskManager taskManager = resourceResolver.adaptTo(TaskManager.class);
    try {
        Task task = taskManager.getTask(itemId);

        request.setAttribute("cq.inbox.inboxitem", task);

        // set the workflow info
        if (task.getProperty("wfInstanceId") instanceof String) {
            String wfInstanceId = (String) task.getProperty("wfInstanceId");
            WorkflowSession wfSession = resourceResolver.adaptTo(WorkflowSession.class);
            if (wfSession != null) {
                try {
                    Workflow workflow = wfSession.getWorkflow(wfInstanceId);
                    if (workflow != null) {
                        request.setAttribute("cq.inbox.workflowinfo", workflow);
                    }
                } catch (WorkflowException e) {
                    log.debug("Failed to load workflow instance for item {} and wf instance id {}", task.getId(), wfInstanceId, e);
                }
            }
        }

        // set the project info
        Project associatedProject = null;
        Resource projectResource = findProject(resourceResolver, task.getId());
        if (projectResource != null) {
            associatedProject = projectResource.adaptTo(Project.class);
            if (associatedProject != null) {
                request.setAttribute("cq.inbox.projectinfo", associatedProject);
            }
        }
    } catch(TaskManagerException te) {
        log.debug("Specified task {} not found, attempting to load workitem ...", itemId, te);

        WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);
        try {
            WorkItem workitem = workflowSession.getWorkItem(itemId);
            request.setAttribute("cq.inbox.inboxitem", workitem);

            Workflow workflow = workitem.getWorkflow();
            if (workflow != null) {
                request.setAttribute("cq.inbox.workflowinfo", workflow);
            }
        } catch (WorkflowException we) {
            log.info("Specified item is neither a workitem nor a task, itemid = {}", itemId, we);
        }
    }
%><%!

    Resource findProject(ResourceResolver resourceResolver, String taskPath) {
        Resource resource = resourceResolver.getResource(taskPath);
        while (resource != null && !ResourceUtil.isNonExistingResource(resource)) {
            if (resource.isResourceType(ProjectConstants.RT_PROJECT_CARD)) {
                return resource;
            }
            resource = resource.getParent();
        }
        return null;
    }
%>




