package com.suntrust.dotcom.services.impl;

import com.adobe.cq.projects.api.Project;
import com.adobe.granite.security.user.UserProperties;
import com.adobe.granite.security.user.UserPropertiesManager;
import com.adobe.granite.security.user.UserPropertiesService;
import com.adobe.granite.taskmanagement.Task;
import com.adobe.granite.taskmanagement.TaskAction;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Status;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.payload.PayloadInfo;
import com.adobe.granite.workflow.payload.PayloadInfoBuilderContext;
import com.adobe.granite.workflow.payload.PayloadInfoBuilderManager;
import com.suntrust.dotcom.services.RequesterInboxItem;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Model(adaptables={Resource.class}, adapters={RequesterInboxItem.class})
public class RequesterInboxItemImpl implements RequesterInboxItem{

	  public static final String DYNAMIC_REL_BASE = "cq-inbox-type--";
	  public static final String DYNAMIC_REL_MODIFIER_SIMPLE = "simple";
	  @SlingObject
	  private ResourceResolver resourceResolver;
	  @Self(injectionStrategy=InjectionStrategy.OPTIONAL)
	  private Resource resource;
	  @OSGiService
	  private UserPropertiesService userPropertiesService;
	  private UserManager userMgr;
	  private UserPropertiesManager userPropertiesMgr;
	  private PayloadInfo payloadInfo;
	  private Project associatedProject;
	  private com.adobe.granite.workflow.exec.InboxItem item;
	  private Logger log = LoggerFactory.getLogger(getClass());
	  
	  @PostConstruct
	  protected void postConstruct()
	    throws Exception
	  {
	    if (this.resource != null) {
	      this.item = ((com.adobe.granite.workflow.exec.InboxItem)this.resource.getValueMap().get("inboxItem", com.adobe.granite.workflow.exec.InboxItem.class));
	    }
	    if (this.resourceResolver != null) {
	      this.userMgr = ((UserManager)this.resourceResolver.adaptTo(UserManager.class));
	    }
	    if ((this.userPropertiesService != null) && (this.resourceResolver != null)) {
	      this.userPropertiesMgr = this.userPropertiesService.createUserPropertiesManager(this.resourceResolver);
	    }
	    this.payloadInfo = getPayloadInfo();
	    this.associatedProject = getAssociatedProject();
	  }
	  
	  public String getTitle()
	  {
	    if ((this.item instanceof Task)) {
	      return ((Task)this.item).getName();
	    }
	    if ((this.item instanceof WorkItem)) {
	      return ((WorkItem)this.item).getNode().getTitle();
	    }
	    return this.item.getId();
	  }
	  
	  public String getDescription()
	  {
	    if ((this.item instanceof Task)) {
	      return ((Task)this.item).getDescription();
	    }
	    if ((this.item instanceof WorkItem))
	    {
	      if ("FailureItem".equals(this.item.getItemSubType())) {
	        return (String)((WorkItem)this.item).getMetaDataMap().get("failureMessage", "");
	      }
	      return ((WorkItem)this.item).getNode().getDescription();
	    }
	    return this.item.getId();
	  }
	  
	  public String getPath()
	  {
	    return this.item.getId();
	  }
	  
	  public String getResourceType()
	  {
	    return this.item.getItemType();
	  }
	  
	  public Map<String, String> getCurrentAssigneeInfo()
	  {
	    String currentAssignee = getAssignee();
	    if (currentAssignee != null)
	    {
	      Map<String, String> result = new HashMap();
	      String currentAssigneeType = "user";
	      
	      Authorizable auth = null;
	      UserProperties up = null;
	      try
	      {
	        auth = this.userMgr.getAuthorizable(currentAssignee);
	        if (auth != null)
	        {
	          up = this.userPropertiesMgr.getUserProperties(auth, "profile");
	          if (up != null)
	          {
	            String alternateTitle = up.getProperty("alternateTitle");
	            if (StringUtils.isNotBlank(alternateTitle))
	            {
	              currentAssignee = alternateTitle;
	            }
	            else
	            {
	              String newAssignee = up.getDisplayName();
	              if (!StringUtils.isBlank(newAssignee)) {
	                currentAssignee = newAssignee;
	              }
	            }
	          }
	        }
	      }
	      catch (RepositoryException localRepositoryException) {}
	      String thumbnailPath = "/apps/dotcom/components/workflow/inbox/content/inbox/images/user.png";
	      if (auth != null)
	      {
	        thumbnailPath = getUserThumbnailPath(auth, up);
	        if (auth.isGroup()) {
	          currentAssigneeType = "group";
	        }
	      }
	      result.put("participant", currentAssignee);
	      result.put("currentAssigneeType", currentAssigneeType);
	      result.put("currentAssigneeThumbnail", thumbnailPath);
	      result.put("currentAssignee", currentAssignee);
	      return result;
	    }
	    return null;
	  }
	  
	  public String getPayloadLink()
	  {
	    if ((this.payloadInfo != null) && 
	      (StringUtils.isNotBlank(this.payloadInfo.getBrowserPath()))) {
	      return this.payloadInfo.getBrowserPath();
	    }
	    return null;
	  }
	  
	  public String getPayloadThumbnail()
	  {
	    if (this.payloadInfo != null) {
	      return this.payloadInfo.getThumbnailPath();
	    }
	    return null;
	  }
	  
	  public boolean isPayloadFolder()
	  {
	    if ((this.payloadInfo != null) && 
	      (StringUtils.isNotBlank(this.payloadInfo.getPath())))
	    {
	      Resource payloadRes = this.resourceResolver.getResource(this.payloadInfo.getPath());
	      return isFolderResource(payloadRes);
	    }
	    return false;
	  }
	  
	  private boolean isFolderResource(Resource payloadResource)
	  {
	    if (payloadResource != null) {
	      return (payloadResource.isResourceType("sling:Folder")) || 
	        (payloadResource.isResourceType("sling:OrderedFolder")) || 
	        (payloadResource.isResourceType("nt:folder"));
	    }
	    return false;
	  }
	  
	  public String getThumbnail()
	  {
	    if (this.associatedProject != null)
	    {
	      Resource projectResource = (Resource)this.associatedProject.adaptTo(Resource.class);
	      if (projectResource != null) {
	        return projectResource.getPath() + ".thumb.319.319.png";
	      }
	    }
	    return null;
	  }
	  
	  public String getPriority()
	  {
	    switch (this.item.getPriority())
	    {
	    case LOW: 
	      return "Low";
	    case HIGH: 
	      return "High";
	    }
	    return "Medium";
	  }
	  
	  public String getPriorityIcon()
	  {
	    String priorityIcon = null;
	    switch (this.item.getPriority())
	    {
	    case HIGH: 
	      priorityIcon = "arrowUp";
	      break;
	    case MEDIUM: 
	      priorityIcon = "arrowUp";
	      break;
	    case LOW: 
	      priorityIcon = "arrowDown";
	    }
	    return priorityIcon;
	  }
	  
	  public String getPriorityStyle()
	  {
	    String priorityStyle = null;
	    switch (this.item.getPriority())
	    {
	    case HIGH: 
	      priorityStyle = "inbox-item-priority--high";
	      break;
	    case MEDIUM: 
	      priorityStyle = "inbox-item-priority--normal";
	      break;
	    case LOW: 
	      priorityStyle = "inbox-item-priority--low";
	    }
	    return priorityStyle;
	  }
	  
	  public String getStatus()
	  {
	    String status = StringUtils.capitalize(this.item.getStatus().name().toLowerCase());
	    String subType = this.item.getItemSubType();
	    if (StringUtils.equalsIgnoreCase(subType, "FailureItem")) {
	      return "Failed";
	    }
	    return status;
	  }
	  
	  public String getStatusIcon()
	  {
	    String statusIcon = "alert";
	    Status itemStatus = this.item.getStatus();
	    String subType = this.item.getItemSubType();
	    if (itemStatus != null) {
	      if (StringUtils.equalsIgnoreCase(subType, "FailureItem")) {
	        statusIcon = "alert";
	      } else if (Status.ACTIVE == itemStatus) {
	        statusIcon = "clock";
	      } else if (Status.COMPLETE == itemStatus) {
	        statusIcon = "checkCircle";
	      } else if (Status.TERMINATED == itemStatus) {
	        statusIcon = "alert";
	      }
	    }
	    return statusIcon;
	  }
	  
	  public String getStatusType()
	  {
	    String statusType = "";
	    Status itemStatus = this.item.getStatus();
	    String subType = this.item.getItemSubType();
	    if (itemStatus != null) {
	      if (StringUtils.equalsIgnoreCase(subType, "FailureItem")) {
	        statusType = "inbox-status-failed";
	      } else if (Status.ACTIVE == itemStatus) {
	        statusType = "inbox-status-active";
	      } else if (Status.COMPLETE == itemStatus) {
	        statusType = "inbox-status-complete";
	      } else if (Status.TERMINATED == itemStatus) {
	        statusType = "inbox-status-terminated";
	      }
	    }
	    return statusType;
	  }
	  
	  public String getDetailsUrl()
	  {
	    String type = "workitem";
	    String subType = this.item.getItemSubType();
	    if ((this.item instanceof Task))
	    {
	      type = "task";
	      subType = ((Task)this.item).getTaskTypeName();
	    }
	    String detailsURL = "/mnt/overlay/dotcom/components/workflow/inbox/content/inbox/details.html?item=" + this.item.getId();
	    if (StringUtils.isNotBlank(type)) {
	      detailsURL = detailsURL + "&type=" + Text.escape(type);
	    }
	    if (StringUtils.isNotBlank(subType)) {
	      detailsURL = detailsURL + "&subtype=" + Text.escape(subType);
	    }
	    detailsURL = detailsURL + "&_charset_=utf-8";
	    return detailsURL;
	  }
	  
	  public String getProjectId()
	  {
	    Resource projectResource = findProject(this.item.getId());
	    if (projectResource != null) {
	      return projectResource.getPath();
	    }
	    return null;
	  }
	  
	  public String getProjectTitle()
	  {
	    if (this.associatedProject != null) {
	      return this.associatedProject.getTitle();
	    }
	    return null;
	  }
	  
	  public String getAssociatedProjectDetailsUrl()
	  {
	    String associatedProjectDetailsHRef = null;
	    Resource projectResource = findProject(this.item.getId());
	    if (projectResource != null)
	    {
	      Resource content = projectResource.getChild("jcr:content");
	      if (content != null)
	      {
	        ValueMap map = (ValueMap)content.adaptTo(ValueMap.class);
	        associatedProjectDetailsHRef = (String)map.get("detailsHref", String.class);
	        if (StringUtils.isBlank(associatedProjectDetailsHRef)) {
	          associatedProjectDetailsHRef = "/projects/details.html";
	        }
	        if (StringUtils.isNotBlank(associatedProjectDetailsHRef)) {
	          associatedProjectDetailsHRef = associatedProjectDetailsHRef + projectResource.getPath();
	        }
	      }
	    }
	    return associatedProjectDetailsHRef;
	  }
	  
	  public String getWorkflowTitle()
	  {
	    Workflow wf = getAssociatedWorkflow();
	    if (wf != null) {
	      return (String)wf.getMetaDataMap().get("workflowTitle", "unknown title");
	    }
	    return null;
	  }
	  
	  public String getWorkflowInitiator()
	  {
	    Workflow wf = getAssociatedWorkflow();
	    if (wf != null) {
	      return wf.getInitiator();
	    }
	    return null;
	  }
	  
	  public Date getStartDate()
	  {
	    return this.item.getProgressBeginTime();
	  }
	  
	  public Date getDueDate()
	  {
	    return this.item.getDueTime();
	  }
	  
	  public String getDueDateColor()
	  {
	    Date dueDate = getDueDate();
	    if (dueDate != null)
	    {
	      Status itemStatus = this.item.getStatus();
	      if ((itemStatus != null) && ((Status.COMPLETE == itemStatus) || (Status.TERMINATED == itemStatus))) {
	        return null;
	      }
	      long deltaInMS = dueDate.getTime() - System.currentTimeMillis();
	      if (deltaInMS < 0L) {
	        return "red";
	      }
	      long deltaInS = deltaInMS / 1000L;
	      if (deltaInS < 86400L) {
	        return "orange";
	      }
	    }
	    return null;
	  }
	  
	  public Set<String> getQuickactionsRel()
	  {
	    Set<String> rels = new LinkedHashSet();
	    rels.add("foundation-collection-item-activator");
	    
	    String[] actionRels = (String[])this.resource.getValueMap().get("subTypeActionRels", String[].class);
	    if ((actionRels != null) && (actionRels.length > 0))
	    {
	      Collections.addAll(rels, actionRels);
	      return rels;
	    }
	    if ((this.item instanceof Task))
	    {
	      if (hasPermissions((Task)this.item))
	      {
	        rels.add("cq-inbox-task-details");
	        Status itemStatus = this.item.getStatus();
	        if ((itemStatus != null) && (Status.ACTIVE == itemStatus))
	        {
	          rels.add("cq-inbox-task-complete");
	          rels.add("cq-inbox-task-reassign");
	        }
	      }
	    }
	    else {
	      rels.add("cq-inbox-task-details");
	    }
	    if (getAssociatedProject() != null) {
	      rels.add("cq-inbox-open-project");
	    }
	    if (StringUtils.isNotBlank(getPayloadLink())) {
	      rels.add("cq-inbox-open-payload");
	    }
	    if ((this.item instanceof WorkItem))
	    {
	      Status itemStatus = this.item.getStatus();
	      if ((itemStatus != null) && (Status.ACTIVE == itemStatus))
	      {
	        if (!"FailureItem".equals(this.item.getItemSubType())) {
	          rels.add("cq-inbox-workitem-delegate");
	        }
	        rels.add("cq-inbox-workitem-complete");
	        rels.add("cq-inbox-workitem-stepback");
	      }
	    }
	    rels.add("cq-projects-admin-actions-properties-activator");
	    
	    addDynamicRels(rels);
	    return rels;
	  }
	  
	  public String getTaskActions()
	  {
	    if ((this.item instanceof Task)) {
	      try
	      {
	        Task task = (Task)this.item;
	        List<TaskAction> actions = task.getActions();
	        if ((actions != null) && (actions.size() > 0))
	        {
	          StringWriter sw = new StringWriter();
	          JSONWriter writer = new JSONWriter(sw);
	          
	          writer.array();
	          for (TaskAction ta : actions)
	          {
	            writer.object();
	            writer.key("actionName").value(ta.getActionID());
	            writer.key("actionId").value(ta.getActionID());
	            writer.endObject();
	          }
	          writer.endArray();
	          return sw.getBuffer().toString();
	        }
	      }
	      catch (JSONException e)
	      {
	        this.log.error("Unable to get task actions", e);
	      }
	    }
	    return null;
	  }
	  
	  private void addDynamicRels(Set<String> rels)
	  {
	    String relItemType = "cq-inbox-type--" + this.item.getItemType().toLowerCase();
	    String subType = getSubType();
	    if (StringUtils.isNotBlank(subType))
	    {
	      subType = subType.replaceAll(":", "-");
	      relItemType = relItemType + "-" + subType;
	    }
	    Status itemStatus = this.item.getStatus();
	    if (itemStatus != null) {
	      relItemType = relItemType + "-" + itemStatus.name().toLowerCase();
	    }
	    this.log.debug("item {} has dynamic rel {}", this.item.getId(), relItemType);
	    rels.add(relItemType);
	    if (this.associatedProject == null)
	    {
	      boolean addSimple = false;
	      if ((this.item instanceof Task))
	      {
	        List<TaskAction> actions = ((Task)this.item).getActions();
	        if ((actions == null) || (actions.size() == 0)) {
	          addSimple = true;
	        }
	      }
	      else
	      {
	        addSimple = true;
	      }
	      if (addSimple)
	      {
	        String simpleRelItemType = relItemType + "-" + "simple";
	        rels.add(simpleRelItemType);
	        this.log.debug("item {} has simple dynamic rel {}", this.item.getId(), simpleRelItemType);
	      }
	    }
	  }
	  
	  private boolean hasPermissions(Task task)
	  {
	    String taskPath = task.getId();
	    if (!StringUtils.startsWith(taskPath, "/")) {
	      return true;
	    }
	    if (hasPrivilege(taskPath, new String[] { "{http://www.jcp.org/jcr/1.0}write" })) {
	      return true;
	    }
	    Resource taskResource = this.resourceResolver.getResource(taskPath);
	    if (taskResource != null)
	    {
	      String currentUser = this.resourceResolver.getUserID();
	      String currentAssignee = task.getCurrentAssignee();
	      if ((currentAssignee != null) && (currentAssignee.equals(currentUser))) {
	        return true;
	      }
	      UserManager userManager = (UserManager)this.resourceResolver.adaptTo(UserManager.class);
	      if (userManager != null) {
	        try
	        {
	          Authorizable auth = userManager.getAuthorizable(currentAssignee);
	          User user = (User)userManager.getAuthorizable(currentUser);
	          if ((auth instanceof Group))
	          {
	            Group authGroup = (Group)auth;
	            if (authGroup.isMember(user)) {
	              return true;
	            }
	          }
	        }
	        catch (RepositoryException e)
	        {
	          this.log.error(String.format("Unable to check permissions of user %s on %s ", new Object[] { currentUser, taskPath }), e);
	        }
	      }
	    }
	    return false;
	  }
	  
	  private boolean hasPrivilege(String path, String... privileges)
	  {
	    try
	    {
	      Session currentSession = (Session)this.resourceResolver.adaptTo(Session.class);
	      AccessControlManager acMgr = currentSession.getAccessControlManager();
	      
	      return acMgr.hasPrivileges(path, AccessControlUtils.privilegesFromNames(acMgr, privileges));
	    }
	    catch (Exception e)
	    {
	      this.log.debug("Current user does not have jcr:write permissions on Project at " + this.item.getId());
	    }
	    return false;
	  }
	  
	  private Project getAssociatedProject()
	  {
	    Project associatedProject = null;
	    if (this.item != null)
	    {
	      Resource projectResource = findProject(this.item.getId());
	      if (projectResource != null) {
	        associatedProject = (Project)projectResource.adaptTo(Project.class);
	      }
	    }
	    return associatedProject;
	  }
	  
	  private Resource findProject(String taskPath)
	  {
	    Resource resource = this.resourceResolver.getResource(taskPath);
	    while ((resource != null) && (!ResourceUtil.isNonExistingResource(resource)))
	    {
	      if (resource.isResourceType("dotcom/components/workflow/inbox/gui/components/projects/admin/card/projectcard")) {
	        return resource;
	      }
	      resource = resource.getParent();
	    }
	    return null;
	  }
	  
	  private Workflow getAssociatedWorkflow()
	  {
	    Workflow associatedWorkflow = null;
	    if ((this.item instanceof WorkItem))
	    {
	      WorkItem wi = (WorkItem)this.item;
	      associatedWorkflow = wi.getWorkflow();
	    }
	    else if ((this.item instanceof Task))
	    {
	      Task task = (Task)this.item;
	      if ((task.getProperty("wfInstanceId") instanceof String))
	      {
	        String wfInstanceId = (String)task.getProperty("wfInstanceId");
	        WorkflowSession wfSession = (WorkflowSession)this.resourceResolver.adaptTo(WorkflowSession.class);
	        if (wfSession != null) {
	          try
	          {
	            associatedWorkflow = wfSession.getWorkflow(wfInstanceId);
	          }
	          catch (WorkflowException e)
	          {
	            this.log.debug("Failed to load workflow instance for item {} and wf instance id {}", new Object[] { this.item.getId(), wfInstanceId, e });
	          }
	        }
	      }
	    }
	    return associatedWorkflow;
	  }
	  
	  private PayloadInfo getPayloadInfo()
	  {
	    PayloadInfoBuilderManager payloadInfoBuilderMgr = (PayloadInfoBuilderManager)this.resourceResolver.adaptTo(PayloadInfoBuilderManager.class);
	    return payloadInfoBuilderMgr.getPayloadInfo(this.item, PayloadInfoBuilderContext.INITIATOR_HINT.TOUCH_INBOX.name());
	  }
	  
	  private String getUserThumbnailPath(Authorizable authorizable, UserProperties userProperties)
	  {
	    String image = null;
	    String DEFAULT_USER_ICON = "/apps/dotcom/components/workflow/inbox/content/inbox/images/user.png";
	    String DEFAULT_GROUP_ICON = "/apps/dotcom/components/workflow/inbox/content/inbox/images/group.png";
	    try
	    {
	      if ((authorizable != null) && 
	        (userProperties != null))
	      {
	        image = userProperties.getResourcePath("photos", "/primary/image.prof.thumbnail.36.png", "");
	        if ((image == null) || (image.equals(""))) {
	          if (authorizable.isGroup()) {
	            image = DEFAULT_GROUP_ICON;
	          } else {
	            image = DEFAULT_USER_ICON;
	          }
	        }
	      }
	    }
	    catch (RepositoryException localRepositoryException) {}
	    if (image == null) {
	      image = DEFAULT_USER_ICON;
	    }
	    return image;
	  }
	  
	  private String getAssignee()
	  {
	    return this.item.getCurrentAssignee();
	  }
	  
	  private String getSubType()
	  {
	    String subType = this.item.getItemSubType();
	    if ((this.item instanceof Task)) {
	      subType = ((Task)this.item).getTaskTypeName();
	    }
	    if (StringUtils.isBlank(subType)) {
	      subType = "default";
	    }
	    return subType.toLowerCase();
	  }
}
