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
%><%@page session="false" contentType="text/html; charset=utf-8" %><%
%><%@page import="com.adobe.cq.projects.api.Project,
                  com.adobe.granite.taskmanagement.Task,
                  com.adobe.granite.taskmanagement.TaskManager,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceUtil,
                  javax.jcr.Node,
                  org.apache.jackrabbit.JcrConstants,
                  org.apache.commons.lang.StringUtils" %><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0" %><%
%><cq:defineObjects /><%

    String projectPath = null;
    if (request.getParameter("item") != null) {
        projectPath = request.getParameter("item");
    } else {
        projectPath = slingRequest.getRequestPathInfo().getSuffix();
    }

    if (projectPath == null) {
        return;
    }

    Resource contentRes = resource.getResourceResolver().resolve(projectPath);

    Project project = null;
    try {
        project = contentRes.adaptTo(Project.class);
    } catch(NullPointerException e) {
        log.debug("Nullpointer when attempting to convert a resource to a project", e);
    }

    String assetPath = "";
    Resource assetFolder = null;
    if ( project != null ) {
        assetPath = project.getAssetFolder().getPath();
    } else {
        // might be a task ...
        Resource projectResource = findProjectResource(contentRes);
        if ( projectResource != null ) {
            project = projectResource.adaptTo(Project.class);
            String tasksPath = projectResource.getPath() + "/" + JcrConstants.JCR_CONTENT+ "/tasks";
            Resource tasksRoot = resource.getResourceResolver().getResource(tasksPath);
            if ( tasksRoot != null ) {
                TaskManager tm = tasksRoot.adaptTo(TaskManager.class);
                Task task = tm.getTask(projectPath);

                if (StringUtils.isNotBlank(task.getContentPath())) {
                    assetFolder = resource.getResourceResolver().getResource(task.getContentPath());
                }

                if (assetFolder != null) {
                    Node node = assetFolder.adaptTo(Node.class);
                    if (node != null && !node.isNodeType("nt:folder") && !node.getPrimaryNodeType().getName().equals("nt:unstructured")) {
                        assetFolder = assetFolder.getParent();
                    }
                }
            }
            assetPath = (assetFolder == null) ? project.getAssetFolder().getPath() : assetFolder.getPath();
        }
    }

    if (assetPath == null) {
        return;
    }

%>

<div class="foundation-content-path" data-foundation-content-type="folder" data-foundation-content-path="<%= xssAPI.encodeForHTMLAttr(assetPath) %>">
</div><%
%><%!
    private Resource findProjectResource(Resource contentResource) {
        if (contentResource == null || ResourceUtil.isNonExistingResource(contentResource)) {
            return null;
        }
        if (contentResource.isResourceType("cq/gui/components/projects/admin/card/projectcard")) {
            return contentResource;
        }
        return findProjectResource(contentResource.getParent());
    }
%>
