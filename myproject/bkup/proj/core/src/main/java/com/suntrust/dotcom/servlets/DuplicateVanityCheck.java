package com.suntrust.dotcom.servlets;


import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.suntrust.dotcom.services.SearchService;
import com.suntrust.dotcom.utils.VanityConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class used by the vanity URL check validation javascript. It determines if the vanity URL entered by the user is
 * already in use already and returns the list of pages in which the vanity path is used.
 */

@SuppressWarnings("serial")
@SlingServlet(
        metatype = true,
        label = "SunTrust - Duplicate Vanity Check Servlet - Sling Safe Methods Servlet",
        description = "Implementation of Duplicate Vanity Check Servlet.",
        paths = { "/bin/wcm/duplicateVanityCheckST" },
        methods = { "GET" }
)
public final class DuplicateVanityCheck extends SlingSafeMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(DuplicateVanityCheck.class);
   
    @Reference
    SearchService searchService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        try {

            final ResourceResolver resolver = request.getResourceResolver();
            final String vanityPath = request.getParameter("vanityPath");
            final String pagePath = request.getParameter("pagePath");
            log.info("vanity path parameter passed is {}; page path parameter passed is {}", vanityPath, pagePath);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            JSONWriter jsonWriter = new JSONWriter(response.getWriter());
            jsonWriter.array();

            //perform search
            if (StringUtils.isNotBlank(vanityPath)) {
                int pageDepth = Integer.valueOf(depth);
                String searchPath = getSearchPath(resolver, pagePath, pageDepth);
                List<Resource> hits = searchService.getSearchResultsAsResources(resolver, getVanityPathSearch(vanityPath, searchPath, "10"));
                if(hits != null && hits.size() >0){
                    log.info(" no of hits {}", hits.size());
                    for( Resource hit : hits){
                       // log.debug(" hit {}", hit.getPath());
                        String path = hit.getPath();
                        //exclude the page itself
                        if (path.startsWith("/content") && !path.equals(pagePath)) {
                            jsonWriter.value(path);
                        }
                    }
                }
            }
            jsonWriter.endArray();
        } catch (JSONException e) {
            throw new ServletException("Unable to generate JSON result", e);
        }
    }

    @Property(label = "configure.depth", description = " depth of the domain path ex: /content/suntrust/dotcom/us/en is depth 4 ")
    public static final String DEPTH = "configure.depth";
    private String depth;

    @Activate
    protected void activate(final Map<String, Object> config) {
        configure(config);
    }

    private void configure(final Map<String, Object> config) {
        this.depth = PropertiesUtil.toString(config.get(DEPTH), "1");
        log.debug("configure: depth='{}''", this.depth);
    }

    public static String getSearchPath(ResourceResolver resolver, String currentPath, int depth){
        String path  = "/content";
        log.info("page path parameter passed is {}; page depth parameter passed is {}", currentPath, depth);
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        Page currentPage = pageManager.getContainingPage(currentPath);
        Page searchRoot = currentPage.getAbsoluteParent(depth);
        if(searchRoot != null){
            path = searchRoot.getPath();
        }
        log.info("vanity search path = {}", path);
        return path;
    }


    public static Map<String, String> getVanityPathSearch(String vanityPath, String path, String limit){

        Map<String, String> map = new HashMap<String, String>();
        map.put("type", "cq:PageContent");

        if (StringUtils.isNotBlank(path)) {
            map.put("path", path);
        }

        // make oak index for the custom field
        map.put("group.1_property", VanityConstants.VANITY_URL_FIELD);
        map.put("group.1_property.value", vanityPath);

        // redirect 301
        map.put("group.2_property", VanityConstants.REDIRECTS_TYPE);
        map.put("group.2_property.value", vanityPath);

        //redirect 302
        //map.put("group.3_property", VanityConstants.REDIRECTS_TYPE_302);
        //map.put("group.3_property.value", vanityPath);

        map.put("group.p.or","true");

        // limit results
        if (StringUtils.isNotBlank(limit)){
            map.put("p.limit",limit);
        }
        map.put("p.guessTotal","true");

        log.info("Predicates {} ", map.toString());

        return map;
    }

}
