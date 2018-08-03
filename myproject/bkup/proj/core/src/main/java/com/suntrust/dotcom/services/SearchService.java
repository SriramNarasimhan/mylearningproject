package com.suntrust.dotcom.services;

import com.day.cq.search.result.Hit;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
* SearchService used in search.
* @author Cognizant
* @version 1.0
* @since 10 July 2017
* 
*/
public interface SearchService {

    /**
     * Returns a list of search result hits for a search query using query builder.
     * @param resourceResolver : user resource resolver object.
     * @param predicateMap : predicates map for query builder
     * @param hitsPerPage : no of hits per page
     * @return hitsList : List of results as hits
     */
    public List<Hit>  getSearchResults(final ResourceResolver resourceResolver , Map<String, String> predicateMap, long hitsPerPage);

    /**
     * Returns a list of search result hits as resources for a search query using query builder. (hits per page is default)
     * @param resourceResolver : user resource resolver object.
     * @param predicateMap : predicates map for query builder
     * @return resourceList : list of search results as resource.
     */
    public List<Resource> getSearchResultsAsResources(final ResourceResolver resourceResolver , Map<String, String> predicateMap);

    /**
     * Returns a list of search result hits as resources for a search query using query builder. (hits per page is default)
     * @param resourceResolver : user resource resolver object.
     * @param predicateMap : predicates map for query builder.
     * @param hitsPerPage : no of hits per page.
     * @return resourceList : list of search results as resource.
     */
    public List<Resource> getSearchResultsAsResources(final ResourceResolver resourceResolver , Map<String, String> predicateMap, long hitsPerPage);

    /**
     * Returns an Iterator of search results as a resource.
     * @param resourceResolver : user resource resolver object.
     * @param predicateMap : predicates map for query builder.
     * @return resources : list of search results as resource.
     */
    public Iterator<Resource> getSearchResultsAsResourcesIterator(final ResourceResolver resourceResolver ,
                                                                  Map<String, String> predicateMap);

}
