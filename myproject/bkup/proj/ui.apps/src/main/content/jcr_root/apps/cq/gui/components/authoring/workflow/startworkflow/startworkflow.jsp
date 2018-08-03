<%--
  ADOBE CONFIDENTIAL

  Copyright 2014 Adobe Systems Incorporated
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

  The modal to start workflows.


--%><%
%><%@page import="com.adobe.granite.ui.components.Config,
                  com.adobe.granite.workflow.WorkflowException,
                  com.adobe.granite.workflow.WorkflowSession,
                  com.adobe.granite.workflow.metadata.MetaDataMap,
                  com.adobe.granite.workflow.model.WorkflowModel,
                  com.day.cq.i18n.I18n,
                  com.day.cq.wcm.api.Page,
                  org.apache.sling.api.resource.Resource,
                  java.util.Arrays"%><%
%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%!

    private boolean doInclude(WorkflowModel model, String[] tags, boolean doStrict, String exclude) {
        if (tags.length == 0) {
            return true;
        }

        MetaDataMap metaData = model.getMetaDataMap();
        String tagStr = metaData.get("tags", String.class) != null ? metaData.get("tags", String.class) : null;
        String tagStrSplits[] = (tagStr != null && !tagStr.equals("")) ? tagStr.trim().split(",") : new String[0];
        if (exclude != null &&
            exclude.equals("excludeWorkflows") &&
            Arrays.asList(tagStrSplits).contains("publish")) {
            return false;
        }
        if (tagStrSplits.length == 0 && !doStrict) {
            // for backward compatibility
            return true;
        } else {
            for (String tag : tagStrSplits) {
                for (String checkTag : tags) {
                    if (checkTag.equals(tag)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

%><%

    Page targetPage = null;

    // get page object from suffix
    String pagePath = slingRequest.getRequestPathInfo().getSuffix();
    if (pagePath != null) {
        Resource pageResource = slingRequest.getResourceResolver().resolve(pagePath);
        if (pageResource != null) {
            targetPage = pageResource.adaptTo(Page.class);
        }
    }

    if (targetPage == null) {
        return;
    }

    I18n wfI18n = new I18n(slingRequest);
    Config wfCfg = new Config(resource);
    String exclude = wfCfg.get("exclude", String.class);

%>
<coral-dialog class="js-cq-WorkflowStart">
    <form action="<%= request.getContextPath() %>/etc/workflow/instances" method="post" class="coral-Form coral-Form--vertical">
        <coral-dialog-header>
            <%= wfI18n.get("Start Workflow") %>
            <button is="coral-button" type="button" icon="close" iconsize="XS" class="coral-Dialog-closeButton" variant="minimal" coral-close title="Close" data-dismiss="modal"></button>
        </coral-dialog-header>
        <coral-dialog-content>
            <input type="hidden" name="_charset_" value="utf-8">
            <input type="hidden" name=":status" value="browser">
            <input type="hidden" name="payloadType" value="JCR_PATH">
            <input type="hidden" name="payload" value="<%= xssAPI.encodeForHTMLAttr(targetPage.getPath()) %>">
            <coral-select name="model" id="workflow-model-select-field" class="js-cq-WorkflowStart-select coral-Form-field" placeholder="<%= wfI18n.get("Select a Workflow Model") %>"><%
                WorkflowSession wfSession = slingRequest.getResourceResolver().adaptTo(WorkflowSession.class);
                WorkflowModel[] models;

                try {
                    models = wfSession.getModels();
                    String[] tags = {"wcm"};

                    for (WorkflowModel model : models) {
                        if (doInclude(model, tags, false, exclude)) {
                %><coral-select-item value="<%= model.getId() %>"><%= xssAPI.encodeForHTML(wfI18n.getVar(model.getTitle())) %></coral-select-item><%
                        }
                    }
                } catch (WorkflowException e) {
                    //throw new ServletException("Error fetching workflow models", e);
                }
            %>
            </coral-select>
            <input id="workflow-title-text-field" is="coral-textfield" type="text" name="workflowTitle" class="js-cq-WorkflowStart-title coral-Form-field" placeholder="<%= wfI18n.get("Enter title of workflow") %>">
        </coral-dialog-content>
        <coral-dialog-footer>
            <button is="coral-button" type="reset" class="js-cq-WorkflowStart-reset" coral-close data-dismiss="modal"><%= wfI18n.get("Close") %></button>
            <button id="start-workflow-button-field" is="coral-button" type="button" variant="primary" class="js-cq-WorkflowStart-submit" data-dismiss="modal" disabled="disabled"><%= wfI18n.get("Start Workflow") %></button>
        </coral-dialog-footer>
    </form>
</coral-dialog>