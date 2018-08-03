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
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.AttrBuilder,
                  org.apache.commons.lang.StringUtils,
                  org.apache.jackrabbit.util.Text,
                  org.apache.sling.api.resource.Resource,
                  com.adobe.granite.workflow.exec.Workflow,
                  com.adobe.granite.workflow.exec.WorkflowData,
				  com.adobe.granite.workflow.exec.InboxItem,
				  com.adobe.granite.workflow.payload.PayloadInfoBuilderManager,
                  com.adobe.granite.workflow.payload.PayloadInfo,
				  org.apache.sling.api.resource.ResourceResolver,
                  com.adobe.granite.workflow.payload.PayloadInfoBuilderContext,
                  com.adobe.granite.workflow.PayloadMap"%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@include file="/libs/cq/inbox/gui/components/inbox/itemdetails/tabs/thumbnail.jsp"%><%

    Workflow workflow = (Workflow) request.getAttribute("cq.inbox.workflowinfo");
    if (workflow == null) {
        return;
    }

    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();

    attrs.addClass("cq-inbox-details--infoimage");

    String xssTitle = "";
    String xssDescription = "";
    String coverUrl = "";
    WorkflowData workflowData = workflow.getWorkflowData();
    if (PayloadMap.TYPE_JCR_PATH.equals(workflowData.getPayloadType())) {
        String payloadPath = (String) workflowData.getPayload();
        if (StringUtils.isNotBlank(payloadPath)) {

            //GRANITE-14077 - Now fetching the thumbnailPath from payloadInfo
            ResourceResolver resolver = slingRequest.getResourceResolver();
            PayloadInfoBuilderManager builder = resolver.adaptTo(PayloadInfoBuilderManager.class);
            InboxItem inboxItem = (InboxItem)request.getAttribute("cq.inbox.inboxitem") ;
            PayloadInfo info = builder.getPayloadInfo(inboxItem, PayloadInfoBuilderContext.INITIATOR_HINT.TOUCH_INBOX.name());

            coverUrl = getThumbnailUrlForPath(resourceResolver, payloadPath);
            if (StringUtils.isBlank(coverUrl) && info!=null) {
                coverUrl = info.getThumbnailPath();
            }

            xssDescription = xssAPI.encodeForHTML(cmp.getValue().get("jcr:description", ""));
            xssTitle = xssAPI.encodeForHTML(cmp.getValue().get("jcr:title", ""));
        }
    }
%>

<div class="foundation-layout-thumbnail">
    <coral-card <%= attrs %>>
        <coral-card-asset>
            <img src="<%= xssAPI.getValidHref(coverUrl) %>"/>
        </coral-card-asset><%
        if (StringUtils.isNotBlank(xssTitle) && StringUtils.isNotBlank(xssDescription)) {
          %><coral-card-content>
                <coral-card-title><%= i18n.get(xssTitle) %></coral-card-title>
                <coral-card-propertylist>
                    <coral-card-property title="<%= i18n.get("Description") %>">
                        <%= StringUtils.isNotEmpty(xssDescription) ? i18n.get(xssDescription) : "&nbsp;" %>
                    </coral-card-property>
                </coral-card-propertylist>
            </coral-card-content><%
        }
%></coral-card>
</div>
