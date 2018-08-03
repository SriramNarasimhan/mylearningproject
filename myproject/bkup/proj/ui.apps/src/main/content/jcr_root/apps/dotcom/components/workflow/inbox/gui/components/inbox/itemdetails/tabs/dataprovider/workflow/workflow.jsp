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
%><%@page import="com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Value,
                  com.adobe.granite.ui.components.ValueMapResourceWrapper,
                  com.adobe.granite.workflow.PayloadMap,
                  com.adobe.granite.workflow.exec.Workflow,
                  com.adobe.granite.workflow.exec.WorkflowData,
                  com.adobe.granite.workflow.metadata.MetaDataMap,
                  com.adobe.granite.workflow.model.WorkflowModel,
                  org.apache.sling.api.wrappers.ValueMapDecorator,
                  java.util.HashMap, com.day.cq.wcm.api.components.ComponentManager" %><%

    Config cfg = cmp.getConfig();

    Workflow workflow = (Workflow) request.getAttribute("cq.inbox.workflowinfo");
    if (workflow == null) {
        return;
    }

    // setup the project info
    // and then switch to granite/ui/components/foundation/section

    try {
        ValueMapDecorator vm = new ValueMapDecorator(new HashMap<String, Object>());

        vm.put("workflowId", workflow.getId());
        vm.put("initiator", workflow.getInitiator());
        vm.put("timeStarted", workflow.getTimeStarted());
        MetaDataMap workflowMetaDataMap = workflow.getMetaDataMap();
        String workflowTitle = i18n.getVar(workflowMetaDataMap.get("workflowTitle", String.class));
        vm.put("workflowTitle", workflowTitle);

        WorkflowData workflowData = workflow.getWorkflowData();
        if (PayloadMap.TYPE_JCR_PATH.equals(workflowData.getPayloadType())) {
            vm.put("workflowPayload", (String) workflowData.getPayload());
        }

        String startComment = workflowMetaDataMap.get("startComment", String.class);
        vm.put("startComment", i18n.getVar(startComment));

        WorkflowModel workflowModel = workflow.getWorkflowModel();
        vm.put("modelTitle", i18n.getVar(workflowModel.getTitle()));
        vm.put("modelDescription", i18n.getVar(workflowModel.getDescription()));
        vm.put("modelVersion", workflowModel.getVersion());

        request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, vm);


%><section  class="coral-TabPanel-pane content-container taskdetails-tab task-tab" data-tabid="f84cc8d6-60ab-474e-bdf8-a0b7a434f289">
    <sling:include resource="<%= resource %>" resourceType="granite/ui/components/foundation/container"/>
</section><%
    } finally {
        request.setAttribute(Value.FORM_VALUESS_ATTRIBUTE, null);
    }
%>
