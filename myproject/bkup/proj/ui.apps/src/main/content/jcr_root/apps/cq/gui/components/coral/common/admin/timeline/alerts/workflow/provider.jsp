<%--
  ADOBE CONFIDENTIAL

  Copyright 2015 Adobe Systems Incorporated
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



--%><%@page session="false" contentType="text/html; charset=utf-8"%><%
%><%@page import="java.util.List,
                  java.util.ArrayList,
                  java.util.Iterator,
                  javax.jcr.RepositoryException,
                  org.apache.commons.collections.iterators.EmptyIterator,
                  org.apache.jackrabbit.api.security.user.UserManager,
                  org.apache.jackrabbit.api.security.user.Authorizable,
                  org.apache.jackrabbit.api.security.user.Group,
                  org.apache.sling.api.resource.Resource,
                  org.apache.sling.api.resource.ResourceResolver,
                  com.adobe.granite.security.user.UserProperties,
                  com.adobe.granite.security.user.UserPropertiesManager,
                  com.adobe.granite.security.user.UserPropertiesService,
                  com.adobe.granite.workflow.WorkflowSession,
                  com.adobe.granite.workflow.WorkflowException,
                  com.adobe.granite.workflow.model.WorkflowNode,
                  com.adobe.granite.workflow.status.WorkflowStatus,
                  com.adobe.granite.workflow.exec.HistoryItem,
                  com.adobe.granite.workflow.exec.Participant,
                  com.adobe.granite.workflow.exec.Route,
                  com.adobe.granite.workflow.exec.Workflow,
                  com.adobe.granite.workflow.exec.WorkItem,
                  com.day.cq.i18n.I18n"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %><%
%><%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0" %><%
%><cq:defineObjects /><%

    ResourceResolver resolver = resource.getResourceResolver();
    I18n i18n = new I18n(slingRequest);

    UserManager userManager = resource.adaptTo(UserManager.class);
    UserPropertiesManager userPropertiesManager = resolver.adaptTo(UserPropertiesManager.class);
    Authorizable user = resolver.adaptTo(Authorizable.class);

    WorkflowSession workflowSession = resolver.adaptTo(WorkflowSession.class);
    WorkflowStatus workflowStatus = null;

    //if nothing is selected, item is null
    if (slingRequest.getRequestParameter("item") != null) {
       String path = slingRequest.getRequestParameter("item").getString("UTF-8") ;
	   Resource res = resourceResolver.getResource(path);
       if (res != null) {
            workflowStatus = res.adaptTo(WorkflowStatus.class);
       } else {
            %>
            <div class="cq-common-admin-timeline-resource-resolve-error"><%= i18n.get("Cannot locate the resource data. Please refresh the page.") %></div>
            <%
            return;
       }
    }
    if (workflowStatus != null && workflowStatus.isInRunningWorkflow(true)) {
        List<Workflow> workflows = workflowStatus.getWorkflows(true);
        for (Workflow workflow: workflows) {

            List<WorkItem> items = workflow.getWorkItems();
            for (WorkItem item : items) {
                // Show workflow alert if ...
                boolean isAssigned = false;
                String assigneeId = item.getCurrentAssignee();
                Authorizable assignee = (assigneeId!=null)?userManager.getAuthorizable(assigneeId):null;
                if (assignee != null) {
                    if (assignee.isGroup()) {
                        // ... assigned to group where user is member of ...
                        Group group= (Group) assignee;
                        isAssigned = group.isMember(user);
                    } else {
                        // ... or assigned to the user itself.
                        isAssigned = assignee.getID().equals(user.getID());
                    }
                }
                if (isAssigned) {
                    String alertText = i18n.getVar(item.getNode().getTitle());

                    Iterator<Participant> delegates = null;
                    List<Route> backRoutes = null;
                    List<Route> routes = null;
                    try {
                        delegates = workflowSession.getDelegates(item);
                    } catch (WorkflowException we) {
                        delegates = EmptyIterator.INSTANCE;
                    }
                    try {
                        backRoutes = workflowSession.getBackRoutes(item, false);
                    } catch (WorkflowException we) {
                        backRoutes = new ArrayList<Route>();
                    }
                    try {
                        routes = workflowSession.getRoutes(item, false);
                    } catch (WorkflowException we) {
                        routes = new ArrayList<Route>();
                    }

%>
                    <div class="cq-common-admin-timeline-alerts-workflow">

                        <%-- ribbon --%>
                        <div class="cq-common-admin-timeline-alerts-workflow-ribbon"><%= xssAPI.encodeForHTML(alertText) %></div>

                        <%-- action buttons --%>
                        <div class="cq-common-admin-timeline-alerts-workflow-actions cq-common-admin-timeline-toggleable" hidden>
                            <%
                            if (backRoutes != null && backRoutes.size() > 0) {
                                // no backRoutes: skip roll back button
                                %><button is="coral-button" class="cq-common-admin-timeline-alerts-workflow-actions-button" data-rel="cq-common-admin-timeline-alerts-workflow-action-back" disabled="true">
                                    <%= i18n.get("Roll back") %>
                                </button><%
                            }
                            if (delegates.hasNext()) {
                                // no delegates available: skip change assignee button
                                %><button is="coral-button" class="cq-common-admin-timeline-alerts-workflow-actions-button" data-rel="cq-common-admin-timeline-alerts-workflow-action-delegate">
                                    <%= i18n.get("Change Assignee") %>
                                </button><%
                            }
                            %>
                            <button is="coral-button" class="cq-common-admin-timeline-alerts-workflow-actions-button" variant="primary" data-rel="cq-common-admin-timeline-alerts-workflow-action-advance" disabled="true">
                              <%= i18n.get("Advance") %>
                            </button>
                        </div>


                        <%

                        //
                        // action back (Roll back)
                        //

                        if (backRoutes.size() > 0) {
                            %>
                            <div class="cq-common-admin-timeline-alerts-workflow-action cq-common-admin-timeline-alerts-workflow-action-back cq-common-admin-timeline-toggleable" hidden>
                                <%--todo: action to config?--%>
                                <form action="/bin/workflow/inbox" class="coral-Form coral-Form--vertical" onsubmit="return false;">
                                    <input type="hidden" name="_charset_" value="utf-8">
                                    <input type="hidden" name="cmd" value="advanceBack">
                                    <input type="hidden" name="item" value="<%= item.getId() %>">

                                    <div><%= i18n.get("Roll Back") %></div>

                                    <coral-select class="coral-Form-field" name="backroute-<%= item.getId() %>" placeholder="<%= i18n.get("Select Previous Step") %>">
                                        <%
                                        for (int i = 0; i < backRoutes.size(); i++) {
                                            Route route = backRoutes.get(i);

                                            // adapted from backroutes.json.jsp
                                            String value = "";
                                            String text = "";
                                            String lastStepUser = "";
                                            if (i == 0) {
                                                WorkflowNode wfNode = route.getDestinations().get(0).getTo();
                                                lastStepUser = getAuthorizableFromLastStep(workflowSession, item.getWorkflow(), wfNode.getId());
                                                if (lastStepUser != null) {
                                                    value = route.getId() + "@" + lastStepUser;
                                                    text = i18n.get("{0} ({1})",
                                                        "name of a workflow route, in brackets a user name; sample: Validate Content (Alison Parker)",
                                                        i18n.getVar(route.getName()),
                                                        getDisplayName(userPropertiesManager, lastStepUser));

                                                    %>
                                                    <coral-select-item value="<%= xssAPI.encodeForHTMLAttr(value) %>"><%= xssAPI.encodeForHTML(text) %></coral-select-item>
                                                    <%
                                                }

                                            }

                                            String[] option = getRouteValueAndText(route, userPropertiesManager, i18n);
                                            if (option == null || lastStepUser == null || lastStepUser.equals(option[2])) {
                                                // lastStepUser equals the participant of the last step:
                                                // option already rendered hence continue;
                                                continue;
                                            }

                                            %><coral-select-item value="<%= xssAPI.encodeForHTMLAttr(option[0]) %>"><%= xssAPI.encodeForHTML(option[1]) %></coral-select-item><%
                                        }
                                    %>
                                    </coral-select>

                                    <div class="cq-common-admin-timeline-alerts-workflow-actions-buttonbar">
                                        <button is="coral-button" class="cq-common-admin-timeline-alerts-workflow-action-cancel">
                                            <%= i18n.get("Cancel") %>
                                        </button>
                                        <button is="coral-button" class="cq-common-admin-timeline-alerts-workflow-action-ok" variant="primary">
                                            <%= i18n.get("Roll Back") %>
                                        </button>

                                    </div>
                                </form>
                            </div>
                        <%
                        }

                        //
                        // action delegate (Change Assignee)
                        //

                        if (delegates.hasNext()) {
                            %>
                            <div class="cq-common-admin-timeline-alerts-workflow-action cq-common-admin-timeline-alerts-workflow-action-delegate cq-common-admin-timeline-toggleable" hidden>
                                <%--todo: action to config?--%>
                                <form action="/bin/workflow/inbox" class="coral-Form coral-Form--vertical" onsubmit="return false;">
                                    <input type="hidden" name="_charset_" value="utf-8">
                                    <input type="hidden" name="cmd" value="delegate">
                                    <input type="hidden" name="item" value="<%= item.getId() %>">

                                    <div><%= i18n.get("Assign Workflow") %></div>

                                    <coral-select class="coral-Form-field" name="delegatee-<%= item.getId() %>" placeholder="<%= i18n.get("Select Assignee") %>">
                                        <%
                                        while (delegates.hasNext()) {
                                            Participant delegate = delegates.next();
                                            %><coral-select-item value="<%= xssAPI.encodeForHTMLAttr(delegate.getID()) %>"><%= xssAPI.encodeForHTML(delegate.getName()) %></coral-select-item><%
                                        }
                                    %>
                                    </coral-select>

                                    <div class="cq-common-admin-timeline-alerts-workflow-actions-buttonbar">
                                        <button is="coral-button" class="cq-common-admin-timeline-alerts-workflow-action-cancel">
                                            <%= i18n.get("Cancel") %>
                                        </button>
                                        <button is="coral-button" class="cq-common-admin-timeline-alerts-workflow-action-ok" variant="primary">
                                            <%= i18n.get("Assign") %>
                                        </button>
                                    </div>
                                </form>
                            </div>
                        <%
                        }

                         //
                         // action advance (Complete)
                         //

                        %>
                        <div class="cq-common-admin-timeline-alerts-workflow-action cq-common-admin-timeline-alerts-workflow-action-advance cq-common-admin-timeline-toggleable" hidden>
                            <%--todo: action to config?--%>
                            <form action="/bin/workflow/inbox" class="coral-Form coral-Form--vertical" onsubmit="return false;">
                                <input type="hidden" name="_charset_" value="utf-8">
                                <input type="hidden" name="cmd" value="advance">
                                <input type="hidden" name="item" value="<%= item.getId() %>">

                                <div><%= i18n.get("Advance Workflow") %></div>

                                <coral-select class="coral-Form-field" name="route-<%= item.getId() %>" placeholder="<%= i18n.get("Select Next Step") %>">
                                    <%
                                    for (int i = 0; i < routes.size(); i++) {
                                        Route route = routes.get(i);
                                        String[] option = getRouteValueAndText(route, userPropertiesManager, i18n);
                                        if (option == null) continue;

                                        %><coral-select-item value="<%= xssAPI.encodeForHTMLAttr(option[0]) %>"><%= xssAPI.encodeForHTML(option[1]) %></coral-select-item><%
                                    }
                                %>
                                </coral-select>

                                <div class="cq-common-admin-timeline-alerts-workflow-actions-buttonbar">
                                    <button is="coral-button" class="cq-common-admin-timeline-alerts-workflow-action-cancel">
                                        <%= i18n.get("Cancel") %>
                                    </button>
                                    <button is="coral-button" class="cq-common-admin-timeline-alerts-workflow-action-ok" variant="primary">
                                        <%= i18n.get("Advance") %>
                                    </button>
                                </div>
                            </form>
                        </div>


                    </div>
                    <%
                }
            }
        }
    }
%><%!

    /**
     * Get the display name of the user of the given id.
     * @param userPropertiesManager
     * @param id
     * @return The display name or the id if not available
     */
    private String getDisplayName(UserPropertiesManager userPropertiesManager, String id) {
        try {
            UserProperties profile = userPropertiesManager.getUserProperties(id, UserPropertiesService.PROFILE_PATH);
            if (profile == null) return id;
            return profile.getDisplayName();
        } catch (RepositoryException re) {
            return id;
        }
    }

    /**
     * Get value and text for a dropdown option of a route. value will be the id of the route plus the id of the
     * participant e.g. "12356@admin". text will be the name of the route plus the display name of the participant
     * e.g. "Validate (Administrator)"
     * @param route The route
     * @param userPropertiesManager The user properties managager
     * @return new String[]{value, text}
     */
    private String[] getRouteValueAndText(Route route, UserPropertiesManager userPropertiesManager, I18n i18n) {
        WorkflowNode wfNode = route.getDestinations().get(0).getTo();
        String value = route.getId();
        String text = i18n.getVar(route.getName());
        String participant = "";
        if (wfNode.getType().equals("PARTICIPANT")) {
            participant = wfNode.getMetaDataMap().get("PARTICIPANT", String.class);
            value += "@" + participant;

            // add participant's display name
            text = i18n.get("{0} ({1})",
                "name of a workflow route, in brackets a user name; sample: Validate Content (Alison Parker)",
                text,
                getDisplayName(userPropertiesManager, participant));
        }
        return new String[]{value, text, participant};
    }


    // copied from com.adobe.granite.workflow.core.util.WorkflowUtil because package is not public available
    /**
     * This method returns the userId of the  user who completed the previous step.
     *
     * @param session  workflow session
     * @param workflow workflow instance
     * @param nodeId   node id of workflow node in question
     * @return user id as string or <code>null</code> if no user could be matched
     * @throws WorkflowException
     */
    public static String getAuthorizableFromLastStep(WorkflowSession session, Workflow workflow, String nodeId) {
        try {
            List<HistoryItem> historyList = session.getHistory(workflow);
            for (int i = historyList.size(); i > 0; i--) {
                HistoryItem item = historyList.get(i - 1);
                if (item.getWorkItem() != null && item.getWorkItem().getNode().getId().equals(nodeId)) {
                    return item.getUserId();
                }
            }
            return null;
        } catch (WorkflowException we) {
            return null;
        }
    }

%>

