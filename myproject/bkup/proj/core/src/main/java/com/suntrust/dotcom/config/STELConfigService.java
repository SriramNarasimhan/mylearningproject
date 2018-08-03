package com.suntrust.dotcom.config;

import java.util.Dictionary;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;

import com.suntrust.dotcom.utils.Utils;

/**
 * Implementation of ConfigService provides OsgiConfig parameters.
 */
@Service(value = { STELConfigService.class })
@Component(immediate = true, metatype = true)
public class STELConfigService {
	/** Dictionary call refrence variable */
	private Dictionary<String, Object> properties;

	@SuppressWarnings("unchecked")
	protected void activate(ComponentContext context) {
		properties = context.getProperties();
	}

	/**
	 * Sets property as null *
	 * 
	 * @param context
	 */
	protected void deactivate(ComponentContext context) {
		properties = null;
	}

	/**
	 * Return key value *
	 * 
	 * @param key
	 * @return String
	 */
	public String getPropertyValue(String key) {
		return Utils.getPropertyValue(key, properties);
	}

	/**
	 * Returns values as ArrayList for given key *
	 * 
	 * @param key
	 * @return ArrayList
	 */
	public List<String> getPropertyArray(String key) {
		return Utils.getPropertyArray(key, properties);
	}
}