/**
 * 
 */
package com.suntrust.dotcom.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @author 609930
 *
 */
public class BaseUsePojo extends WCMUsePojo {

	ObjectMapper mapper = new ObjectMapper();
	private final Logger log = LoggerFactory.getLogger(getClass());

	/** ResourceResolverFactory class reference variable */
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	/** ResourceResolver class reference variable */
	private ResourceResolver resourceResolver=null;
	
	/** Session class reference variable */
	private Session session=null;
	
	/**
	 * @param propName
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws RepositoryException
	 */
	public <T> T populateBean(String propName, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IllegalStateException, IOException, RepositoryException {
		return mapper.readValue(readJSONValue(propName), clazz);

	}

	public <T> List<T> populateBeanList(String propName, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IllegalStateException, IOException, RepositoryException {
		log.debug("class name"+clazz.getName());
		return mapper.readValue(readJSONValue(propName),
				TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, clazz));

	}
	
	public <T> List<T> populateBeanList(String nodePath, String propName, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IllegalStateException, IOException, RepositoryException, LoginException {
		log.debug("class name"+clazz.getName());
		return mapper.readValue(readJSONValue(nodePath, propName),
				TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, clazz));

	}

	private <T> String readJSONValue(String propName)
			throws RepositoryException, ValueFormatException, IOException, JsonParseException, JsonMappingException {
		Value[] values = new Value[] {};
		log.debug("property name"+propName);
		PropertyIterator propItr = getResource().adaptTo(Node.class).getProperties(propName);
		if (propItr.hasNext()) {
			Property prop = propItr.nextProperty();
			if (prop.isMultiple()) {
				values = prop.getValues();
			} else {
				values = (new Value[] { prop.getValue() });
			}
		}
		
		log.debug("complete json property value"+Arrays.toString(values));
		return Arrays.toString(values).replaceAll("@TypeHint", "");
	}
	
	private <T> String readJSONValue(String nodePath, String propName)
			throws RepositoryException, ValueFormatException, IOException, JsonParseException, JsonMappingException, LoginException {
		Value[] values = new Value[] {};
		log.debug("property name"+propName);
		PropertyIterator propItr = getResourceResolver().getResource(nodePath).adaptTo(Node.class).getProperties(propName);
		if (propItr.hasNext()) {
			Property prop = propItr.nextProperty();
			if (prop.isMultiple()) {
				values = prop.getValues();
			} else {
				values = (new Value[] { prop.getValue() });
			}
		}
		
		log.debug("complete json property value"+Arrays.toString(values));
		return Arrays.toString(values).replaceAll("@TypeHint", "");
	}

	@Override
	public void activate() throws Exception {
		
	}

}
