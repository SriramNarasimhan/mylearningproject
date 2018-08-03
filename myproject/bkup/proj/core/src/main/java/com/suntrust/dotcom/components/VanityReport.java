package com.suntrust.dotcom.components;

import com.adobe.cq.sightly.WCMUsePojo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VanityReport extends WCMUsePojo {

    private static Logger log = LoggerFactory.getLogger(VanityReport.class);

    SlingScriptHelper ssh;

    private String rootpath;

    @Override
    public void activate() throws Exception {

        try {
            ssh = getSlingScriptHelper();
            initSelf();
            // Convenience methods allow to access the default bindings
            Resource resource = getResource();

            // Parameters are can be accessed via a get() method
            String param1 = get("param1", String.class);

            rootpath = 	get("path", String.class);

            String hasVanity = 	get("hasVanity", String.class);
            String has301 =     get("has301", String.class);
            String has302 = 	get("has302", String.class);





        } catch (Exception e) {
            log.info(e.toString(), e);

        }

    }

    public void initSelf() throws Exception {

    }

}
