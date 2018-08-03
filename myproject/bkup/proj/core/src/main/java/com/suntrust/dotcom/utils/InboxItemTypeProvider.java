package com.suntrust.dotcom.utils;


import com.adobe.granite.workflow.exec.Status;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.api.observation.JackrabbitEventFilter;
import org.apache.jackrabbit.api.observation.JackrabbitObservationManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate=true, 
metatype=true, 
label="Suntrust Requester Inbox Item Type Provider", 
description="Manages the mappings from the inbox item type and subtype for aem/inbox/requester URI"
)
@Service({InboxItemTypeProvider.class})
public class InboxItemTypeProvider  implements EventListener
{
  private static final Logger LOGGER = LoggerFactory.getLogger(InboxItemTypeProvider.class);
  
  @Reference
  SlingRepository slingRepository;
  @Reference
  private ResourceResolverFactory rrf;
  
  private Map<String, InboxItemTypeDefinition> itemTypeRenderers;
  private Session itemTypeReaderSession;
  private String[] legacyPaths ={"/etc/projects/tasks/types", "/libs/cq/core/content/projects/tasktypes", "/apps/cq/core/content/projects/tasktypes"};
  private String[] registryPaths = {"/apps/dotcom/components/workflow/inbox/extensions/itemtyperegistry"};
  private String defaultURLFailureItem = "/apps/dotcom/components/workflow/inbox/content/inbox/details/failureitem.html";
  private String defaultURLWorkItem = "/apps/dotcom/components/workflow/inbox/content/inbox/details/workitem.html";
  private String defaultURLTask = "/apps/dotcom/components/workflow/inbox/content/inbox/details/task.html";
  
  @Activate
  protected void activate(ComponentContext componentContext)
    throws Exception
  {
    
    String[] observationPaths = new String[this.legacyPaths.length + this.registryPaths.length];
    int targetIndex = 0;
    for (int sourceIndex = 0; sourceIndex < this.legacyPaths.length; targetIndex++)
    {
      observationPaths[targetIndex] = this.legacyPaths[sourceIndex];sourceIndex++;
    }
    for (int sourceIndex = 0; sourceIndex < this.registryPaths.length; targetIndex++)
    {
      observationPaths[targetIndex] = this.registryPaths[sourceIndex];sourceIndex++;
    }
    this.itemTypeReaderSession = this.slingRepository.loginService("dotcomreadservice", null);
    if (this.itemTypeReaderSession != null)
    {
      LOGGER.debug("Adding listener to track task type changes.");
      
      JackrabbitEventFilter filter = new JackrabbitEventFilter().setAbsPath(observationPaths[0]).setEventTypes(63).setIsDeep(true).setNoLocal(true).setNoExternal(false).setAdditionalPaths(observationPaths);
      
      JackrabbitObservationManager observationManager = (JackrabbitObservationManager)this.itemTypeReaderSession.getWorkspace().getObservationManager();
      observationManager.addEventListener(this, filter);
      
      loadItemTypeRenderers();
    }
    else
    {
      LOGGER.error("Unable to log in itemtype reader service user.");
    }
  }
  
  @Deactivate
  protected void deactivate(ComponentContext context)
  {
    if (this.itemTypeReaderSession != null)
    {
      try
      {
        this.itemTypeReaderSession.getWorkspace().getObservationManager().removeEventListener(this);
      }
      catch (RepositoryException e)
      {
        LOGGER.warn("Error while unregistering observation listener.", e);
      }
      this.itemTypeReaderSession.logout();
    }
  }
  
  public void onEvent(EventIterator events)
  {
    LOGGER.debug("Received Observation event.  Rebuilding cache.");
    loadItemTypeRenderers();
  }
  
  public String resolveDetailsURL(String itemTypeName, String subTypeName)
  {
    if (StringUtils.isBlank(subTypeName)) {
      subTypeName = "default";
    }
    InboxItemTypeDefinition itemType = getItemType(itemTypeName, subTypeName);
    if (itemType != null) {
      return itemType.getURL();
    }
    return null;
  }
  
  public List<InboxItemTypeDefinition> getItemTypes(String type, boolean includeUntitled)
  {
    List<InboxItemTypeDefinition> result = new ArrayList();
    
    boolean checkType = StringUtils.isNotBlank(type);
    for (InboxItemTypeDefinition itemType : this.itemTypeRenderers.values())
    {
      boolean add = true;
      if ((checkType) && (!StringUtils.equalsIgnoreCase(type, itemType.getType()))) {
        add = false;
      } else if ((!includeUntitled) && (StringUtils.isBlank(itemType.getTitle()))) {
        add = false;
      }
      if (add) {
        result.add(itemType);
      }
    }
    return result;
  }
  
  private void loadItemTypeRenderers()
  {
    LOGGER.debug("Building task type cache.");
    
    Map<String, InboxItemTypeDefinition> newItemTypeRenderers = new HashMap();
    ResourceResolver resourceResolver = null;
    try
    {
      resourceResolver = this.rrf.getServiceResourceResolver(Collections.singletonMap("sling.service.subservice", "dotcomreadservice"));
      if (this.legacyPaths != null) {
        for (String legacyPath : this.legacyPaths)
        {
          Resource oldLocation = resourceResolver.getResource(legacyPath);
          if ((oldLocation != null) && (oldLocation.getChildren().iterator().hasNext()))
          {
            LOGGER.debug("Task type definitions detected under old location [{}].  These should be migrated to [/apps/cq/inbox/content/inbox/itemtyperegistry/task].", "/etc/projects/tasks/types");
            readItemTypes(newItemTypeRenderers, "task", oldLocation);
          }
        }
      }
      if (this.registryPaths != null) {
        for (String registryPath : this.registryPaths)
        {
          Resource newItemTypeRegistryRoot = resourceResolver.getResource(registryPath);
          readItemTypes(newItemTypeRenderers, newItemTypeRegistryRoot);
        }
      }
      addDefaultTypes(newItemTypeRenderers);
      
      this.itemTypeRenderers = newItemTypeRenderers;
    }
    catch (LoginException e)
    {
      LOGGER.warn("Error reading task type config from /libs/cq/core/content/projects/tasktypes", e);
    }
    finally
    {
      if ((resourceResolver != null) && (resourceResolver.isLive())) {
        resourceResolver.close();
      }
    }
  }
  
  private void addDefaultTypes(Map<String, InboxItemTypeDefinition> newItemTypeRenderers)
  {
    String failureId = InboxItemsUtils.constructInboxTypeId("workitem", "failureitem");
    if (!newItemTypeRenderers.containsKey(failureId)) {
      newItemTypeRenderers.put(failureId, constructDefaultType("workitem", "failureitem", null, this.defaultURLFailureItem));
    }
    String workitemTypeId = InboxItemsUtils.constructInboxTypeId("workitem", "default");
    if (!newItemTypeRenderers.containsKey(workitemTypeId)) {
      newItemTypeRenderers.put(workitemTypeId, constructDefaultType("workitem", "default", "Default Workitem Type", this.defaultURLWorkItem));
    }
    String defaultTaskTypeId = InboxItemsUtils.constructInboxTypeId("task", "default");
    if (!newItemTypeRenderers.containsKey(defaultTaskTypeId)) {
      newItemTypeRenderers.put(defaultTaskTypeId, constructDefaultType("task", "default", "Default Task Type", this.defaultURLTask));
    }
  }
  
  private InboxItemTypeDefinition constructDefaultType(String type, String subType, String title, String url)
  {
    InboxItemTypeDefinition result = new InboxItemTypeDefinition(type, subType);
    result.setURL(url);
    result.setTitle(title);
    return result;
  }
  
  private void readItemTypes(Map<String, InboxItemTypeDefinition> newItemTypeRenderers, Resource newItemTypeRegistry)
  {
    if (newItemTypeRegistry != null) {
      for (Resource type : newItemTypeRegistry.getChildren())
      {
        String typeName = type.getName();
        readItemTypes(newItemTypeRenderers, typeName, type);
      }
    }
  }
  
  private void readItemTypes(Map<String, InboxItemTypeDefinition> newItemTypeRenderers, String typeName, Resource newItemTypeRegistry)
  {
    for (Resource taskTypeResource : newItemTypeRegistry.getChildren()) {
      recurseItemTypes(newItemTypeRenderers, typeName, null, taskTypeResource);
    }
  }
  
  private void recurseItemTypes(Map<String, InboxItemTypeDefinition> newItemTypeRenderers, String type, String baseSubType, Resource currentLocation)
  {
    if (currentLocation == null) {
      return;
    }
    String newBaseSubType = baseSubType + ":" + currentLocation.getName();
    String url;
    if ((currentLocation.getValueMap().containsKey("url")) || 
      (currentLocation.getValueMap().containsKey("inboxUrl")))
    {
      url = (String)currentLocation.getValueMap().get("url");
      if ((StringUtils.isBlank(url)) && (currentLocation.getValueMap().containsKey("inboxUrl")))
      {
        LOGGER.debug("Type definition '{}' uses old 'inboxUrl' property instead of 'url' property", currentLocation.getPath());
        url = (String)currentLocation.getValueMap().get("inboxUrl");
      }
      InboxItemTypeDefinition newDefinition = new InboxItemTypeDefinition(type, newBaseSubType);
      newDefinition.setURL(url);
      newDefinition.setTitle((String)currentLocation.getValueMap().get("jcr:title", String.class));
      newDefinition.setTypeDefinitionSourcePath(currentLocation.getPath());
      newDefinition.setActionRels((String[])currentLocation.getValueMap().get("actionRels.ACTIVE", String[].class), Status.ACTIVE);
      newDefinition.setActionRels((String[])currentLocation.getValueMap().get("actionRels.COMPLETE", String[].class), Status.COMPLETE);
      newDefinition.setActionRels((String[])currentLocation.getValueMap().get("actionRels.TERMINATED", String[].class), Status.TERMINATED);
      newItemTypeRenderers.put(newDefinition.getId(), newDefinition);
    }
    for (Resource itemType : currentLocation.getChildren()) {
      recurseItemTypes(newItemTypeRenderers, type, newBaseSubType, itemType);
    }
  }
  
  public InboxItemTypeDefinition getItemType(String itemTypeName, String subTypeName)
  {
    String inboxTypeId = InboxItemsUtils.constructInboxTypeId(itemTypeName, subTypeName);
    return (InboxItemTypeDefinition)this.itemTypeRenderers.get(inboxTypeId);
  }
  
  protected void bindSlingRepository(SlingRepository paramSlingRepository)
  {
    this.slingRepository = paramSlingRepository;
  }
  
  protected void unbindSlingRepository(SlingRepository paramSlingRepository)
  {
    if (this.slingRepository == paramSlingRepository) {
      this.slingRepository = null;
    }
  }
  
  protected void bindRrf(ResourceResolverFactory paramResourceResolverFactory)
  {
    this.rrf = paramResourceResolverFactory;
  }
  
  protected void unbindRrf(ResourceResolverFactory paramResourceResolverFactory)
  {
    if (this.rrf == paramResourceResolverFactory) {
      this.rrf = null;
    }
  }
}
