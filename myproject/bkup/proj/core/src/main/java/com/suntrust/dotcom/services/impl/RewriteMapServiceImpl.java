package com.suntrust.dotcom.services.impl;


import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.day.cq.dam.commons.util.DamUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.services.RewriteMapService;
import com.suntrust.dotcom.services.SearchService;
import com.suntrust.dotcom.utils.Utils;
import com.suntrust.dotcom.utils.VanityConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;


@Component(
        label = "SunTrust - Rewrite Map Service",
        description = "Service to generate rewrite map.",
        metatype = true,
        immediate = false)
@Service(value=RewriteMapService.class)
public class RewriteMapServiceImpl implements RewriteMapService {
	
	@Property(
            unbounded = PropertyUnbounded.ARRAY,
            cardinality=10,
            label = "Site Root Paths",
            description = "Allows multiple path to be configured for map generation ex: /content/suntrust/dotcom/us/en"
    )
    private static final String MULTI_PATH_PROPERTY = "config.multipaths";
    private String[] multipaths;

	@Property(label = "Destination path", description = "Destination path of the map file ex: /content/vanity_urls")
	private static final String DESTINATION_PATH = "destinationPath";
	private String destinationPath;
	
	
    @Property(
            unbounded = PropertyUnbounded.ARRAY,
            cardinality=10,
            label = "Remove Full Resource Paths",
            description = "Allows multiple path to be configured to remove full resource path for redirects and vanities ex: /content/suntrust/dotcom/us/en/personal-banking => /personal-banking"
    )
    private static final String REMOVE_FULL_RESOURCE_PROPERTY = "config.remove.fullresourcepaths";
    private ArrayList removeFullResourcePaths;
	

    @Activate
    protected void activate(final Map<String, Object> config) {
        configure(config);
    }

    private void configure(final Map<String, Object> config) {
        this.destinationPath = PropertiesUtil.toString(config.get(DESTINATION_PATH), null);
        this.multipaths = PropertiesUtil.toStringArray(config.get(MULTI_PATH_PROPERTY));
        this.removeFullResourcePaths = Utils.convertArrayToList (PropertiesUtil.toStringArray(config.get(REMOVE_FULL_RESOURCE_PROPERTY),null));
    }
    
	private final Logger log = LoggerFactory.getLogger(getClass());

	
    private static final String EMPTY_TEXT = "EMPTY";
    private static final String FILE_NAME = "vanities.txt";
    private static final String FILE_NAME_REDIRECTS = "redirects.txt";
    private static final String FILE_NAME_EXTERNAL = "external.txt";
    //private static final String FILE_NAME_REDIRECTS_302 = "redirects-302.txt";


    private final String CONTENT_ROOT_PATH = "/content";
    private final String DELIMITER = " ";
    private final String NEW_LINE = "\n";

    @Reference
    private SearchService searchService;

    @Reference
    private Replicator replicator;

    /**
     * returns the rewrite map of vanity urls for a domain path
     * @param resolver
     * @param path
     * @param destinationPath
     * @param removeFullResourcePaths
     */
    public void getRewriteMap(ResourceResolver resolver, String path, String payLoadPath, boolean isUnPubWf){

        try {
            if(StringUtils.isNotBlank(path) && StringUtils.isNotBlank(destinationPath)) {
                Resource resource = createMapFile(resolver, getFileName(resolver,path, FILE_NAME), destinationPath, resolver.getResource(payLoadPath), VanityConstants.VANITY_URL_FIELD, removeFullResourcePaths, isUnPubWf);
                Resource redirect301Res = createMapFile(resolver, getFileName(resolver,path, FILE_NAME_REDIRECTS), destinationPath, resolver.getResource(payLoadPath), VanityConstants.REDIRECTS_TYPE, removeFullResourcePaths, isUnPubWf);
                
                /* Commented for external paths*/
                //Resource external = createMapFileForExternal(resolver, getFileName(resolver,path, FILE_NAME_EXTERNAL), destinationPath);
                //Resource redirect302Res = createMapFile(resolver, getFileName(resolver,path, FILE_NAME_REDIRECTS), destinationPath, getVanityPages(resolver, path, JcrConstants.JCR_CONTENT + "/" + VanityConstants.REDIRECTS_TYPE_302), VanityConstants.REDIRECTS_TYPE_302);

                // To replicate if executed from Author
                if(resource != null){
                    log.debug(" asset: {}", resource.getPath());
                    Session session = resolver.adaptTo(Session.class);
                    replicateResource(session, resource.getPath());
                    replicateResource(session, redirect301Res.getPath());
                   // replicateResource(session, external.getPath()); /*commented as per requirement*/
                    //replicateResource(session, redirect302Res.getPath());

                }

            }

        } catch (RepositoryException e) {
            log.error(" RepositoryException in RewriteMapService {}", e);
        } catch (IOException e) {
            log.error(" IOEXception in RewriteMapService {}", e);
        } catch (ReplicationException e) {
            log.error(" ReplicationException in RewriteMapService {}", e);
        } catch (Exception e) {
            log.error(" Exception in RewriteMapService {}", e);
        }

    }

    /**
     * generates the filename for the rewrite map asset
     * @param resolver
     * @param path
     * @return
     */
    private String getFileName(ResourceResolver resolver, String path, String label){
        String filename = StringUtils.replace(path,"/content/","");
        filename = StringUtils.replace(filename, "/", "_");
                //resolver.getResource(path).getName();
        filename = filename + "_" + label;
        log.info(" file name:  {}", filename);
        return filename;
    }

    /**
     * Seraches and creates a list of pages with vanity path for a domain
     * @param resolver
     * @param path
     * @return hits
     
    private Resource getVanityPages(ResourceResolver resolver, String path, String propertyName){
    	Page page = resolver.getResource(path).adaptTo(Page.class);
    	Resource res = resolver.getResource(path);
    	if(res.isResourceType("cq:Page")){
    		if(page.getContentResource().getValueMap().containsKey(propertyName)){
    			return res;
    		}
    	}
        return null;
    }*/

    /**
     * Creates the map content for the rewite map and writes it on the asset.
     * @param resolver
     * @param fileName
     * @param destinationPath
     * @param results
     * @return map file resource
     * @throws RepositoryException
     * @throws PersistenceException
     */
    private Resource createMapFile(ResourceResolver resolver, String fileName, String destinationPath, Resource results, String propertyName, ArrayList resourcePath, boolean isUnPubWf ) throws RepositoryException, PersistenceException, UnsupportedEncodingException, IOException {
    	
    	Resource contentRootRes = resolver.getResource(CONTENT_ROOT_PATH);
    	Session session = resolver.adaptTo(Session.class);
        Resource mapAsset = null;
         
        // create map for vanity path : full path
        if (null!=results){
            PageManager pageManager = resolver.adaptTo(PageManager.class);
                Page page = pageManager.getContainingPage(results);
                String[] vanityPaths = PropertiesUtil.toStringArray(page.getProperties().get(propertyName, String[].class));
                String urlResourcePath = getUrlResourcePath(resolver, page.getPath(),resourcePath);
                
                /*create or modify asset*/
                if(contentRootRes != null){
                	Node siteAssetRoot = JcrUtils.getOrCreateByPath(destinationPath,JcrResourceConstants.NT_SLING_ORDERED_FOLDER, JcrResourceConstants.NT_SLING_ORDERED_FOLDER, session, true);
                	if(siteAssetRoot != null){
                		log.info(" asset root {}", siteAssetRoot.getPath());
                		Asset asset = modifyOrcreateAssetInDam(resolver, destinationPath, fileName, vanityPaths, urlResourcePath, isUnPubWf);
                		mapAsset = asset.adaptTo(Resource.class);                	
                	}
                }
                
            
        }else {
           log.info("No Resource found with valid vanity/redirect URL");
        }

        //create or get base node structure
        resolver.refresh();
        resolver.commit();
        return mapAsset;
    }

    /**
     * Creates a file for external rewite map under assets.
     * @param resolver
     * @param fileName
     * @param destinationPath
     * @return map file resource
     * @throws RepositoryException
     * @throws PersistenceException
     */
    private Resource createMapFileForExternal(ResourceResolver resolver, String fileName, String destinationPath) throws RepositoryException, PersistenceException, UnsupportedEncodingException {
        Resource mapAsset = null;
        
        //create or get base node structure
        Resource contentRootRes = resolver.getResource(CONTENT_ROOT_PATH);
        Session session = resolver.adaptTo(Session.class);
        if(contentRootRes != null) {
            Node siteAssetRoot = JcrUtils.getOrCreateByPath(destinationPath,JcrResourceConstants.NT_SLING_ORDERED_FOLDER, JcrResourceConstants.NT_SLING_ORDERED_FOLDER, session, true);
            //Resource siteAssetRoot = ResourceUtil.getOrCreateResource(resolver, destinationPath, JcrResourceConstants.NT_SLING_ORDERED_FOLDER, "dam:Asset", true);
            
            if(siteAssetRoot != null) {
                log.info(" external asset root {}", siteAssetRoot.getPath());
                Asset asset = getOrCreateAssetInDam( resolver, siteAssetRoot.getPath(), fileName, EMPTY_TEXT);
                mapAsset = asset.adaptTo(Resource.class);
            }
        }

        resolver.refresh();
        resolver.commit();
        return mapAsset;
    }
    

    /**
     * Get or Create an Asset of type text file
     * @param resolver
     * @param parentPath
     * @param assetName
     * @param content
     * @return
     * @throws PersistenceException
     */
    private Asset getOrCreateAssetInDam(ResourceResolver resolver, String parentPath, String assetName,String content) throws PersistenceException, UnsupportedEncodingException {
        AssetManager assetManager = resolver.adaptTo(AssetManager.class);
    	Asset asset = null;
        resolver.refresh();
        if(StringUtils.isNotBlank(parentPath) && StringUtils.isNotBlank(assetName)) {
        	Resource resource = resolver.getResource(parentPath + "/" + assetName);
        	if (resource != null && DamUtil.isAsset(resource)) {
        		asset = resource.adaptTo(Asset.class);
        	} else {	
        		asset = assetManager.createAsset(parentPath + "/" + assetName, new ByteArrayInputStream(content.getBytes(CharEncoding.UTF_8)), "text/plain", true);
        		resolver.commit();
        	}
        }
        return asset;
    }
    
    
    /**
     * Creates or Modifies an Asset of type text file at with the generated map
     * @param resolver
     * @param parentPath
     * @param assetName
     * @param content
     * @return
     * @throws IOException 
     */
    private Asset modifyOrcreateAssetInDam(ResourceResolver resolver, String parentPath, String assetName, String[] vanityPaths, String urlResourcePath, boolean isUnPubWf) throws IOException {
        AssetManager assetManager = resolver.adaptTo(AssetManager.class);
        Asset asset = null;
        StringBuffer sb = new StringBuffer();
        resolver.refresh();
        log.info("parentPath::"+parentPath);
        log.info("assetName::"+assetName);
        
        if(StringUtils.isNotBlank(parentPath) && StringUtils.isNotBlank(assetName)) {
        	Resource resource = resolver.getResource(parentPath + "/" + assetName);
        	if (resource != null && DamUtil.isAsset(resource)) {
        		asset = resource.adaptTo(Asset.class);
        		 Resource original = asset.getOriginal();
        	     InputStream stream = original.adaptTo(InputStream.class);
        	     Properties prop = new Properties();
        	     prop.load(stream);
        	     log.info("urlResourcePath::"+urlResourcePath);
        	     if(prop.containsValue(urlResourcePath)) {
        	    	 log.info("prop has urlResourcePath::"+urlResourcePath);
        	    	 prop.stringPropertyNames().stream().forEach(key ->{
        	    		 if(prop.getProperty(key).equals(urlResourcePath)) {
        	    			log.info("prop.getProperty(key)::"+prop.getProperty(key)); 
        	    			 prop.remove(key);
        	    		 }
        	    	 
        	    	 });
        	     }
        	    if(null!=vanityPaths && vanityPaths.length>0 && !isUnPubWf)	 
        	    	Stream.of(vanityPaths).forEach(vanity -> prop.put(vanity, urlResourcePath));
        	     
        	     sb.setLength(0);
        	     prop.stringPropertyNames().stream().forEach(key ->{
        	    	 sb.append(key);
        	    	 sb.append(DELIMITER);
        	    	 sb.append(prop.getProperty(key));
        	    	 sb.append(NEW_LINE);
        	     });
        	     
        	}
        	else if(!isUnPubWf){
        		sb.setLength(0);
        		if(null!=vanityPaths && vanityPaths.length>0){
	        		Stream.of(vanityPaths).forEach(vanity ->{
	        			sb.append(vanity);
	       	    	 	sb.append(DELIMITER);
	       	    	 	sb.append(urlResourcePath);
	       	    	 	sb.append(NEW_LINE);
	        		});
        		}
        		else
        			sb.append(EMPTY_TEXT);
        		
        	}
        	
        	if(null!=sb.toString())
        		asset = assetManager.createAsset(parentPath + "/" + assetName, new ByteArrayInputStream(sb.toString().getBytes(CharEncoding.UTF_8)), "text/plain", true);
        	
        	resolver.commit();
        }
        
        
        return asset;
    }
    
    /**
     *  Replicate the asset using user session
     * @param session
     * @param resPath
     * @throws ReplicationException
     */
    public void replicateResource(Session session, String resPath) throws ReplicationException {
        replicator.replicate(session, ReplicationActionType.ACTIVATE, resPath);
        log.debug(" activating {}", resPath);
    }

    
    /**
     * Return public facing URL for given resource based on the resource path to be removed
     * @param resourceResolver
     * @param pageUrl
     * @param resourcePath
     * @return pageUrl
     */
    public String getUrlResourcePath(ResourceResolver resolver,String pageUrl, ArrayList resourcePath)
    {
        if(resourcePath != null && StringUtils.isNotBlank(pageUrl))
        {
            Iterator<String> itr = resourcePath.iterator();
            while(itr.hasNext())
            {
                String value = (String)itr.next();
                if(StringUtils.isNotBlank(value))
                {
                    String[] strVal = value.split(":");
                    String key = strVal[0];
                    String replace = strVal[1];
                    pageUrl= pageUrl.replace(key,replace);
                    log.debug("removed full resource path to from url"+pageUrl+":"+key+":"+replace);
                }
            }
            return pageUrl;
        }
        return pageUrl;
    }

    @Override
    public String[] getMultiPaths(){
    	return multipaths;
    }

	@Override
	public String getDestinationPath() {
		return destinationPath;
	}

	@Override
	public ArrayList<String> getfullResourcePaths() {
		return removeFullResourcePaths;
	}

}
