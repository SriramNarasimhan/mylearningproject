package com.suntrust.dotcom.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.workflow.collection.ResourceCollection;
import com.day.cq.workflow.collection.ResourceCollectionManager;
import com.day.cq.workflow.collection.ResourceCollectionUtil;
import com.suntrust.dotcom.services.WorkflowPackageManager;

/**
 * ACS AEM Commons - Workflow Package Manager.
 * Manager for creating and working with Workflow Packages.
 *
 */
@Component
@Service
public class WorkflowPackageManagerImpl implements WorkflowPackageManager {
	
	/** Logger class reference variable */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowPackageManagerImpl.class);

    /** Workflow package template path */
    private static final String WORKFLOW_PACKAGE_TEMPLATE = "/libs/cq/workflow/templates/collectionpage";

    /** VLT Package definition constant */
    private static final String NT_VLT_DEFINITION = "vlt:PackageDefinition";

    /** VLT definition constant */
    private static final String NN_VLT_DEFINITION = "vlt:definition";

    /** Workflow Resource list path */
    private static final String FILTER_RESOURCE_TYPE = "cq/workflow/components/collection/definition/resourcelist";

    /** Workflow Resource path */
    private static final String FILTER_RESOURCE_RESOURCE_TYPE = "cq/workflow/components/collection/definition/resource";

    /** Workflow page path */
    private static final String WORKFLOW_PAGE_RESOURCE_TYPE = "cq/workflow/components/collection/page";

    /** Sling folder constant */
    private static final String NT_SLING_FOLDER = "sling:Folder";

    /** Sling Resource Type constant */
    private static final String SLING_RESOURCE_TYPE = SlingConstants.PROPERTY_RESOURCE_TYPE;
    
    /** Package type list */
    private static final String[] DEFAULT_WF_PACKAGE_TYPES = {"cq:Page", "cq:PageContent", "dam:Asset"};
    
    /** Workflow Package Types array */
    private String[] workflowPackageTypes = DEFAULT_WF_PACKAGE_TYPES;

    @Property(label = "Workflow Package Types",
            description = "Node Types allowed by the WF Package. Default: cq:Page, cq:PageContent, dam:Asset",
            value = { "cq:Page", "cq:PageContent", "dam:Asset" })
    
    /** wf-package.types string constant */
    public static final String PROP_WF_PACKAGE_TYPES = "wf-package.types";

    /** ResourceCollectionManager class reference variable */
    @Reference
    private ResourceCollectionManager resourceCollectionManager;

    /**
     * {@inheritDoc}
     */
    public final Page create(final ResourceResolver resourceResolver,
                             final String name, final String... paths) throws WCMException,
            RepositoryException {
        return this.create(resourceResolver, null, name, paths);
    }

    /**
     * {@inheritDoc}
     */
    public final Page create(final ResourceResolver resourceResolver, String bucketSegment,
                             final String name, final String... paths) throws WCMException,
            RepositoryException {

        final Session session = resourceResolver.adaptTo(Session.class);
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        String bucketPath = "/etc/workflow/packages";
        if (StringUtils.isNotBlank(bucketSegment)) {
            bucketPath += "/" + bucketSegment;
        }
        LOGGER.info("================>bucketPath:"+bucketPath);
        LOGGER.info("================>file list sent size: "+paths.length);
        final Node shardNode = JcrUtils.getOrCreateByPath(bucketPath,
                NT_SLING_FOLDER, NT_SLING_FOLDER, session, false);
        final Page page = pageManager.create(shardNode.getPath(), JcrUtil.createValidName(name),
                WORKFLOW_PACKAGE_TEMPLATE, name, false);
        final Resource contentResource = page.getContentResource();

        Node node = JcrUtil.createPath(contentResource.getPath() + "/" + NN_VLT_DEFINITION, NT_VLT_DEFINITION, session);
        node = JcrUtil.createPath(node.getPath() + "/filter", JcrConstants.NT_UNSTRUCTURED, session);
        JcrUtil.setProperty(node, SLING_RESOURCE_TYPE, FILTER_RESOURCE_TYPE);

        int indexNumber = 0;
        Node resourceNode = null;
        for (final String path : paths) {
            if (path != null) {
                resourceNode = JcrUtil.createPath(node.getPath() + "/resource" + indexNumber++,
                        JcrConstants.NT_UNSTRUCTURED, session);
                JcrUtil.setProperty(resourceNode, "root", path);
                JcrUtil.setProperty(resourceNode, "rules", this.getIncludeRules(path));
                JcrUtil.setProperty(resourceNode, SLING_RESOURCE_TYPE, FILTER_RESOURCE_RESOURCE_TYPE);
                LOGGER.info("================>Path created for: "+path);
            }
        }

        session.save();

        return page;
    }

    /**
     * {@inheritDoc}
     */
    public final List<String> getPaths(final ResourceResolver resourceResolver,
                                       final String path) throws RepositoryException {
        return getPaths(resourceResolver,path, workflowPackageTypes);
    }

    /**
     * {@inheritDoc}
     */
    public final List<String> getPaths(final ResourceResolver resourceResolver,
            final String path, final String[] nodeTypes) throws RepositoryException {
        final List<String> collectionPaths = new ArrayList<String>();
        LOGGER.info("collectionPaths:"+collectionPaths.size());

        final Resource resource = resourceResolver.getResource(path);
        LOGGER.info("resource:"+resource);

        if (resource == null) {
            LOGGER.warn("Requesting paths for a non-existent Resource [ {} ]; returning empty results.", path);
            return Collections.EMPTY_LIST;

        } else if (isWorkflowPackage(resourceResolver, path) == false) {
            LOGGER.debug("Requesting paths for a non-Resource Collection  [ {} ]; returning provided path.", path);
            return Arrays.asList(new String[]{ path });

        } else {
            final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            LOGGER.info("pageManager:"+pageManager);
            final Page page = pageManager.getContainingPage(path);
            LOGGER.info("page:"+page.getPath());

            if (page != null && page.getContentResource() != null) {
                final Node node = page.getContentResource().adaptTo(Node.class);
                LOGGER.info("node:"+node.getPath());

                final ResourceCollection resourceCollection =
                        ResourceCollectionUtil.getResourceCollection(node, resourceCollectionManager);
                LOGGER.info("resourceCollection:"+resourceCollection);

                if (resourceCollection != null) {
                    final List<Node> members = resourceCollection.list(nodeTypes);
                    LOGGER.info("members:"+members.size());
                    for (final Node member : members) {
                    	LOGGER.info("member:"+member.getPath());
                        collectionPaths.add(member.getPath());
                    }
                    return collectionPaths;
                }
            }

            return Arrays.asList(new String[]{ path });
        }
    }

    /**
     * {@inheritDoc}
     */
    public final void delete(final ResourceResolver resourceResolver, final String path) throws RepositoryException {
        final Resource resource = resourceResolver.getResource(path);

        if (resource == null) {
            LOGGER.error("Requesting to delete a non-existent Workflow Package [ {} ]", path);
            return;
        }

        final Node node = resource.adaptTo(Node.class);
        if (node == null) {
        	 LOGGER.error("Trying to delete a wf resource [ {} ] that does not resolve to a node.", resource.getPath());
        } else {
        	node.remove();
        	node.getSession().save();
        }
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isWorkflowPackage(final ResourceResolver resourceResolver, final String path) {
        final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        final Page workflowPackagesPage = pageManager.getPage(path);
        if (workflowPackagesPage == null) {
            return false;
        }

        final Resource contentResource = workflowPackagesPage.getContentResource();
        if (contentResource == null || contentResource.isResourceType(WORKFLOW_PAGE_RESOURCE_TYPE) == false || contentResource.getChild(NN_VLT_DEFINITION) == null) {
            return false;
        }

        return true;
    }

    /**
     * Creates the Workflow Page Resource's include rules.
     *
     * @param path the path for which the include rules are to be created
     * @return a String array of all the include rules
     */
    private String[] getIncludeRules(final String path) {
        String[] rules;

        final String rootInclude = "include:" + path;
        final String contentInclude = "include:" + path + "/jcr:content(/.*)?";

        rules = new String[]{rootInclude, contentInclude};

        return rules;
    }

    /**
     * Workflow activate method
     * 
     * @param config
     */
    @Activate
    protected final void activate(final Map<String, String> config) {
        workflowPackageTypes =
                PropertiesUtil.toStringArray(config.get(PROP_WF_PACKAGE_TYPES), DEFAULT_WF_PACKAGE_TYPES);
        LOGGER.info("workflowPackageTypes:"+workflowPackageTypes);
    }
}
