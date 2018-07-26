package com.first.myproject.services.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This service executes scheduled jobs
 * 
 */
@Component//(immediate = true)
// @Service(value = Runnable.class)
@Property(name = "scheduler.expression")
public class HelloWorldScheduledServiceImpl {

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/** The scheduler for rescheduling jobs. */
	@Reference
	private Scheduler scheduler;

	protected void activate(ComponentContext componentContext) throws Exception {
		/*// case 1: with addJob() method: executes the job every minute
		String schedulingExpression = "0 * * * * ?";
		String jobName1 = "case1";
		Map<String, Serializable> config1 = new HashMap<String, Serializable>();
		boolean canRunConcurrently = true;
		final Runnable job1 = new Runnable() {
			public void run() {
				log.info("Executing job1");
			}
		};
		try {
			this.scheduler.addJob(jobName1, job1, config1,
					schedulingExpression, canRunConcurrently);
		} catch (Exception e) {
			job1.run();
		}

		// case 2: with addPeriodicJob(): executes the job every 3 minutes
		String jobName2 = "case2";
		long period = 120;
		Map<String, Serializable> config2 = new HashMap<String, Serializable>();
		final Runnable job2 = new Runnable() {
			public void run() {
				log.info("Executing job2");
			}
		};
		try {
			this.scheduler.addPeriodicJob(jobName2, job2, config2, period,
					canRunConcurrently);
		} catch (Exception e) {
			job2.run();
		}

		// case 3: with fireJobAt(): executes the job at a specific date (date
		// of deployment + delay of 30 seconds)
		String jobName3 = "case3";
		final long delay = 30 * 10;
		final Date fireDate = new Date();
		fireDate.setTime(System.currentTimeMillis() + delay);
		Map<String, Serializable> config3 = new HashMap<String, Serializable>();
		final Runnable job3 = new Runnable() {
			public void run() {
				log.info(
						"Executing job3 at date: {} with a delay of: {} seconds",
						fireDate, delay / 1000);
			}
		};
		try {
			this.scheduler.fireJobAt(jobName3, job3, config3, fireDate);
		} catch (Exception e) {
			job3.run();
		}*/
	}

	protected void deactivate(ComponentContext componentContext) {
		log.info("Deactivated, goodbye!");
	}

}