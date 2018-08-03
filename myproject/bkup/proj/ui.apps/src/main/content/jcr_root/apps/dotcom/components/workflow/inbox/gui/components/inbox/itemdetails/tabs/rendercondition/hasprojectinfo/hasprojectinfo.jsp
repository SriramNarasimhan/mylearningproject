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
--%><%@page session="false"
            import="com.adobe.cq.projects.api.Project,
                    com.adobe.granite.ui.components.rendercondition.RenderCondition,
                    com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition"%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%

    boolean render = false;
    if (request.getAttribute("cq.inbox.projectinfo") instanceof Project) {
        render = true;
    }

    request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(render));
%>
