package com.first.myproject.sightly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.first.myproject.sightly.DisplayPage;

/**
 * SearchPagesService Component
 */
@Component(immediate = true)
@Service(SearchPagesService.class)
public class SearchPagesService {

	private Session session;

	@Reference
	private QueryBuilder builder;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public Iterator<Map<String, String>> getPagessList(ResourceResolver resolver, String noOfPages) {
		final List<Map<String, String>> hits = new ArrayList<Map<String, String>>();

		session = resolver.adaptTo(Session.class);
		logger.info("session" + session);

		Map<String, String> map = new HashMap<String, String>();
		map.put("path", "/content");
		map.put("type", "cq:Page");
		map.put("property", "jcr:content/cq:template");
		map.put("property.value", "/apps/sightly/templates/templateSightly");

		map.put("p.offset", "0");
		map.put("p.limit", "-1");
		map.put("orderby", "@jcr:content/jcr:title");
		map.put("orderby.sort", "asc");
		logger.info("map" + map);

		Query query = builder.createQuery(PredicateGroup.create(map), session);
		query.setStart(0);
		query.setHitsPerPage(Integer.parseInt(noOfPages));

		DisplayPage displayPage = new DisplayPage();

		SearchResult result = query.getResult();

		int hitsPerPage = result.getHits().size();
		logger.info("hitsPerPage" + hitsPerPage);
		long totalMatches = result.getTotalMatches();
		logger.info("totalMatches" + totalMatches);
		long offset = result.getStartIndex();
		logger.info("offset" + offset);
		long numberOfPages = totalMatches / 20;
		logger.info("numberOfPages" + numberOfPages);

		for (Hit hit : result.getHits()) {
			try {
				Page page = hit.getResource().adaptTo(Page.class);
				displayPage.setPageName(page.getName());
				displayPage.setpageTitle(page.getTitle());
				logger.info("page.getTitle()" + page.getTitle());
				displayPage.setPageLink(page.getPath());
				Map<String, String> pageMap = new HashMap<String, String>();
				pageMap.put("pageName", displayPage.getPageName());
				pageMap.put("pageTitle", displayPage.getPageTitle());
				pageMap.put("pageLink", displayPage.getPageLink());
				logger.info("pageItem ::::" + page.getPath());
				hits.add(pageMap);
			} catch (RepositoryException e) {
				logger.error("exception");
			}
		}

		return hits.iterator();

	}

}