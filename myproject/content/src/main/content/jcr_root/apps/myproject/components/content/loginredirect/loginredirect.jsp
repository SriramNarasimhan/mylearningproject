<%@page session="false"%><%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Login page Redirect component

--%>
<%@ page import="com.day.cq.wcm.api.WCMMode"%>
<%@include file="/apps/myproject/global.jsp"%>

<c:set var="userName" value="<%=request.getSession().getAttribute("username")%>"/>

<c:choose>
    <c:when test="${empty userName}">
        <script>window.location.href = window.location.href.replace("loginsuccess", "login");</script>
    </c:when>
    <c:otherwise>
		User "<c:out value="${userName}"/>" successfully logged in and in Session.
    </c:otherwise>
</c:choose>