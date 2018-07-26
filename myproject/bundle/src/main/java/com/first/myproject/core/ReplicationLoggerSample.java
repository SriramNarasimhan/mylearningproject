/**
 * 
 */
package com.first.myproject.core;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobProcessor;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * @author Welcome
 * 
 */
@Service(value = EventHandler.class)
@Component(metatype = false, immediate = true)
@Property(name = "event.topics", value = ReplicationAction.EVENT_TOPIC)
public class ReplicationLoggerSample implements JobProcessor, EventHandler {

	/** Default log. */
	protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	// Inject a Sling ResourceResolverFactory
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	public void handleEvent(Event event) {
		LOGGER.info("Handling event++++++++++++++++++++++++++++");
		process(event);

	}
	
	public boolean process(Event event) {
		LOGGER.info("Job processing++++++++++++++++++++++++++++");
		ReplicationAction action = ReplicationAction.fromEvent(event);
		ResourceResolver resourceResolver = null;
		if(action.getType().equals(ReplicationActionType.ACTIVATE)) {
			try {
				resourceResolver = resolverFactory.getAdministrativeResourceResolver(null);
				final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
				final Page page = pageManager.getContainingPage(action.getPath());
				if (page != null){
					LOGGER.info("Page activation********"+ page.getTitle());
				}
			}
			catch (LoginException e) {
				e.printStackTrace();
			}
			finally {
				if(resourceResolver != null && resourceResolver.isLive()) {
					resourceResolver.close();
				}
			}
		}
		return true;
	}

}
