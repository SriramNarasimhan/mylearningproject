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
%><%@page session="false"
          import="com.adobe.granite.ui.components.Tag,
                  org.apache.sling.commons.json.io.JSONStringer,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  org.apache.sling.api.resource.ResourceResolver,
                  org.apache.commons.lang.StringUtils,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.commons.json.JSONObject,
                  org.apache.jackrabbit.api.security.user.Authorizable,
                  com.adobe.granite.security.user.UserPropertiesService,
                  org.apache.sling.api.resource.ValueMap,
                  javax.jcr.RepositoryException,
                  org.apache.sling.commons.json.JSONException" %>
<%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><ui:includeClientLib categories="cq.inbox.gui.inboxcalendarviewmodule"/><%
%>
<%
    Config cfg = cmp.getConfig();

    String src = cmp.getExpressionHelper().getString(cfg.get("src", String.class));
    src = handleURITemplate(src, request);

    String path = cmp.getExpressionHelper().getString(cfg.get("path", String.class));

    String layoutName = "foundation-layout-calendar";

    Boolean calendarActiveState;
    String active = request.getParameter("active");
    if (StringUtils.isBlank(active)) {
        calendarActiveState = Boolean.TRUE;
    } else {
        calendarActiveState = Boolean.parseBoolean(active);
    }
    String projectFilter = slingRequest.getRequestPathInfo().getSuffix();

    JSONObject result = null;
    String cardSize = "M";
    String grouping = "Shedule";

    try {
        Authorizable user = resourceResolver.adaptTo(Authorizable.class);
        if (user!=null){
            String userPath = user.getPath();
            Resource preferenceNode = null;
            preferenceNode = resourceResolver.getResource(userPath + "/" + UserPropertiesService.PREFERENCES_PATH);
            if (preferenceNode != null) {
                ValueMap conf = preferenceNode.adaptTo(ValueMap.class);
                String inboxJSONSettings = conf.get("cq.inbox.calSettings", String.class);
                if (StringUtils.isNotBlank(inboxJSONSettings)) {
                    result = new JSONObject(inboxJSONSettings);

                    cardSize = (String)result.get("cardSize");
                    grouping = (String)result.get("groupByValue");
                }
            }
        }
    } catch (RepositoryException e) {
        log.error("Failed to load user's inbox preferences, using defaults", e);
    } catch (JSONException e) {
        log.error("Failed to load user's inbox preferences, using defaults", e);
    }

    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
    cmp.populateCommonAttrs(attrs);

    attrs.addClass("foundation-collection");
    attrs.addClass("cq-inbox-calendar");
    attrs.add("data-foundation-collection-id", path);
    attrs.add("data-foundation-collection-src", src);


    String layoutJson = new JSONStringer()
            .object()
            .key("name").value(layoutName)
             .key("limit").value(cfg.get("limit", Long.class))
            .key("layoutId").value("calendar")  //resource.getName()) // This is used as an id to identify the layout when there are multiple layouts to represent the same collection.
            .endObject()
            .toString();

    attrs.addClass(layoutName);
    attrs.add("data-foundation-layout", layoutJson);

%>
<div id="CalendarWrapper"  <%= attrs %> >
    <input type="hidden" id="calState" value=<%=calendarActiveState%> />
    <input type="hidden" id="inboxCardSize" value=<%=cardSize%> />
    <input type="hidden" id="inboxGrouping" value=<%=grouping%> />
    <%
        if (projectFilter != null) {
    %>
    <input type="hidden" id="projectFilter" value=<%=projectFilter%> />
    <% }
    %>

</div>

<%!

    private String handleURITemplate(String template, HttpServletRequest request) {
        if (template != null && template.startsWith("/")) {
            template = request.getContextPath() + template;
        }

        if (template != null) {
            return template;
        }

        return null;
    }
%>
<script>
    CQ.Inbox.UI.init();

</script>