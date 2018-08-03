package com.suntrust.dotcom.services.impl;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.services.TypeAheadSearchService;

@Service(value={TypeAheadSearchService.class})
@Component(
		label="Suntrust Type Ahead Search Config",
		description="Suntrust Type Ahead Search Config",
		metatype=true,
		immediate=true
		)
public class TypeAheadSearchServiceImpl implements TypeAheadSearchService{

	private static final Logger LOGGER = LoggerFactory.getLogger(TypeAheadSearchServiceImpl.class);
	
	@Property(
			label = "Type Ahead Dictionary Search Path",
			description = "Enter the root path from where type ahead will read keywords." 
			)
	private static final String TYPEAHEAD_SEARCH_PATH="typeahead.search.path";

	@Property(
			label = "Type Ahead Dictionary DAM Path",
			description = "Enter the dam path from where type ahead will read txt file and add to existing keywords." 
			)
	private static final String TYPEAHEAD_FILE_PATH="typeahead.dam.file.path";

	@Property(
			unbounded=PropertyUnbounded.ARRAY,
			cardinality=Integer.MAX_VALUE,
			label = "DAM File Names Update",
			description = "Enter txt file names, along with category, separated with :" 
			)
	private static final String TYPEAHEAD_DAM_FILE_UPDATE="typeahead.dam.file.name.update";

	@Property(
			unbounded=PropertyUnbounded.ARRAY,
			cardinality=Integer.MAX_VALUE,
			label = "DAM File Names Delete",
			description = "Enter txt file names, along with category, separated with :" 
			)
	private static final String TYPEAHEAD_DAM_FILE_REMOVE="typeahead.dam.file.name.remove";
	
	
	private String dictContentPath;
	private String dictDamPath;
	private String[] filenameUpdate;
	private String[] filenameRemove;
	
	@Override
	public String getDictContentPath() {
		return dictContentPath;
	}
	@Override
	public String getDictDamPath() {
		return dictDamPath;
	}
	@Override
	public String[] getFilenameUpdate() {
		return filenameUpdate;
	}
	@Override
	public String[] getFilenameRemove() {
		return filenameRemove;
	}
	
	@Activate
	protected void activate(Map<String, Object> properties) {
		LOGGER.info("[*** TypeAheadSearchServiceImpl ConfigurationService]: activating configuration service");
		readProperties(properties);
	}
	
	protected void readProperties(Map<String, Object> properties) {

		LOGGER.info("OSGi properties set for Type Ahead Service==>" + properties.toString());

		this.dictContentPath = PropertiesUtil.toString(properties.get(TYPEAHEAD_SEARCH_PATH), "");
		this.dictDamPath = PropertiesUtil.toString(properties.get(TYPEAHEAD_FILE_PATH), "");
		this.filenameUpdate = PropertiesUtil.toStringArray(properties.get(TYPEAHEAD_DAM_FILE_UPDATE));
		this.filenameRemove = PropertiesUtil.toStringArray(properties.get(TYPEAHEAD_DAM_FILE_REMOVE));
	}
}
