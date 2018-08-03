package com.suntrust.dotcom.services.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.suntrust.dotcom.services.SearchService;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
* This SearchServiceImpl is used to search.
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
*/
@Component(
        label = "SunTrust - Search Service",
        description = "Service to perform searches.",
        metatype = true,
        immediate = false)
@Service
public class SearchServiceImpl implements SearchService {
	/**	Logger variable to log program state * */ 
	private static final Logger LOG = LoggerFactory.getLogger(SearchServiceImpl.class);
	/**	Service variable to create query * */
    @Reference
    QueryBuilder queryBuilder;

    /**
     * Returns a list of search result hits for a search query using query builder.
     * @param resourceResolver : user resource resolver object.
     * @param predicateMap : search predicates map for query builder
     * @param hitsPerPage : no of hits per page
     * @return hitsList : List of results as hits
     */
    public List<Hit>  getSearchResults(final ResourceResolver resourceResolver , Map<String, String> predicateMap, long hitsPerPage) {

        List<Hit> hitsList = null;
        Session session = resourceResolver.adaptTo(Session.class);

        Query queryObj = this.queryBuilder.createQuery(PredicateGroup.create(predicateMap), session);

        LOG.debug("Search Query" + queryObj.getPredicates().toString());
        if(hitsPerPage > 0) {
            queryObj.setHitsPerPage(hitsPerPage);
        }
        SearchResult searchResults = queryObj.getResult();

        if (searchResults != null) {
        	LOG.debug("Total number of matches is: " + searchResults.getTotalMatches());
            hitsList = searchResults.getHits();
        }
        return hitsList;
    }

    /**
     * Returns a list of search result hits as resources for a search query using query builder. (hits per page is default)
     * @param resourceResolver : user resource resolver object.
     * @param predicateMap : search predicates map for query builder
     * @return resourceList : list of search results as resource.
     */
    public List<Resource> getSearchResultsAsResources(final ResourceResolver resourceResolver , Map<String, String> predicateMap) {
        return getSearchResultsAsResources(resourceResolver, predicateMap, 0);
    }

    /**
     * Returns a list of search result hits as resources for a search query using query builder. (hits per page is default)
     * @param resourceResolver : user resource resolver object.
     * @param predicateMap : search predicates map for query builder.
     * @param hitsPerPage : no of hits per page.
     * @return resourceList : list of search results as resource.
     */
    public List<Resource> getSearchResultsAsResources(final ResourceResolver resourceResolver , Map<String, String> predicateMap, long hitsPerPage) {
        List<Resource> resourceList =  new ArrayList<Resource>();
        List<Hit> hitsList = getSearchResults(resourceResolver, predicateMap, hitsPerPage);
        if (hitsList != null && !hitsList.isEmpty()) {

            for (Hit hit : hitsList) {
                try {
                    Resource res = resourceResolver.getResource(hit.getPath());
                    resourceList.add(res);
                } catch (RepositoryException e) {
                	LOG.error("RepositoryException in SearchService#getSearchResultsAsResources()",e);
                }
            }
        }

        return  resourceList;
    }

    /**
     * Returns an Iterator of search results as a resource.
     * @param resourceResolver : user resource resolver object.
     * @param predicateMap : search predicates map for query builder.
     * @return resources : list of search results as resource.
     */
    public Iterator<Resource> getSearchResultsAsResourcesIterator(final ResourceResolver resourceResolver ,
                                                                  Map<String, String> predicateMap) {
        List<Resource> resourceList = getSearchResultsAsResources(resourceResolver, predicateMap);
        Iterator<Resource> resources = resourceList.iterator();
        return resources;
    }

}
