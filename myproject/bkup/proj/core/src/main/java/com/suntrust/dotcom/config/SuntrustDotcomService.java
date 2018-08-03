package com.suntrust.dotcom.config;

import java.util.Dictionary;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;

import com.suntrust.dotcom.utils.Utils;

/**
 * Implementation of ConfigService provides OsgiConfig parameters.
 */
@Service(value = { SuntrustDotcomService.class })

@Component(immediate = true, metatype = true)
@Properties({@Property(name = "maintenance.mode.ocm", value = "online"),
			@Property(name = "maintenance.mode.olb", value = "online"),
			@Property(name = "speed.bump.page.url", value = "/content/suntrust/dotcom/us/configuration/speedbump.html"),
			@Property(name = "white.listed.document.url", value = "/content/dam/dotcom/en-us/suntrust/configuration/whitelistedurls.txt"),
			@Property(name = "live.engage.file.path", value = "/content/dam/suntrust/us/en/internal-applications/live-engage-chat/liveengage.txt"),
			@Property(name = "audio.disclaimer.content.path", value = "/content/dam/en_us/suntrust/personal-banking/shared/disclaimers/audiodisclaimer"),
			@Property(name = "video.disclaimer.content.path", value = "/content/dam/en_us/suntrust/personal-banking/shared/disclaimers/disclaimercontent"),
			@Property(name = "newaccountstart.content.path", value = "/new-account-start")
})  
		

public class SuntrustDotcomService {

    private Dictionary<String, Object> properties;

    @SuppressWarnings("unchecked")
    protected void activate(ComponentContext context) {
        properties = context.getProperties();
    }

    /**
     * Sets property as null
     * 
     * @param context
     */
    protected void deactivate(ComponentContext context) {
        properties = null;
    }

    /**
     * Return key value
     * 
     * @param key
     * @return String
     */
    public String getPropertyValue(String key) {
        return Utils.getPropertyValue(key,properties);
    }

    /**
     * Returns values as ArrayList for given key
     * 
     * @param key
     * @return ArrayList
     */
    public List<String> getPropertyArray(String key) {
        return Utils.getPropertyArray(key,properties);
    }
}