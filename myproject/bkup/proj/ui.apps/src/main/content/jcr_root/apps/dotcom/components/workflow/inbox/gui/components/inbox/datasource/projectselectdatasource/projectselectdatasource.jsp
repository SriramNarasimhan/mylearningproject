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
%><%@page import="javax.jcr.Session,
                  javax.jcr.security.AccessControlManager,
                  javax.jcr.security.Privilege,
                  java.util.ArrayList,
                  java.util.Arrays,
                  java.util.HashMap,
                  java.util.Iterator,
                  java.util.List,
                  org.apache.commons.collections.Transformer,
                  org.apache.commons.collections.iterators.TransformIterator,
                  org.apache.commons.lang.StringUtils,
                  org.apache.jackrabbit.JcrConstants,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.sling.api.resource.ValueMap,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  com.adobe.cq.projects.api.Project,
                  com.adobe.cq.projects.api.ProjectFilter,
                  com.adobe.cq.projects.api.ProjectManager,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ds.DataSource,
                  com.adobe.granite.ui.components.ds.SimpleDataSource,
                  com.adobe.granite.ui.components.ds.ValueMapResource"
          session="false"%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
    Config cfg = new Config(resource.getChild(Config.DATASOURCE));
    final boolean writeableProjects = cfg.get("writeableProjectsOnly", false);

    ProjectManager pm = resourceResolver.adaptTo(ProjectManager.class);
    AccessControlManager acm = resourceResolver.adaptTo(Session.class).getAccessControlManager();
    final ResourceResolver resolver = resourceResolver;

    ProjectFilter filter = new ProjectFilter();
    filter.setActive(Boolean.TRUE);

    String[] projectTemplates = cfg.get("projectTemplates", String[].class);
    if (projectTemplates != null) {
        filter.setProjectTemplates(Arrays.asList(projectTemplates));
    }

    final Iterator<Project> projectIterator = pm.getProjects(filter, 0, -1);
    final List<Resource> resourceList = new ArrayList<Resource>();

    ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
    resourceList.add(new ValueMapResource(resolver, "", "nt:unstructured", vm));
    final String projectPath = request.getParameter("projectPath");
    boolean projectPathMatches = false;

    if (projectIterator != null) {
        while(projectIterator.hasNext()) {
            Project project = projectIterator.next();
            Resource projectResource = project.adaptTo(Resource.class);;

            if (writeableProjects) {
                Privilege p = acm.privilegeFromName(Privilege.JCR_WRITE);
                if (!acm.hasPrivileges(projectResource.getPath() + "/jcr:content", new Privilege[]{p})) {
                    continue;
                }
            }

            String title = projectResource.getName();
            Resource contentResource = projectResource.getChild(JcrConstants.JCR_CONTENT);
            if (contentResource != null) {
                ValueMap projectVM = contentResource.adaptTo(ValueMap.class);
                String tstTitle = projectVM.get("jcr:title", String.class);
                if (!StringUtils.isBlank(tstTitle)) {
                    title = tstTitle;
                }
            }

            vm = new ValueMapDecorator(new HashMap<String, Object>());
            vm.put("value", projectResource.getPath());
            vm.put("text", title);

            if (!projectPathMatches && projectPath != null && projectPath.equals(projectResource.getPath())) {
                vm.put("selected", true);
                projectPathMatches = true;
            }

            resourceList.add(new ValueMapResource(resolver, projectResource.getPath(), "nt:unstructured", vm));
        }
    }

    @SuppressWarnings("unchecked")
    DataSource ds = new SimpleDataSource(new TransformIterator(resourceList.iterator(), new Transformer() {
        public Object transform(Object input) {
            try {
                return input; //new ValueMapResource(resolver, projectResource.getPath(), "nt:unstructured", vm);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }));

    request.setAttribute(DataSource.class.getName(), ds);
%>