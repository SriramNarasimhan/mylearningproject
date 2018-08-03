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
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page session="false"%><%
%><%@page import="java.util.Calendar,
                  java.util.Date,
                  java.text.SimpleDateFormat,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ValueMap,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ValueMapResourceWrapper,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.security.user.util.AuthorizableUtil,
                  com.adobe.cq.projects.api.Project,
                  com.adobe.granite.ui.components.Value,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  java.util.HashMap,
                  com.adobe.granite.taskmanagement.TaskManager,
                  com.adobe.granite.taskmanagement.Task,
 				  com.adobe.granite.workflow.payload.PayloadInfoBuilderManager,
                  com.adobe.granite.workflow.payload.PayloadInfo,
                  com.adobe.granite.workflow.payload.PayloadInfoBuilderContext,
                  org.apache.commons.lang.StringUtils, java.util.Iterator, com.adobe.granite.workflow.WorkflowSession, com.adobe.granite.workflow.exec.WorkItem, com.adobe.granite.workflow.metadata.MetaDataMap, com.adobe.granite.workflow.exec.WorkflowData, com.adobe.granite.workflow.PayloadMap, com.adobe.granite.workflow.model.WorkflowNode" %><%

    Config cfg = cmp.getConfig();

    String itemId = request.getParameter("item");
    if (StringUtils.isBlank(itemId)) {
        // no item specified -> fail fast.
        return;
    }

    WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);
    try {
        WorkItem workitem = workflowSession.getWorkItem(itemId);


        ValueMapDecorator vm = new ValueMapDecorator(new HashMap<String, Object>());

        vm.put("id", workitem.getId());
        vm.put("assignee", workitem.getCurrentAssignee());
        vm.put("contentPath", workitem.getContentPath());
        vm.put("status", workitem.getStatus());
        vm.put("localizedStatus", i18n.getVar(workitem.getStatus().name()));
        vm.put("startDate", workitem.getTimeStarted());
        vm.put("endDate", workitem.getTimeEnded());
		
		//GRANITE-14077 - Now fetching the thumbnailPath from payloadInfo
        PayloadInfoBuilderManager builder = resourceResolver.adaptTo(PayloadInfoBuilderManager.class);
        PayloadInfo info = builder.getPayloadInfo(workitem, PayloadInfoBuilderContext.INITIATOR_HINT.TOUCH_INBOX.name());
        String thumbnailUrl = info.getThumbnailPath();
        if(StringUtils.isNotBlank(thumbnailUrl)){
			thumbnailUrl = request.getContextPath() + info.getThumbnailPath();
            vm.put("thumbnailUrl", thumbnailUrl);
        }


        WorkflowNode modelNode = workitem.getNode();
        if (modelNode != null) {
            vm.put("stepTitle", i18n.getVar(modelNode.getTitle()));
            vm.put("stepDescription", i18n.getVar(modelNode.getDescription()));
        }

        MetaDataMap metaDataMap = workitem.getMetaDataMap();
        Iterator<String> metadataNameIterator = metaDataMap.keySet().iterator();
        if (metadataNameIterator != null) {
            while (metadataNameIterator.hasNext()) {
                String propertyName = metadataNameIterator.next();
                Object propertyValue = metaDataMap.get(propertyName, metaDataMap.get(propertyName));
                vm.put(propertyName, propertyValue);
            }
        }

        request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, vm);

%><section  class="coral-TabPanel-pane content-container taskdetails-tab task-tab" data-tabid="f84cc8d6-60ab-474e-bdf8-a0b7a434f289">
    <sling:include resource="<%= resource %>" resourceType="granite/ui/components/foundation/container"/>
</section><%
    } finally {
        request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, null);
    }
%>