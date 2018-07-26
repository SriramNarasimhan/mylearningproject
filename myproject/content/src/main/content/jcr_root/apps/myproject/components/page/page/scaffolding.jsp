<%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Scaffolding selector script

  Finds and includes the correct scaffold for the currentPage.

--%><%@page contentType="text/html"
        pageEncoding="utf-8"
        import="com.day.cq.wcm.api.components.IncludeOptions,
                javax.jcr.Node, javax.jcr.RepositoryException,
                javax.jcr.NodeIterator,
                org.apache.sling.api.resource.Resource,
                com.day.text.Text" %><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><cq:defineObjects/><%

    // first check if the page has a scaffold specified
    String scaffoldPath = pageProperties.get("cq:scaffolding", "");
    if (scaffoldPath.length() == 0) {
        // search all scaffolds for the correct template
        // this should be improved and respect template + best content path
        Resource scRoot = resourceResolver.getResource("/etc/scaffolding");
        Node root = scRoot == null ? null : scRoot.adaptTo(Node.class);
        if (root != null) {
            scaffoldPath = findScaffoldByTemplate(root, pageProperties.get("cq:template", ""));
            if (scaffoldPath == null) {
                scaffoldPath = findScaffoldByPath(root, currentPage.getPath());
            }
        }
    }
    if (scaffoldPath == null || scaffoldPath.length() == 0) {
        // use default
        scaffoldPath = "/etc/scaffolding";
    }
    scaffoldPath+="/jcr:content.html";
    IncludeOptions.getOptions(request, true).forceSameContext(true);
    %><cq:include resourceType="wcm/scaffolding/components/scaffolding" path="<%= scaffoldPath %>" /><%

%><%!
    private String findScaffoldByTemplate(Node node, String template)
            throws RepositoryException {
        if (node.hasProperty("jcr:content/cq:targetTemplate")) {
            String tt = node.getProperty("jcr:content/cq:targetTemplate").getString();
           if (tt.equals(template)) {
               return node.getPath();
           }
        }
        NodeIterator iter = node.getNodes();
        String scaffold = null;
        while (scaffold == null && iter.hasNext()) {
            Node child = iter.nextNode();
            if (!child.getName().equals("jcr:content")) {
                scaffold = findScaffoldByTemplate(child, template);
            }
        }
        return scaffold;
    }

    private String findScaffoldByPath(Node node, String path)
            throws RepositoryException {
        if (node.hasProperty("jcr:content/cq:targetPath")) {
            String tt = node.getProperty("jcr:content/cq:targetPath").getString();
            if (Text.isDescendantOrEqual(tt, path)) {
               return node.getPath();
           }
        }
        NodeIterator iter = node.getNodes();
        String scaffold = null;
        while (scaffold == null && iter.hasNext()) {
            Node child = iter.nextNode();
            if (!child.getName().equals("jcr:content")) {
                scaffold = findScaffoldByPath(child, path);
            }
        }
        return scaffold;
    }
%>
