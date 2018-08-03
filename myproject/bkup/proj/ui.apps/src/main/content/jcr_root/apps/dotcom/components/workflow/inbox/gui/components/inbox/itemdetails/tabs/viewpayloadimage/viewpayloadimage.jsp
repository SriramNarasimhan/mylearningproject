<%--
  ADOBE CONFIDENTIAL

  Copyright 2013 Adobe Systems Incorporated
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
            import="com.adobe.granite.ui.components.AttrBuilder,
            com.adobe.granite.ui.components.Tag,
            com.adobe.granite.workflow.exec.InboxItem,
            com.adobe.granite.workflow.payload.PayloadInfo,
            com.adobe.granite.workflow.payload.PayloadInfoBuilderContext,
            com.adobe.granite.workflow.payload.PayloadInfoBuilderManager,
			com.day.cq.wcm.api.components.ComponentContext,
            org.apache.commons.lang.StringUtils,
            com.adobe.cq.inbox.ui.ThumbnailHelper" %><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@include file="/libs/cq/inbox/gui/components/inbox/itemdetails/tabs/thumbnail.jsp"%><%
%><%
    String title = "";
    String description = "";
    String thumbnailUrl = null;

    String payloadPath = cmp.getValue().get("contentPath");

    PayloadInfo info = null;
    PayloadInfoBuilderManager builder = resourceResolver.adaptTo(PayloadInfoBuilderManager.class);
    InboxItem inboxItem = (InboxItem)request.getAttribute("cq.inbox.inboxitem") ;
    if (inboxItem != null) {
        info = builder.getPayloadInfo(inboxItem, PayloadInfoBuilderContext.INITIATOR_HINT.TOUCH_INBOX.name());
        if (info != null) {
            if (StringUtils.isNotBlank(info.getDescription())) {
                description = info.getDescription();
            }
            if (StringUtils.isNotBlank(info.getTitle())) {
                title = info.getTitle();
            }
            if (StringUtils.isNotBlank(info.getThumbnailPath())) {
                // call the thumbnailHelper to scale the .thumb. url to 319
                slingRequest.setAttribute("thumbnail", info.getThumbnailPath());
                slingRequest.setAttribute("width", 319);
                slingRequest.setAttribute("height", 319);
                ThumbnailHelper thumbnailHelper = slingRequest.adaptTo(ThumbnailHelper.class);
                thumbnailUrl = thumbnailHelper.getThumbnail();
                slingRequest.removeAttribute("thumbnail");
                slingRequest.removeAttribute("width");
                slingRequest.removeAttribute("height");
            }
        }
    }

    // if we didn't get a thumbnail from the payloadinfo try and use .thumb
    if (StringUtils.isBlank(thumbnailUrl)) {
        String newThumbnailUrl = getThumbnailUrlForPath(resourceResolver, payloadPath);
        if (StringUtils.isNotBlank(newThumbnailUrl)) {
            thumbnailUrl = newThumbnailUrl;
        } else {
            // fallback to default if we still don't have a thumbnail (e.g. because of invalid payload path)
            thumbnailUrl = "/libs/cq/ui/widgets/themes/default/icons/240x180/page.png";
        }
    }

    thumbnailUrl = request.getContextPath() + thumbnailUrl;

    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
    attrs.addClass("cq-inbox-details--infoimage");

%>
<div class="foundation-layout-thumbnail">
    <coral-card <%= attrs %>>
        <coral-card-asset>
            <img src="<%= xssAPI.getValidHref(thumbnailUrl) %>"/>
        </coral-card-asset><%
        if (StringUtils.isNotBlank(title) || StringUtils.isNotBlank(description)) {
            %><coral-card-content><%
                if (StringUtils.isNotBlank(title)) {
                    %><coral-card-title><%= xssAPI.encodeForHTML(i18n.getVar(title)) %></coral-card-title><%
                }
            %><coral-card-propertylist>
                    <coral-card-property title="<%= i18n.get("Description") %>">
                        <%= StringUtils.isNotEmpty(description) ? xssAPI.encodeForHTML(i18n.getVar(description)) : "&nbsp;" %>
                    </coral-card-property>
                </coral-card-propertylist>
            </coral-card-content><%
        }
%></coral-card>
</div>
