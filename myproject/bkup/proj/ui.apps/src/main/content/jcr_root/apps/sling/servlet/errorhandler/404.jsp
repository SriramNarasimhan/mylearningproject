<%@page session="false" import="
org.apache.sling.api.resource.ResourceResolverFactory,
com.suntrust.dotcom.services.ServiceUtils"%>
<%@include file="/libs/foundation/global.jsp"%>
<%String pagePath = request.getPathInfo();
pagePath = pagePath.split(".html")[0];
ServiceUtils serviceUtils = sling.getService(ServiceUtils.class);
String[] loginPage = serviceUtils.getLoginPageOfProtectedPage(pagePath);
if(loginPage[0] != null)
    response.sendRedirect(loginPage[0]+"?resource="+java.net.URLEncoder.encode(loginPage[1] , "UTF-8"));
%><%@include file="/apps/acs-commons/components/utilities/errorpagehandler/404.jsp" %>