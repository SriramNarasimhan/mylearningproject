/**
 * 
 */
package com.first.myproject.core;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobProcessor;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationAction;

/**
 * @author Welcome
 * 
 */
@Service(value = EventHandler.class)
@Component(metatype = false, immediate = true)
@Property(name = "event.topics", value = ReplicationAction.EVENT_TOPIC)
public class MyEventListener implements JobProcessor, EventHandler {

	/** Default log. */
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	// Inject a Sling ResourceResolverFactory
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	public void handleEvent(Event event) {
		logger.info("Handling event++++++++++++++++++++++++++++");
		process(event);

	}
	
	public boolean process(Event arg0) {
		logger.info("Job processing++++++++++++++++++++++++++++");
		return true;
	}

}
