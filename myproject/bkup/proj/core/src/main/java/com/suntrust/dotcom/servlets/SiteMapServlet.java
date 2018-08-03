package com.suntrust.dotcom.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.Utils;

@Component(metatype = true, label = "Dotcom - Site Map Servlet", description = "Page and Asset Site Map Servlet", configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Service
@SuppressWarnings("serial")
@Properties({
        @Property(name = "sling.servlet.resourceTypes", unbounded = PropertyUnbounded.ARRAY, label = "Sling Resource Type", description = "Sling Resource Type for the Home Page component or components."),
        @Property(name = "sling.servlet.selectors", value = "index", propertyPrivate = true),
        @Property(name = "sling.servlet.extensions", value = "xml", propertyPrivate = true),
        @Property(name = "sling.servlet.methods", value = "GET", propertyPrivate = true),
        @Property(name = "webconsole.configurationFactory.nameHint", value = "Site Map for: {externalizer.domain}, on resource types: [{sling.servlet.resourceTypes}]") })
/**
* The SiteMapServlet class extends the SlingSafeMethodsServlet object 
* which creates an xml that contains the list of the pages under each parent LOB's.
*
* @author  Jagan Mohan Rao Y
* @version 1.0
* @since   2017-09-27 
*/
public final class SiteMapServlet extends SlingSafeMethodsServlet {
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SiteMapServlet.class);
	
	/** Date Format for the last modified date in the SiteMap. */
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
    
    /** Boolean to include the last modified date in the SiteMap. */
    private static final boolean DEFAULT_INCLUDE_LAST_MODIFIED = false;
    
    /** Boolean to include the parent property priority to the children pages in the SiteMap. */
    private static final boolean DEFAULT_INCLUDE_INHERITANCE_VALUE = false;
    
    /** EXTERNALIZER Domain author/publish. */
    private static final String DEFAULT_EXTERNALIZER_DOMAIN = "publish";
    
    /** Property of EXTERNALIZER Domain author/publish. */
    @Property(value = DEFAULT_EXTERNALIZER_DOMAIN, label = "Externalizer Domain", description = "Must correspond to a configuration of the Externalizer component.")
    private static final String PROP_EXTERNALIZER_DOMAIN = "externalizer.domain";
    
    /** Property of Last Modified Date. */
    @Property(boolValue = DEFAULT_INCLUDE_LAST_MODIFIED, label = "Include Last Modified", description = "If true, the last modified value will be included in the sitemap.")
    private static final String PROP_INCLUDE_LAST_MODIFIED = "include.lastmod";
    
    /** Property of Change Frequency. */
    @Property(label = "Change Frequency Properties", unbounded = PropertyUnbounded.ARRAY, description = "The set of JCR property names which will contain the change frequency value.")
    private static final String PROP_CHANGE_FREQUENCY_PROPERTIES = "changefreq.properties";
    
    /** Property of Priority property. */
    @Property(label = "Priority Properties", unbounded = PropertyUnbounded.ARRAY, description = "The set of JCR property names which will contain the priority value.")
    private static final String PROP_PRIORITY_PROPERTIES = "priority.properties";
    
    /** Property of DAM FOLDER Property. */
    @Property(label = "DAM Folder Property", description = "The JCR property name which will contain DAM folders to include in the sitemap.")
    private static final String PROP_DAM_ASSETS_PROPERTY = "damassets.property";
    
    /** Property of DAM Asset MIME Types Property. */
    @Property(label = "DAM Asset MIME Types", unbounded = PropertyUnbounded.ARRAY, description = "MIME types allowed for DAM assets.")
    private static final String PROP_DAM_ASSETS_TYPES = "damassets.types";
    
    /** Exclude from SiteMap Property. */
    @Property(label = "Exclude from Sitemap Property", description = "The boolean [cq:Page]/jcr:content property name which indicates if the Page should be hidden from the Sitemap. Default value: hideInNav")
    private static final String PROP_EXCLUDE_FROM_SITEMAP_PROPERTY = "exclude.property";
    
    /** Default include Inheritance Property to be written in SiteMap. */
    @Property(boolValue = DEFAULT_INCLUDE_INHERITANCE_VALUE, label = "Include Inherit Value", description = "If true searches for the frequency and priority attribute in the current page if null looks in the parent.")
    private static final String PROP_INCLUDE_INHERITANCE_VALUE = "include.inherit";
    
    /** Property of Character Encoding. */
    @Property(label = "Character Encoding", description = "If not set, the container's default is used (ISO-8859-1 for Jetty)")
    private static final String PROP_CHARACTER_ENCODING_PROPERTY = "character.encoding";
    
    /** NAMESPACE written in the generated XML file. */
    private static final String NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";
    
    /** CHANGE FREQUENCY to be written in the generated XML file. */ 
    private static final String CHANGEFREQUENCY = "changefreq";
    
    /**  PRIORITY to be written in the generated XML file.*/
    private static final String PRIORITY = "priority";  
    
    /** EXTERNALIZER Domain author/publish. */
    @Reference
    private Externalizer externalizer;
    
    /** Service class that is used to read the canonical URL. */
    @Reference 
    private SuntrustDotcomService dotcomService;
    
    /**Externalizer Domain*/
    private String externalizerDomain;
    
    /**Include InheritValue*/
    private boolean includeInheritValue;
    
    /**Include Last Modified Date*/
    private boolean includeLastModified;
    
    /**Change Frequency Properties*/
    private String[] changefreqProperties;
    
    /**Priority Properties*/
    private String[] priorityProperties;
    
    /**damAsset Property. */
    private String damAssetProperty;
    
    /**DamAsset Types.*/
    private List<String> damAssetTypes;
    
    /**Exclude From SiteMap Property.*/
    private String excludeFromSiteMapProperty;
    
    /**Character Encoding.*/
    private String characterEncoding;
    
    /**
     *  activate method gets initiated when the component is called
     *  for the configured resource Types with index selector 
     *  and .xml extension
     *  @param  properties object
     *  @return Nothing. 
     */    
    @Activate
    protected void activate(Map<String, Object> properties) {    	
    	LOGGER.debug("SiteMap is called for the individual LOB pages");
		this.externalizerDomain = PropertiesUtil.toString(
				properties.get(PROP_EXTERNALIZER_DOMAIN),
				DEFAULT_EXTERNALIZER_DOMAIN);
		this.includeLastModified = PropertiesUtil.toBoolean(
				properties.get(PROP_INCLUDE_LAST_MODIFIED),
				DEFAULT_INCLUDE_LAST_MODIFIED);
		this.includeInheritValue = PropertiesUtil.toBoolean(
				properties.get(PROP_INCLUDE_INHERITANCE_VALUE),
				DEFAULT_INCLUDE_INHERITANCE_VALUE);
		this.changefreqProperties = PropertiesUtil
				.toStringArray(
						properties.get(PROP_CHANGE_FREQUENCY_PROPERTIES),
						new String[0]);
		this.priorityProperties = PropertiesUtil.toStringArray(
				properties.get(PROP_PRIORITY_PROPERTIES), new String[0]);
		this.damAssetProperty = PropertiesUtil.toString(
				properties.get(PROP_DAM_ASSETS_PROPERTY), "");
		this.damAssetTypes = Arrays.asList(PropertiesUtil.toStringArray(
				properties.get(PROP_DAM_ASSETS_TYPES), new String[0]));
		this.excludeFromSiteMapProperty = PropertiesUtil.toString(
				properties.get(PROP_EXCLUDE_FROM_SITEMAP_PROPERTY),
				NameConstants.PN_HIDE_IN_NAV);
		this.characterEncoding = PropertiesUtil.toString(
				properties.get(PROP_CHARACTER_ENCODING_PROPERTY), null);
    }
    
    /**
     *  doGet method gets called when the requests originates from
     *  the configured resource Types with index selector   
     *  and .xml extension 
     *  @param  request object
     *  @param  response object
     *  @return Nothing. 
     */
    
    @Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
        response.setContentType(request.getResponseContentType());
        if (characterEncoding != null) {
            response.setCharacterEncoding(characterEncoding);
        }
        ResourceResolver resourceResolver = request.getResourceResolver();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        Page page = pageManager.getContainingPage(request.getResource());

        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        try {
            XMLStreamWriter stream = outputFactory.createXMLStreamWriter(response.getWriter());
            stream.writeStartDocument("1.0");
            stream.writeStartElement("", "urlset", NAMESPACE);
            stream.writeNamespace("", NAMESPACE);

            // first do the current page
            if(page != null){
            	write(page, stream, resourceResolver);
            	for (Iterator<Page> children = page.listChildren(new PageFilter(false, true), true); children.hasNext();) {            	
                    write(children.next(), stream, resourceResolver);
                }
            }                        

            if (!damAssetTypes.isEmpty() && damAssetProperty.length() > 0) {   
                for (Resource assetFolder : getAssetFolders(page, resourceResolver)) {
                    writeAssets(stream, assetFolder, resourceResolver);
                }
            }
            stream.writeEndElement();
            stream.writeEndDocument();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
    /**
     *  getAssetFolders method gets the list of the asset folders
     *  iterative method  
     *  and .xml extension 
     *  @param  page object
     *  @param  resolver object
     *  @return Collection of Type Resource. 
     */
    private Collection<Resource> getAssetFolders(Page page, ResourceResolver resolver) {
        List<Resource> allAssetFolders = new ArrayList<Resource>();
        ValueMap properties = page.getProperties();
        String[] configuredAssetFolderPaths = properties.get(damAssetProperty, String[].class);
        if (configuredAssetFolderPaths != null) {
            // Sort to aid in removal of duplicate paths. 
            Arrays.sort(configuredAssetFolderPaths);
            String prevPath = "#";
            for (String configuredAssetFolderPath : configuredAssetFolderPaths) {
                // Ensure that this folder is not a child folder of another
                // configured folder, since it will already be included when
                // the parent folder is traversed.
				if (StringUtils.isNotBlank(configuredAssetFolderPath)
						&& !configuredAssetFolderPath.equals(prevPath)
						&& !StringUtils.startsWith(configuredAssetFolderPath,
								prevPath + "/")) {
                    Resource assetFolder = resolver.getResource(configuredAssetFolderPath);
                    if (assetFolder != null) {
                        prevPath = configuredAssetFolderPath;
                        allAssetFolders.add(assetFolder);
                    }
                }
            }
        }
        return allAssetFolders;
    }
    /**
     * write method writes the path of the LOB's to the XML file.
     * 
     *  @param  page the cuurentPage under the parents iterative
     *  @param  stream XMLStreamWriter object
     *  @param  resolver resource resolver object    
     *  @return Nothing. 
     */
    private void write(Page page, XMLStreamWriter stream, ResourceResolver resolver) throws XMLStreamException {       
        if(page == null || page.getContentResource() == null){  
        	return;
        }else{
        	 if (isHidden(page)) {
                 return;
             } 
	        stream.writeStartElement(NAMESPACE, "url");
			String canonicalpath = Utils.getCanonicalUrl(
					dotcomService.getPropertyArray("canonical.urls"),
					page.getPath(), resolver);					
	    	LOGGER.debug("canonicalpath is called info mode"+canonicalpath);
	
	        String loc = externalizer.externalLink(resolver, externalizerDomain, canonicalpath); 
	        writeElement(stream, "loc", loc);	
	        if (includeLastModified) {
	            Calendar cal = page.getLastModified();
	            if (cal != null) {
	                writeElement(stream, "lastmod", DATE_FORMAT.format(cal));
	            }
	        }	
	        if (includeInheritValue) {
				HierarchyNodeInheritanceValueMap hierarchyNodeInheritanceValueMap = new HierarchyNodeInheritanceValueMap(
						page.getContentResource());           
	            writeFirstPropertyValue(stream, CHANGEFREQUENCY, changefreqProperties, hierarchyNodeInheritanceValueMap);
	            writeFirstPropertyValue(stream, PRIORITY, priorityProperties, hierarchyNodeInheritanceValueMap);  
	        } else {
	            ValueMap properties = page.getProperties();
	            writeFirstPropertyValue(stream, CHANGEFREQUENCY, changefreqProperties, properties);
	            writeFirstPropertyValue(stream, PRIORITY, priorityProperties, properties);
	        }        
	        stream.writeEndElement();
        }
    }
    /**
     * isHidden method will check if the page should be present in SiteMap.
     * 
     *  @param  page Page object    
     *  @return boolean checkbox is selected? true/false. 
     */
    private boolean isHidden(final Page page) {
        return page.getProperties().get(this.excludeFromSiteMapProperty, false);
    }
    /**
     * writeAsset method writes the assets to the XML file.
     *  @param  asset Asset object
     *  @param  stream XMLStreamWriter object 
     *  @param  resolver element name to be written
     *  @param  text the element text that should be written
     *  @return Nothing. 
     */
	private void writeAsset(Asset asset, XMLStreamWriter stream,
			ResourceResolver resolver) throws XMLStreamException {
        stream.writeStartElement(NAMESPACE, "url");
        String loc = externalizer.externalLink(resolver, externalizerDomain, asset.getPath());
        writeElement(stream, "loc", loc);
        if (includeLastModified) {
            long lastModified = asset.getLastModified();
            if (lastModified > 0) {
                writeElement(stream, "lastmod", DATE_FORMAT.format(lastModified));
            }
        }
        Resource contentResource = asset.adaptTo(Resource.class).getChild(JcrConstants.JCR_CONTENT);
        if (contentResource != null) {
            if (includeInheritValue) {
				HierarchyNodeInheritanceValueMap hierarchyNodeInheritanceValueMap = new HierarchyNodeInheritanceValueMap(
						contentResource);
                writeFirstPropertyValue(stream, CHANGEFREQUENCY, changefreqProperties, hierarchyNodeInheritanceValueMap);
                writeFirstPropertyValue(stream, PRIORITY, priorityProperties, hierarchyNodeInheritanceValueMap);
            } else {
                ValueMap properties = contentResource.getValueMap(); 
                writeFirstPropertyValue(stream, CHANGEFREQUENCY, changefreqProperties, properties);
                writeFirstPropertyValue(stream, PRIORITY, priorityProperties, properties); 
            }
        }
        stream.writeEndElement();
    }
    /**
     * writeAssets method writes the change frequency/priority values to the XML file.     
     *  @param  stream XMLStreamWriter object 
     *  @param  assetFolder Resource object
     *  @param  resolver element name to be written
     *  @param  text the element text that should be written
     *  @return Nothing. 
     */
	private void writeAssets(final XMLStreamWriter stream,
			final Resource assetFolder, final ResourceResolver resolver)
			throws XMLStreamException {
        for (Iterator<Resource> children = assetFolder.listChildren(); children.hasNext();) {
            Resource assetFolderChild = children.next();
            if (assetFolderChild.isResourceType(DamConstants.NT_DAM_ASSET)) {
                Asset asset = assetFolderChild.adaptTo(Asset.class);
                if (damAssetTypes.contains(asset.getMimeType())) {
                    writeAsset(asset, stream, resolver);
                }
            } else {
                writeAssets(stream, assetFolderChild, resolver);
            }
        }
    }
    /**
     * writeAssets method writes the change frequency/priority values to the XML file.     
     *  @param  stream XMLStreamWriter object 
     *  @param  elementName String object
     *  @param  propertyNames property names
     *  @param  properties properties current page values.
     *  @return Nothing. 
     */
    private void writeFirstPropertyValue(final XMLStreamWriter stream, final String elementName,
            final String[] propertyNames, final ValueMap properties) throws XMLStreamException {
        for (String prop : propertyNames) {
            String value = properties.get(prop, String.class);
            if (value != null) {
                writeElement(stream, elementName, value);
                break;
            }
        }
    }
    /**
     * writeFirstPropertyValue method writes the assets to the XML file iterative.     
     *  @param  stream XMLStreamWriter object 
     *  @param  elementName String object
     *  @param  propertyNames property names
     *  @param  properties properties values from the parent page ValueMap.
     *  @return Nothing.  
     */
    private void writeFirstPropertyValue(final XMLStreamWriter stream, final String elementName,
            final String[] propertyNames, final InheritanceValueMap properties) throws XMLStreamException { 
        for (String prop : propertyNames) {
            String value = properties.get(prop, String.class);
            if (value == null) {
                value = properties.getInherited(prop, String.class);
            }
            if (value != null) {
                writeElement(stream, elementName, value);
                break;
            }
        }
    }
    /**
     * writeElement method creates the elements in the XML file.
     * 
     *  @param  stream XMLStreamWriter object
     *  @param  elementName element name to be written
     *  @param  text the element text that should be written
     *  @return Nothing. 
     */
	private void writeElement(final XMLStreamWriter stream,
			final String elementName, final String text)
			throws XMLStreamException {
        stream.writeStartElement(NAMESPACE, elementName);
        stream.writeCharacters(text);
        stream.writeEndElement();
    }

}