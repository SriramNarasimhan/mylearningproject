package com.suntrust.dotcom.services.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.adobe.granite.asset.api.AssetManager;
import com.adobe.granite.asset.api.Asset;
import com.adobe.granite.asset.api.Rendition;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.suntrust.dotcom.services.PageThumbnailService;
import com.suntrust.dotcom.services.ServiceAgentService;

@Component
@Service
public class PageThumbnailServiceImpl implements PageThumbnailService {
	Logger logger = LoggerFactory.getLogger(PageThumbnailServiceImpl.class);

	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	@Reference
	private ServiceAgentService serviceAgent;

	public static final String IMAGE = "image";
	public static final String FILE = "file";
	public static final String DAM_THUMBNAIL_NAME = "dam:thumbnail";
	public static final String DAM_THUMBNAIL_FOLDER_NAME = "dam:thumbnails";
	public static final String HTML_EXTN = ".html";
	Session session = null;

	@SuppressWarnings({ "unused" })
	public String getPageThumbnail(String pagePath) throws RepositoryException {
		HashMap<String, Integer> itemType = new HashMap<String, Integer>();

		Node fileNode = null;
		Node fileJCRNode = null;
		Node damThumbnailNode = null;
		String imagePath = "";
		ResourceResolver resourceResolver = null;
		resourceResolver = getResolver(resourceResolver);

		final PageManager pm = resourceResolver.adaptTo(PageManager.class);
		final Page page = pm.getPage(pagePath);

		if (page != null && page.getContentResource() != null) {
			try {
				if(page.getContentResource(IMAGE) != null){
					Node img = (Node) page.getContentResource(IMAGE).adaptTo(Node.class);
					if(img != null)
					{
						if(img.hasProperty("fileReference"))
						{
							imagePath=img.getProperty("fileReference").getString();
							final AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
							if(assetManager.assetExists(imagePath))
							{
								Asset asset = assetManager.getAsset(imagePath);
								Rendition rendition =asset.getRendition("cq5dam.thumbnail.319.319.png");//if this rendition is present its path will be returned else original image path will be returned
								if(rendition != null)
								{
									return  rendition.getPath();
								}
	
							}
						}
					}
				}

			} catch (Exception e) {
				logger.error("Exception, {}", e.getMessage());
			}

		} else {
			logger.info("Page is null");
		}

		if (resourceResolver != null) {
			serviceAgent.release(resourceResolver);
		}
		return imagePath;
	}

	/**
	 * @param resourceResolver
	 * @return
	 */
	private ResourceResolver getResolver(ResourceResolver resourceResolver) {
		try {
			resourceResolver = serviceAgent.getServiceResourceResolver("dotcomreadservice");
		} catch (Exception e) {
			logger.error("Exception, {}", e.getMessage());
		}
		return resourceResolver;
	}

	public String getThumbnail(HashMap<String, Integer> itemType, Node fileJCRNode) throws PathNotFoundException, RepositoryException, NumberFormatException {
		Node damThumbnailNode = fileJCRNode.getNode(DAM_THUMBNAIL_FOLDER_NAME);
		NodeIterator nodes = damThumbnailNode.getNodes();
		String path = null;
		int minSize = 0;
		int maxSize = 0;
		while (nodes.hasNext()) {
			Node node = nodes.nextNode();
			String patternString = DAM_THUMBNAIL_NAME + "_(\\d+).*";
			Pattern pattern = Pattern.compile(patternString);
			Matcher matcher = pattern.matcher(node.getName());
			if (matcher.find()) {
				maxSize = Integer.parseInt(matcher.group(1));
				if (minSize > maxSize) {
				} else {
					path = node.getPath();
					minSize = maxSize;
				}

			}
		}
		return path;
	}

	@Override
	public String getPageLink(String pagePath) throws RepositoryException {
		ResourceResolver resourceResolver = null;
		resourceResolver = getResolver(resourceResolver);
		final PageManager pm = resourceResolver.adaptTo(PageManager.class);
		final Page page = pm.getPage(pagePath);

		if (page != null && page.getContentResource() != null) {
			pagePath = page.getPath() + HTML_EXTN;
		} else {
			logger.info("Page is null");
		}

		if (resourceResolver != null) {
			serviceAgent.release(resourceResolver);
		}
		return pagePath;
	}

	public List<Map<String, Object>> getDetails(String pagePath, String pageParam) throws RepositoryException {
		List<Map<String, Object>> pageDetails = new LinkedList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		String image = getPageThumbnail(pagePath);
		String page = getPageLink(pagePath);
		if (pageParam != null) {
			pageParam = pageParam.replace("?", "");
			page = page + "?" + pageParam;
		} else {
			logger.info("Param is null");
		}
		map.put("thumbnail", image);
		map.put("pagelink", page);
		pageDetails.add(map);
		return pageDetails;
	}

}
