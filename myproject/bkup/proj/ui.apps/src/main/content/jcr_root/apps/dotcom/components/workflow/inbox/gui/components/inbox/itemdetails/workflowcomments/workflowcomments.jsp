<%--
  ADOBE CONFIDENTIAL

  Copyright 2017 Adobe Systems Incorporated
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
          import="com.day.cq.commons.date.RelativeTimeFormat,
          com.adobe.granite.workflow.WorkflowSession,
          com.adobe.granite.workflow.exec.Workflow,
          com.adobe.granite.workflow.exec.HistoryItem,
          java.util.List,
          com.adobe.granite.security.user.UserProperties,
          com.adobe.granite.security.user.UserPropertiesManager,
          org.apache.sling.api.resource.Resource,
          org.apache.commons.lang3.StringUtils,
          java.util.Calendar,
          org.apache.sling.api.wrappers.ValueMapDecorator,
          java.util.Vector,
          java.util.HashMap, javax.jcr.RepositoryException,
          java.util.Date,
          com.day.cq.i18n.I18n, com.adobe.granite.workflow.exec.WorkItem"%>
<%@include file="/libs/granite/ui/global.jsp"%><%

    Workflow wfinfo = (Workflow) request.getAttribute("cq.inbox.workflowinfo");

    if (wfinfo==null) {
        // no item specified -> fail fast.
        return;
    }

    Vector<ValueMapDecorator> commentVector = new Vector<ValueMapDecorator>();

    WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);
    UserPropertiesManager userPropertiesManager = resourceResolver.adaptTo(UserPropertiesManager.class);
    RelativeTimeFormat tf = new RelativeTimeFormat("r", slingRequest.getResourceBundle(slingRequest.getLocale()));


    //Get WorkFlow Start Comment
    String startcomment = wfinfo.getMetaDataMap().get("startComment", String.class);;

    if (StringUtils.isNotEmpty(startcomment)) {
        ValueMapDecorator  startUserInfo = getUserInfo(wfinfo.getInitiator(),userPropertiesManager);

        String commentLabel = getCommentLabel((String) startUserInfo.get("userid"), wfinfo.getTimeStarted(),tf,i18n,"WorkFlow Start");


        ValueMapDecorator  startVM = new ValueMapDecorator(new HashMap<String, Object>());

        startVM.put("thumbnail",(String) startUserInfo.get("thumbnail"));
        startVM.put("commentLabel",commentLabel);
        startVM.put("comment",startcomment);
        commentVector.add(startVM);
    }


    List<HistoryItem> hist = workflowSession.getHistory(wfinfo);
    for (HistoryItem histItem : hist) {
        String workItemComment = histItem.getComment();
        if (StringUtils.isNotEmpty(workItemComment)) {
            WorkItem wi = histItem.getWorkItem();
            ValueMapDecorator  workitemUserInfo = getUserInfo(histItem.getUserId(),userPropertiesManager);
            String commentLabel = getCommentLabel((String) workitemUserInfo.get("userid"), wi.getTimeEnded(),tf,i18n,wi.getNode().getTitle());

            ValueMapDecorator  startVM = new ValueMapDecorator(new HashMap<String, Object>());

            startVM.put("thumbnail",(String) workitemUserInfo.get("thumbnail"));
            startVM.put("commentLabel",commentLabel);
            startVM.put("comment",workItemComment);
            commentVector.add(startVM);
        }
    }

    if (commentVector.size()!=0) {
        for (ValueMapDecorator commentItem : commentVector) {
            String xssThumbnailPath = xssAPI.getValidHref((String) commentItem.get("thumbnail"));
            String xssCommentLabel = xssAPI.filterHTML((String) commentItem.get("commentLabel"));
            String xssComment = xssAPI.filterHTML((String) commentItem.get("comment"));
            %><div class="task-comment-icon">
                <img src="<%= xssThumbnailPath %>" alt=""> <!--picture -->
            </div>
            <div class="smallText task-comment-text">
                <div><%= xssCommentLabel%><!-- person -->
                </div>
            </div>
            <div class="task-comment-balloon"><%= xssComment %></div> <!-- comment -->
            </section><% }
    } else { %>
        <%=i18n.get("No comments available")%>
    <% } %>
<%!
    private String getCommentLabel(String userName, Date date, RelativeTimeFormat tf,I18n i18n, String labelSuffix) {
        Calendar commentDate = Calendar.getInstance();
        commentDate.setTime(date);
        String dateText = tf.format(commentDate.getTimeInMillis(), true) ;
        String stepTitle = "";
        if (StringUtils.isNotEmpty(labelSuffix)) {
            stepTitle = "(" + labelSuffix + ")";
        }
        String commentLabel = i18n.get("{0} by {1} ", "example: {5 days ago} by {Alison Parker}", dateText, userName) + i18n.get(stepTitle);

        return commentLabel;
    }

    private ValueMapDecorator getUserInfo(String userId,UserPropertiesManager userPropertiesManager) throws RepositoryException {
        String thumbnail = "/libs/granite/security/clientlib/themes/default/resources/sample-user-thumbnail.36.png";
        ValueMapDecorator  vm = new ValueMapDecorator(new HashMap<String, Object>());
        UserProperties profile = null;

        profile = userPropertiesManager.getUserProperties(userId, "profile");


        if (profile != null) {
            userId = profile.getDisplayName();
            Resource photo = profile.getResource(UserProperties.PHOTOS);
            if (photo!=null) {
                thumbnail = photo.getPath() + "/primary/image.prof.thumbnail.36.png";
            }
        }

        vm.put("userid",userId);
        vm.put("thumbnail",thumbnail);

        return vm;
    }
%>

