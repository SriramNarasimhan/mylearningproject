package com.suntrust.dotcom.services.impl;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.suntrust.dotcom.services.SearchService;
import com.suntrust.dotcom.services.VanityReportService;
import com.suntrust.dotcom.utils.VanityConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(
        label = "SunTrust - Vanity Report Service",
        description = "Service to generate SEO URL Reports.",
        metatype = true,
        immediate = false)
@Service
public class VanityReportServiceImpl implements VanityReportService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private SearchService searchService;


    public Map<String, String> getSEOUrlMap(ResourceResolver resolver, String rootpath, String propertyName){

        Map<String, String> urlMatrix = new HashMap<String, String>();
        //PageManager pageManager = resolver.adaptTo(PageManager.class);

        log.info(" Report Service : START");
        if(StringUtils.isNotBlank(rootpath) && StringUtils.isNotBlank(propertyName) ){

            Map<String, String> predicates = getSEOPredicates(rootpath, "-1", VanityConstants.PROPERTY_PREFIX + propertyName);
            log.info(" Predicates {}", predicates.toString());
            List<Resource> results = getResults(resolver, searchService, predicates );
            if(results != null){
                log.info(" Number of Results = ", results.size());
                for( Resource pageRes: results){
                    Page page = pageRes.adaptTo(Page.class);
                    log.info(" page {}", page.getPath() );
                    if(page != null){
                        String[] values = PropertiesUtil.toStringArray(page.getProperties().get(propertyName, String[].class));
                        if(values != null && values.length >0){
                            for( String seoUrl: values){
                                urlMatrix.put(page.getPath(), seoUrl);
                            }
                        }
                    }
                }
            }

        }

        return urlMatrix;
    }

    /**
     * Search using Search service module, returns the list of hits as resources
     * @param resourceResolver
     * @param searchService
     * @param predicates
     * @return resources
     */
    public static List<Resource> getResults(ResourceResolver resourceResolver, SearchService searchService, Map<String, String> predicates){

        List<Resource> resources = null;
        if (resourceResolver != null && searchService != null && predicates != null){
            resources = searchService.getSearchResultsAsResources(resourceResolver, predicates);
        }
        return resources;
    }

    /**
     * Generate the predicate map for searching all the SEO paths
     * @param path
     * @param limit
     * @return map
     */
    private static Map<String, String> getSEOPredicates(String path, String limit, String propertyName){

        Map<String, String> map = new HashMap<String, String>();
        map.put("type", "cq:Page");

        if (StringUtils.isNotBlank(path)) {
            map.put("path", path);
        }
        // ensure the property is indexed
        if (StringUtils.isNotBlank(propertyName)){
            map.put("property", propertyName);
            map.put("property.operation", "exists");
        }

        // limit results
        if (StringUtils.isNotBlank(limit)){
            map.put("p.limit",limit);
        }
        map.put("p.guessTotal","true");

        // order by last modified
        map.put("orderby", VanityConstants.JCR_CONTENT_CQ_LAST_MODIFIED);
        map.put("orderby.sort", "desc");

        return map;
    }


}
