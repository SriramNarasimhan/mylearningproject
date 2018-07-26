<%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.


--%><%@include file="/apps/myproject/global.jsp" %><%
%><%@page import="java.util.Iterator"%><%
%><%@page import="org.apache.sling.api.resource.ValueMap"%><%
%><%@page import="com.day.cq.wcm.api.Page"%><%
%><%@page import="com.day.cq.wcm.core.stats.PageViewStatistics"%><%
%><%@ page import="org.slf4j.Logger" %><%
%><%@ page import="org.slf4j.LoggerFactory" %><%!
    private final Logger log = LoggerFactory.getLogger(getClass());
%><cq:includeClientLib categories="cq.jquery"/><%

// write page statistics
final PageViewStatistics pwSvc = sling.getService(PageViewStatistics.class);
String trackURL = null;
if(pwSvc != null && pwSvc.getTrackingURI() != null) {
	trackURL = pwSvc.getTrackingURI().toString();
}

if (trackURL != null && currentPage != null) {
    %><script type="text/javascript">
    {
        window.setTimeout(function() {
            $CQ.getScript("<%=trackURL%>.js?path=<%=currentPage.getPath()%>");
        }, 1);
    }
    </script><%
}
%>
<cq:include script="/libs/foundation/components/mvt/mvt_stats.jsp"/>