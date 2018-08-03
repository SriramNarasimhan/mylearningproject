package com.suntrust.dotcom.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
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
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.Externalizer;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.api.RenditionPicker;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.text.Text;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.Utils;


@Component(metatype = true, label = "Dotcom - Video Site Map Servlet", description = "Video Site Map Servlet", configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Service
@Properties({
        @Property(name = "sling.servlet.resourceTypes", unbounded = PropertyUnbounded.ARRAY, label = "Sling Resource Type", description = "Sling Resource Type for the Home Page Video Components."),
        @Property(name = "sling.servlet.selectors", value = "videos", propertyPrivate = true),
        @Property(name = "sling.servlet.extensions", value = "xml", propertyPrivate = true),
        @Property(name = "sling.servlet.methods", value = "GET", propertyPrivate = true),
        @Property(name = "webconsole.configurationFactory.nameHint", value = "Site Map for: {externalizer.domain}, on resource types: [{sling.servlet.resourceTypes}]") })
public class SiteMapVideoServlet extends SlingAllMethodsServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Logger log = LoggerFactory.getLogger(SiteMapVideoServlet.class);

	@Reference
	QueryBuilder queryBuilder;
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	@Reference
	private Externalizer externalizer;
	@Reference
	private SuntrustDotcomService dotcomService;
	
	private ResourceResolver resourceResolver;
	private Session session = null;
	private PageManager pageManager;
    private String externalizerDomain;
	
	private static final String SERVICE_ACCOUNT_IDENTIFIER = "dotcomreadservice";
	private static final String NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";
	private static final String VDO_NAMESPACE = "http://www.google.com/schemas/sitemap-video/1.1";
	
	private static final String VDO_PAGEPATH="pagePath";
	private static final String VDO_TITLE="vdoTitle";
	private static final String VDO_DESC="vdoDescription";
	private static final String VDO_DURATION="vdoDuration";
	private static final String VDO_THUMBNAIL="vdoThumbnail";
	private static final String VDO_PUBLICATIONDATE="vdoPublicationDate";
	
	/** EXTERNALIZER Domain author/publish. */
    private static final String DEFAULT_EXTERNALIZER_DOMAIN = "publish";
    
    /** Property of EXTERNALIZER Domain author/publish. */
    @Property(value = DEFAULT_EXTERNALIZER_DOMAIN, label = "Externalizer Domain", description = "Must correspond to a configuration of the Externalizer component.")
    private static final String PROP_EXTERNALIZER_DOMAIN = "externalizer.domain";
    
    
    
    @Activate
    protected void activate(Map<String, Object> properties) {    	
    	log.debug("SiteMap is called for the individual video asset pages");
		this.externalizerDomain = PropertiesUtil.toString(
				properties.get(PROP_EXTERNALIZER_DOMAIN),
				DEFAULT_EXTERNALIZER_DOMAIN);
    }
	
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		final Map<String, Object> authInfo = Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE,
                (Object) SERVICE_ACCOUNT_IDENTIFIER);
		
		response.setContentType(request.getResponseContentType());
            response.setCharacterEncoding("UTF-8");
        
		XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
		XMLStreamWriter stream =null;
		
		try {
			resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo);
			session = resourceResolver.adaptTo(Session.class);
	        pageManager =resourceResolver.adaptTo(PageManager.class);
	        
			List<Map<String,String>> resList = getVideoResourceDetails();
			
			stream = outputFactory.createXMLStreamWriter(response.getWriter());
			stream.writeStartDocument("UTF-8","1.0");
            stream.setPrefix("video", VDO_NAMESPACE);
            stream.writeStartElement("", "urlset", NAMESPACE);
            stream.writeNamespace("", NAMESPACE);
            stream.writeNamespace("video", VDO_NAMESPACE);
            
	            if(null!=resList && !resList.isEmpty()){
	            	for(Map<String,String> dataMap : resList){
	            		if(!dataMap.isEmpty())
	            			writeUrlElement(stream, dataMap);
	            	}
	            }
	            else
	            	stream.writeCharacters("No valid video resources mapped to pages");
	            
            stream.writeEndElement();
            stream.writeEndDocument();
		} catch (LoginException | XMLStreamException e) {
			log.error("Exception thrown while generating Video Site Map : ",e);
		}
        
	}
	
	
	private void writeUrlElement(final XMLStreamWriter stream, final Map<String, String> dataMap) throws XMLStreamException {
		if(!dataMap.isEmpty()){
			stream.writeStartElement(NAMESPACE, "url");
				writeLocElement(stream, dataMap);
				writeVdoElement(stream, dataMap);
			stream.writeEndElement();
		}
	}
	
	private void writeLocElement(final XMLStreamWriter stream, final Map<String, String> dataMap) throws XMLStreamException {
			stream.writeStartElement(NAMESPACE, "loc");
			if(dataMap.containsKey(VDO_PAGEPATH))
				stream.writeCharacters(dataMap.get(VDO_PAGEPATH));
			else
				stream.writeCharacters(StringUtils.EMPTY);
			
			stream.writeEndElement();
	}
	private void writeVdoElement(final XMLStreamWriter stream, final Map<String, String> dataMap) throws XMLStreamException {
			stream.writeStartElement(VDO_NAMESPACE, "video");
				writeVdoThumbnailElement(stream, dataMap);
				writeVdoTitleElement(stream, dataMap);
				writeVdoDescElement(stream, dataMap);
				writeVdoDurationElement(stream, dataMap);
				writeVdoPublishedDateElement(stream, dataMap);
			stream.writeEndElement();
	}
	
	private void writeVdoThumbnailElement(final XMLStreamWriter stream, final Map<String, String> dataMap) throws XMLStreamException {
		stream.writeStartElement(VDO_NAMESPACE, "thumbnail");
			if(dataMap.containsKey(VDO_THUMBNAIL))
				stream.writeCharacters(dataMap.get(VDO_THUMBNAIL));
			else
				stream.writeCharacters(StringUtils.EMPTY);
			
		stream.writeEndElement();
	}
	
	private void writeVdoTitleElement(final XMLStreamWriter stream, final Map<String, String> dataMap) throws XMLStreamException {
		stream.writeStartElement(VDO_NAMESPACE, "title");
			if(dataMap.containsKey(VDO_TITLE))
				stream.writeCharacters(dataMap.get(VDO_TITLE));
			else
				stream.writeCharacters(StringUtils.EMPTY);
			
		stream.writeEndElement();
	}
	
	private void writeVdoDescElement(final XMLStreamWriter stream, final Map<String, String> dataMap) throws XMLStreamException {
		stream.writeStartElement(VDO_NAMESPACE, "description");
			if(dataMap.containsKey(VDO_DESC))
				stream.writeCharacters(dataMap.get(VDO_DESC));
			else
				stream.writeCharacters(StringUtils.EMPTY);
			
		stream.writeEndElement();
	}
	
	private void writeVdoDurationElement(final XMLStreamWriter stream, final Map<String, String> dataMap) throws XMLStreamException {
		stream.writeStartElement(VDO_NAMESPACE, "duration");
			if(dataMap.containsKey(VDO_DURATION))
				stream.writeCharacters(dataMap.get(VDO_DURATION));
			else
				stream.writeCharacters(StringUtils.EMPTY);
			
		stream.writeEndElement();
	}
	
	private void writeVdoPublishedDateElement(final XMLStreamWriter stream, final Map<String, String> dataMap) throws XMLStreamException {
		stream.writeStartElement(VDO_NAMESPACE, "publication_date");
			if(dataMap.containsKey(VDO_PUBLICATIONDATE))
				stream.writeCharacters(dataMap.get(VDO_PUBLICATIONDATE));
			else
				stream.writeCharacters(StringUtils.EMPTY);
			
		stream.writeEndElement();
	}
	
	private List<Map<String, String>> getVideoResourceDetails(){
		try{
		Map<String,String> predicateMap = new HashMap<>();
		Map<String, String> siteMapVars =null;
		List<Map<String, String>> resList = new ArrayList<>();
		  predicateMap.put("type", "nt:unstructured");
		  predicateMap.put("path", "/content/suntrust/dotcom/us");
		  predicateMap.put("1_property", "videoserverurl"); /*this property has to be indexed*/
		  predicateMap.put("1_property.operation", "exists");
		  predicateMap.put("p.limit", "-1");
		  
		  Query queryObj = this.queryBuilder.createQuery(PredicateGroup.create(predicateMap), session);
		  SearchResult result = queryObj.getResult();
		  Iterator<Resource> resources = result.getResources();
		  log.info("Search results for video assets : "+ result.getTotalMatches());
		  resList.clear();
		  while(resources.hasNext()){
			  Resource res =resources.next();
			  
			  Page page = pageManager.getContainingPage(res);
			  Resource videoNodeRes = res.getParent();

			  siteMapVars= new HashMap<>();
			  
			  ValueMap s7ResMap = res.adaptTo(ValueMap.class);
			  ValueMap vResMap = videoNodeRes.adaptTo(ValueMap.class);
			  boolean ishiddenExtSearch = false;
			  /* to be implemented later. 
			   * remove video pages if excluded from external search
			   * if(page.getProperties().containsKey("externalsearch"))
				  ishiddenExtSearch = Boolean.valueOf(page.getProperties().get("externalsearch").toString());*/
			  
			  
			  if(s7ResMap.containsKey("fileReference") && !ishiddenExtSearch){
				  log.info("Video Asset Reference Found :: "+ s7ResMap.get("fileReference", String.class));
				  String canonicalLocPath = Utils.getCanonicalUrl(dotcomService.getPropertyArray("canonical.urls"),page.getPath(),resourceResolver);
				  siteMapVars.put(VDO_PAGEPATH, externalizer.externalLink(resourceResolver, externalizerDomain, canonicalLocPath));
				  Resource vdoRes = resourceResolver.getResource(s7ResMap.get("fileReference", String.class));
				  if(null!=vdoRes){
					  log.info("Video Resource found in JCR");
					  Asset vdoAsset = vdoRes.adaptTo(Asset.class);
					  ValueMap vdoResMap = resourceResolver.getResource(vdoRes.getPath()+"/jcr:content").adaptTo(ValueMap.class);
					  ValueMap vdoResMetaMap = null;
					  if(null!=resourceResolver.getResource(vdoRes.getPath()+"/jcr:content/metadata"))
						  vdoResMetaMap = resourceResolver.getResource(vdoRes.getPath()+"/jcr:content/metadata").adaptTo(ValueMap.class);
					  
					  siteMapVars.put(VDO_THUMBNAIL, externalizer.externalLink(resourceResolver, externalizerDomain, 
									  vdoAsset.getRendition(new RenditionPicker() {
											
											@Override
											public Rendition getRendition(Asset asset) {
												List<Rendition> renditions = asset.getRenditions();
											      for (Rendition rendition: renditions) {
											          if (Text.getName(rendition.getPath()).startsWith("cq5dam.web.")) {
											              return rendition;
											          }
											      }
												return asset.getImagePreviewRendition();
											}
										}).getPath()
							  ));
					  
					  if(vResMap.containsKey("description")){
						  siteMapVars.put(VDO_DESC, vResMap.get("description", String.class));
					  }
					  else if(vResMap.containsKey("node_asset_data_description")){
						  siteMapVars.put(VDO_DESC, vResMap.get("node_asset_data_description", String.class));
					  }
					  if(vResMap.containsKey("title")){
						  siteMapVars.put(VDO_TITLE, vResMap.get("title", String.class));
					  }
					  else if(vResMap.containsKey("node_asset_data_title")){
						  siteMapVars.put(VDO_TITLE, vResMap.get("node_asset_data_title", String.class));
					  }
					  
						if (null != vdoAsset && null != vdoResMetaMap) {
							if (vdoResMetaMap.containsKey("videoDuration")) {
								siteMapVars.put(VDO_DURATION, vdoResMetaMap.get("videoDuration", String.class));
							} else if (vdoResMetaMap.containsKey("duration")) {
								siteMapVars.put(VDO_DURATION, vdoResMetaMap.get("duration", String.class));
							}
						}
					  if(vdoResMap.containsKey("cq:lastReplicated")){
						  siteMapVars.put(VDO_PUBLICATIONDATE, vdoResMap.get("cq:lastReplicated",String.class));
					  }
					  else if(vdoResMap.containsKey("jcr:lastModified")){
						  siteMapVars.put(VDO_PUBLICATIONDATE, vdoResMap.get("jcr:lastModified",String.class));
					  }
					  
					  resList.add(siteMapVars);
				  }
			  }
			  
			  
		  }
		  
		  return resList;
	}
		catch(Exception e){
			log.error("Exception Encountered with retrieving video asset pages",e);
			return null;
		}
	}
}
