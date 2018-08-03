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
%><%@page session="false" %><%
%><%@page import="com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Value,
                  com.adobe.cq.projects.api.Project,
                  org.apache.commons.lang3.StringUtils,
                  org.apache.sling.api.resource.Resource,
                  com.adobe.granite.workflow.exec.Workflow,
                  com.adobe.granite.workflow.metadata.MetaDataMap,
                  java.util.List,
                  com.adobe.granite.workflow.exec.WorkItem,
                  com.adobe.granite.workflow.exec.Status,
                  java.util.Set, java.util.HashSet" %><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
    if (!(request.getAttribute("cq.inbox.workflowinfo") instanceof Workflow)) {
        return;
    }

    Config cfg = new Config(resource);

    AttrBuilder attrs = new AttrBuilder(request, xssAPI);

    String title = cfg.get("jcr:title", String.class);
    Boolean showStartEnd = cfg.get("showStartEnd", Boolean.class);
    if (showStartEnd == null) {
        showStartEnd = Boolean.TRUE;
    }

    attrs.add("id", cfg.get("id", String.class));
    attrs.addClass("cq-inbox-workflowprogress");

    Workflow workflow = (Workflow) request.getAttribute("cq.inbox.workflowinfo");

    MetaDataMap modelMetadata = workflow.getWorkflowModel().getMetaDataMap();
    String[] stagesAsObjects = modelMetadata.get("workflowStages", String[].class);
    if (stagesAsObjects == null) {
        return;
    }
    boolean isCompleted = (workflow.getState().equalsIgnoreCase("COMPLETED") || workflow.getState().equalsIgnoreCase("ABORTED"));

    Set<String> activeStages = new HashSet<String>();
    if (!isCompleted) {
        List<WorkItem> workitems = workflow.getWorkItems();
        for (WorkItem workitem : workitems) {
            if (Status.ACTIVE == workitem.getStatus()) {
                String stageName = workitem.getNode().getMetaDataMap().get("workflowStage", String.class);
                if (StringUtils.isNotBlank(stageName)) {

                    activeStages.add(stageName);
                }
            }
        }
    }
%><div <%= attrs.build() %>><coral-steplist class="cq-inbox-workflowprogress--steplist">
    <%
        int MAX_STAGE_LENGTH = 30;
        if (showStartEnd) {
        %><coral-step ><%=i18n.get("Start")%></coral-step><%
    }
    for(String stage : stagesAsObjects) {
        //If the workflow is completed, and we don't show the start end tags, we need to ensure that the last stage in the list is selected so it shows the workflow stage as done
        boolean forceSelected = isCompleted && !showStartEnd && stagesAsObjects[stagesAsObjects.length-1].equalsIgnoreCase(stage);
        %><coral-step <%=forceSelected || activeStages.contains(stage) ? "selected" : ""%> >
                <%
                    String stageLabel = i18n.getVar(stage);
                    if (stageLabel.length() >= MAX_STAGE_LENGTH) {
                        stageLabel = ellipsize(stageLabel,MAX_STAGE_LENGTH);
                    }
                %>
                <%=stageLabel%>
        </coral-step><%
    }
    if (showStartEnd) {
%><coral-step <%= isCompleted ? "selected" : ""%>><%=i18n.get("End")%></coral-step><%
    }
%></coral-steplist>
</div>
<%!
    private final static String NON_THIN = "[^iIl1\\.,']";


    private static int textWidth(String str) {
        return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
    }

    public static String ellipsize(String text, int max) {

        // measure text width
        if (textWidth(text) <= max)
            return text;


        // chop at last whole word
        int end = text.lastIndexOf(' ', max - 3);

        // String is one long word, truncate it
        if (end == -1)
            return text.substring(0, max-3) + "...";

        // find the last word
        int newEnd = end;
        do {
            end = newEnd;
            newEnd = text.indexOf(' ', end + 1);
            
            if (newEnd == -1)
                newEnd = text.length();

        } while (textWidth(text.substring(0, newEnd) + "...") < max);

        return text.substring(0, end) + "...";
    }
%>
