package com.first.myproject.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
//import com.day.cq.commons.TidyJSONWriter;
//import org.apache.sling.commons.json; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
//import com.day.cq.dam.api.Asset;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@SlingServlet(paths = "/bin/imagereader", methods = "GET")
public class ImageReaderServlet extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(ImageReaderServlet.class);

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String json = "";
		MultiValueMap map = new MultiValueMap();
		PrintWriter out = response.getWriter();
		ObjectMapper mapper = new ObjectMapper();
		// to enable standard indentation ("pretty-printing"):
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		try {
			String id = request.getParameter("dampath");

			ResourceResolver resolver = request.getResourceResolver();
			Resource damRootResource = resolver.getResource(id);
			log.info("damRootResource "+damRootResource.getPath());
			Iterator<Resource> assetList = resolver
					.listChildren(damRootResource);
			while (assetList.hasNext()) {
				Resource child = assetList.next();
				log.info("in while");
				Node childNode = child.adaptTo(Node.class);
				
				if(childNode.getPrimaryNodeType().getName().equals("dam:Asset")){
					log.info("asset");
					Asset asset = child.adaptTo(Asset.class);
					if (asset.getMimeType().contains("image")) {
						map.put("image", child.getPath());
						map.putAll(asset.getMetadata());
					}
				}
				else if(childNode.getPrimaryNodeType().getName().toLowerCase().contains("folder")){
					getImagesRecursively(child, resolver, map);
					log.info("folder");
					
				}
			}
			// convert map to JSON string
			json = mapper.writeValueAsString(map);
			log.info("json string"+json.toString());
			out.println(json.toString());
		} catch (Exception e) {
			log.debug("exception in Image servlet ==" + e.getMessage());
		}
	}
	
	private void getImagesRecursively(Resource child, ResourceResolver resolver, MultiValueMap map){
		Iterator<Resource> childAssetList = resolver
				.listChildren(child);
		while (childAssetList.hasNext()) {
			Resource damResource = childAssetList.next();
			log.info("damResource "+damResource.getPath());
			Node assetNode = damResource.adaptTo(Node.class);
			try {
				if (assetNode.getPrimaryNodeType().getName()
						.equals("dam:Asset")) {
					Asset childAsset = damResource.adaptTo(Asset.class);
					if (childAsset.getMimeType().contains("image")) {
						map.put("image", childAsset.getPath());
						map.putAll(childAsset.getMetadata());
					}
				}
				else if(assetNode.getPrimaryNodeType().getName().toLowerCase().contains("folder")){
					getImagesRecursively(damResource, resolver, map);
					log.info("folder");
					
				} 
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}