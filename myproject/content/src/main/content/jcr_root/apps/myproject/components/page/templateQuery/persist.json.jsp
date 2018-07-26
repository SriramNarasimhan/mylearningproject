<%@page import="com.first.myproject.services.RepositoryService"%>
<%@include file="/apps/myproject/global.jsp"%>
<%@ page import="org.apache.sling.commons.json.io.*,com.first.myproject.*" %><%
String first = request.getParameter("first");
String last = request.getParameter("last");
String address = request.getParameter("address");
String desc = request.getParameter("desc");
  
com.first.myproject.services.CustomerService cs = sling.getService(com.first.myproject.services.CustomerService.class);

int myPK = cs.injestCustData(first, last, address, desc) ;
   
//Send the data back to the client
JSONWriter writer = new JSONWriter(response.getWriter());
writer.object();
writer.key("pk");
writer.value(myPK);
  
writer.endObject();
%>