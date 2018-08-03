package com.suntrust.dotcom.servlets;

import java.io.IOException;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.Utils;

@Component(metatype = true, label = "Dotcom Index - Site Map Servlet", description = "Page and Asset Site Map Servlet", configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Service
@SuppressWarnings("serial")
@Properties({
		@Property(name = "sling.servlet.resourceTypes", unbounded = PropertyUnbounded.ARRAY, label = "Sling Resource Type", description = "Sling Resource Type for the Home Page component or components."),
        @Property(name = "sling.servlet.selectors", value = "sitemap", propertyPrivate = true),
        @Property(name = "sling.servlet.extensions", value = "xml", propertyPrivate = true),
        @Property(name = "sling.servlet.methods", value = "GET", propertyPrivate = true),
        @Property(name = "webconsole.configurationFactory.nameHint", value = "Site Map for: {externalizer.domain}, on resource types: [{sling.servlet.resourceTypes}]") })

/**
* The SiteMapIndexServlet class extends the SlingSafeMethodsServlet object 
* which creates an xml that contains the list of the SiteMap xmls of the parent LOB's.
*
* @author  Jagan Mohan Rao Y
* @version 1.0
* @since   2017-09-27 
*/
public final class SiteMapIndexServlet extends SlingSafeMethodsServlet { 
	
	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SiteMapIndexServlet.class);
	
	/** Date Format for the last modified date in the SiteMap. */
    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
    
    /** Boolean to include the last modified date in the SiteMap. */
    private static final boolean DEFAULT_INCLUDE_LAST_MODIFIED = false;
    
    /** EXTERNALIZER Domain author/publish. */
    private static final String DEFAULT_EXTERNALIZER_DOMAIN = "publish";
    
    /** Property of EXTERNALIZER Domain author/publish. */
    @Property(value = DEFAULT_EXTERNALIZER_DOMAIN, label = "Externalizer Domain", description = "Must correspond to a configuration of the Externalizer component.")
    private static final String PROP_EXTERNALIZER_DOMAIN = "externalizer.domain";
    
    /** Property of Last Modified Date. */
    @Property(boolValue = DEFAULT_INCLUDE_LAST_MODIFIED, label = "Include Last Modified", description = "If true, the last modified value will be included in the sitemap.")
    private static final String PROP_INCLUDE_LAST_MODIFIED = "include.lastmod";
    
    /** Property of Character Encoding. */
    @Property(label = "Character Encoding", description = "If not set, the container's default is used (ISO-8859-1 for Jetty)")
    private static final String PROP_CHARACTER_ENCODING_PROPERTY = "character.encoding";
    
    /** Property of the LOB_TYPES. */
    private static final String PROP_LOB_TYPES = "lob.Types";
    
    /** Name Space written in the generated xml file. */    
    private static final String NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";
    
    /** EXTERNALIZER Domain author/publish. */
    @Reference
    private Externalizer externalizer; 
    
    /** Service class that is used to read the canonical URL. */
    @Reference 
    private SuntrustDotcomService dotcomService;
   
    /** EXTERNALIZER Domain author/publish. */
    private String externalizerDomain;
    
    /** include LastModified date true/false. */
    private boolean includeLastModified;
    
    /** Character Encoding*/
    private String characterEncoding;
    
    /** List of the LobTypes from the config file. */
    private List<String> lobTypes;
    
    /** List of the pages based on priority. */
    private Map<String, Float> priorityLOBOrderHashMap = new HashMap<String, Float>(); 
    
    /** Regex to validate the priority  of the pages for the digits. */
    private static final String REGEX = "^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$";

    /**
     *  activate method gets initiated when the page is called which uses
     *  /apps/dotcom/components/page/sitemapindextemplate pages template   
     *  @param  properties object
     *  @return Nothing. 
     */
    @Activate
    protected void activate(Map<String, Object> properties) {    	
    	LOGGER.debug("SiteMapIndex is called to get all the LOB pages");
        this.externalizerDomain = PropertiesUtil.toString(properties.get(PROP_EXTERNALIZER_DOMAIN),DEFAULT_EXTERNALIZER_DOMAIN);
        this.includeLastModified = PropertiesUtil.toBoolean(properties.get(PROP_INCLUDE_LAST_MODIFIED),DEFAULT_INCLUDE_LAST_MODIFIED);       
        this.characterEncoding = PropertiesUtil.toString(properties.get(PROP_CHARACTER_ENCODING_PROPERTY),null);
        this.lobTypes = dotcomService.getPropertyArray(PROP_LOB_TYPES);
    }
    /**
     *  doGet method gets called when the requests originates from
     *  /apps/dotcom/components/page/sitemapindextemplate pages    
     *  @param  request object
     *  @param  response object
     *  @return Nothing. 
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        response.setContentType(request.getResponseContentType());
        if (characterEncoding != null) {
            response.setCharacterEncoding(characterEncoding);
        }
        ResourceResolver resourceResolver = request.getResourceResolver();        
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
        try {
            XMLStreamWriter stream = outputFactory.createXMLStreamWriter(response.getWriter());
            stream.writeStartDocument("1.0");
            stream.writeStartElement("", "sitemapindex", NAMESPACE);
            stream.writeNamespace("", NAMESPACE);                      
            for (int i = 0; i < lobTypes.size(); i++) {
            	LOGGER.debug("Lob type value from prop file is"+lobTypes.get(i));
            	write(lobTypes.get(i), stream ,resourceResolver);
    		}           
            writeSiteMapElement(priorityLOBOrderHashMap,stream);
            stream.writeEndElement();
            stream.writeEndDocument();
            priorityLOBOrderHashMap.clear();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }
	/**
     * write method writes the path of the LOB's to the XML file.
     * 
     *  @param  path the currentNode in the Component
     *  @param  stream XMLStreamWriter object
     *  @param  resolver resource resolver object    
     *  @return Nothing. 
     */
    private void write(String path, XMLStreamWriter stream,ResourceResolver resolver) throws XMLStreamException {               		               
        PageManager pageManager = resolver.adaptTo(PageManager.class);                 
        Page page = pageManager.getPage(path) ;
        LOGGER.debug("Page Path $$$$"+path);               
        if (page == null || page.getContentResource() == null) {  
            return; 
        }        		
        float priorityKeyValue = 0.0f; 
        Resource resource = page.getContentResource();		        
		ValueMap valueMap = resource.adaptTo(ValueMap.class);  		
		if(!valueMap.isEmpty()){
	        for(String key : valueMap.keySet()) {
	            if(key.equals("priority")){            	            	            	
		        	Pattern pattern = Pattern.compile(REGEX);	        	
		        	if(pattern.matcher(valueMap.get(key, String.class)).matches()){
		        		priorityKeyValue =  Float.parseFloat(valueMap.get(key, String.class));  
		            	LOGGER.debug("Map values are $$$$"+priorityKeyValue);
		        	}	   
	            }                        
	        }    
		}
        String canonicalpath = Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),path,resolver);	         
    	String loc = externalizer.externalLink(resolver, externalizerDomain, canonicalpath + ".index.xml");
    	String Date ="";
    	if (includeLastModified) {
		    Calendar cal = page.getLastModified();
		    if (cal != null) {
		        Date = DATE_FORMAT.format(cal);
		    }
		}    	    	
    	String values = loc+";"+Date;              
        priorityLOBOrderHashMap.put(values,priorityKeyValue);         
    }
    /**
     * sortByValues method sorts the map based on the values.
     * 
     *  @param  map Map that needs to be sorted
     *  @return the sortedMap sorted based on the values. 
     */   
   private static <K, V extends Comparable<V>> Map<K, V> 
    sortByValues(final Map<K, V> map) {
    Comparator<K> valueComparator = 
             new Comparator<K>() { 
      public int compare(K key1, K key2) {
        int compare = 
              map.get(key1).compareTo(map.get(key2));
        if (compare == 0) 
          return 1;
        else 
          return compare; 
      }
    };
 
    Map<K, V> sortedByValues = 
      new TreeMap<K, V>(valueComparator);
    sortedByValues.putAll(map);
    return sortedByValues;
  } 
   
   /**
    * writeSiteMapElement method creates the elements for each LOB in the XML file.
    * 
    *  @param  stream XMLStreamWriter object
    *  @param  priorityLOBOrderHashMap contains the date and the canonical URL
    *  @return Nothing. 
    */
   private void writeSiteMapElement(Map<String, Float> priorityLOBOrderHashMap,XMLStreamWriter stream) throws XMLStreamException { 	  			
		Map<String, Float> treeMap = new TreeMap <String, Float>(priorityLOBOrderHashMap);				
		Map<String, Float> sortedMap = sortByValues(treeMap);  	  	   
	    Set<Entry<String, Float>> set = ((TreeMap<String, Float>) sortedMap).descendingMap().entrySet();     
	    Iterator<Entry<String, Float>> i = set.iterator(); 
	    while(i.hasNext()) {  
	    	Map.Entry mapEntry = (Map.Entry)i.next();	       
	      	LOGGER.debug("Key : " + mapEntry.getKey() + " Value : " + mapEntry.getValue());  
			stream.writeStartElement(NAMESPACE, "sitemap");
		    String loc = mapEntry.getKey().toString().split(";")[0]; 
		    String Date = mapEntry.getKey().toString().split(";")[1];  
		    writeElement(stream, "loc", loc);
		    writeElement(stream, "lastmod", Date); 	    	
	    	stream.writeEndElement();	     
	    }																			
   } 
    
    /**
     * writeElement method creates the elements for each LOB in the XML file.
     * 
     *  @param  stream XMLStreamWriter object
     *  @param  elementName element name to be written
     *  @param  text the element text that should be written
     *  @return Nothing. 
     */
    private void writeElement(final XMLStreamWriter stream, final String elementName, final String text) throws XMLStreamException { 
        stream.writeStartElement(NAMESPACE, elementName);
        stream.writeCharacters(text);
        stream.writeEndElement();
    }

}