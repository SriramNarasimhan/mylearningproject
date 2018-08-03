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
          import="com.adobe.granite.ui.components.Config,
                  com.adobe.granite.workflow.WorkflowSession,
                  com.adobe.granite.workflow.exec.WorkItem,
                  com.adobe.granite.workflow.metadata.MetaDataMap,
                  org.apache.commons.lang.StringUtils,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceUtil, com.adobe.granite.workflow.PayloadMap, com.adobe.granite.workflow.exec.WorkflowData" %><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
    String workitemId = request.getParameter("item");
    WorkflowSession wfSession = resourceResolver.adaptTo(WorkflowSession.class);

    WorkItem workitem = wfSession.getWorkItem(workitemId);

    String payloadPath = "";
    WorkflowData workflowData = workitem.getWorkflowData();
    if (workflowData!=null && PayloadMap.TYPE_JCR_PATH.equals(workflowData.getPayloadType())) {
        payloadPath = (String) workflowData.getPayload();
    }
    MetaDataMap wfNodeMetadata = workitem.getNode().getMetaDataMap();
    String dialogPath = wfNodeMetadata.get("DIALOG_PATH", String.class);

    if (StringUtils.isNotBlank(dialogPath)) {
        boolean showWarningForMissingDialog = true;
        Resource dialog = resourceResolver.getResource(dialogPath);
        String touchDialogPath = dialog.getValueMap().get("touchUiDialogPath", dialogPath);
        if (StringUtils.isNotBlank(touchDialogPath)) {
            Resource touchDialog = resourceResolver.getResource(touchDialogPath);
            if (touchDialog != null && !ResourceUtil.isNonExistingResource(touchDialog)) {
                Resource contentChild = touchDialog.getChild("content");
                if (contentChild != null && !ResourceUtil.isNonExistingResource(contentChild)) {
                    showWarningForMissingDialog = false;
                    Config dialogConfig = new Config(touchDialog);

                    %><div class="external-dialog-injection" data-dialogpath="<%= xssAPI.getValidHref(touchDialog.getPath()) %>" data-payloadpath="<%= xssAPI.getValidHref(payloadPath) %>" ><%
                    %><ui:includeClientLib categories="<%= StringUtils.join(dialogConfig.get("extraClientlibs", new String[0]), ",") %>" /><%

                    Resource dialogContent = touchDialog.getChild("content");
                    %><sling:include path="<%= dialogContent.getPath() %>" /><%
                    %></div><%
                }
            }
        }
        if (showWarningForMissingDialog) {
            %><script type="text/javascript">
            (function($) {
                $(document).off("foundation-contentloaded.touchUiError")
                           .on("foundation-contentloaded.touchUiError", function () {

                            if ($(".dialog-injection-error").length) {
                                $(":submit").attr("disabled", "disabled");
                            }
                        });
            })(Granite.$);
            </script>
            <h1 class="dialog-injection-error"><%= i18n.get("'touchUiDialogPath' property invalid or missing on {0}", "extjs dialog at the specified path is missing the touch ui dialog property", xssAPI.getValidHref(dialogPath) )%></h1><%
        }
    }
%>