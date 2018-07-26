<%@page import="com.first.myproject.services.RepositoryService"%>
<%@include file="/apps/myproject/global.jsp"%>
<%

com.first.myproject.services.RepositoryService rs = sling.getService(RepositoryService.class);
String repoName = rs.getRepositoryName(); %>

<%=repoName %>
 