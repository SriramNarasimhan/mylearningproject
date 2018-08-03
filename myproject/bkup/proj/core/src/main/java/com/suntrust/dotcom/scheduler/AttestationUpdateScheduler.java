package com.suntrust.dotcom.scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;
import com.suntrust.dotcom.config.ContentExpiryConfigService;
import com.suntrust.dotcom.services.ServiceAgentService;

/**
 * AttestationUpdateScheduler- Scheduler to update Attestation details from csv
 * file
 */
@Component(immediate = true, metatype = true, label = "SunTrust-Attestation Page Update Scheduler")
@Service(value = Runnable.class)
@Properties({
		@Property(name = Constants.SERVICE_VENDOR, value = "Suntrust"),
		@Property(label = "Cron expression", description = "At 00:00 on day-of-month 1 in every 6th month.", name = "scheduler.expression", value = "0 0 1 */6 *") })
public class AttestationUpdateScheduler implements Runnable {

	private final Logger LOG = LoggerFactory
			.getLogger(AttestationUpdateScheduler.class);

	@Reference
	private ContentExpiryConfigService configService;
	@Reference
	private ServiceAgentService serviceAgent;

	private ResourceResolver resourceResolver;

	/**
	 * Run method call.
	 * 
	 */
	@Override
	public void run() {

		readAttestationCsvData();
	}

	private void readAttestationCsvData() {
		try {
			resourceResolver = serviceAgent
					.getServiceResourceResolver("dotcomreadservice");

			String attestationCsvPath = configService
					.getPropertyValue("suntrust.attestation.csv.path");

			if (!StringUtils.isEmpty(attestationCsvPath)) {

				Resource resource = resourceResolver
						.getResource(attestationCsvPath
								+ "/jcr:content/renditions/original/jcr:content");

				if (resource != null) {
					Node docNode = resource.adaptTo(Node.class);
					InputStream inputStream = docNode.getProperty("jcr:data")
							.getBinary().getStream();

					CsvReader csvReader = new CsvReader(inputStream, ',',
							StandardCharsets.UTF_8);
					csvReader.readHeaders();

					while (csvReader.readRecord()) {
						updateAttestationPage(csvReader, resourceResolver);
					}
				}
			}
		} catch (RepositoryException | IOException
				| org.apache.sling.api.resource.LoginException e1) {
			LOG.error("Error occur while reading CSV attestation data:: Trace:"
					+ e1.getMessage());
		}

	}

	private void updateAttestationPage(CsvReader csvReader,
			ResourceResolver resourceResolver) {

		try {
			String pagePath = csvReader.get("attestationPath");

			if (!StringUtils.isEmpty(pagePath)) {
				final URI gotoUri = new URI(pagePath);

				Resource nodeResource = resourceResolver.resolve(gotoUri
						.getPath());
				if (nodeResource != null) {
					Resource jcrResource = resourceResolver
							.resolve(nodeResource.getPath() + "/jcr:content");
					if (jcrResource != null) {
						Node jcrNode = jcrResource.adaptTo(Node.class);
						jcrNode.setProperty("attestationowner",
								csvReader.get("attestationOwner"));
						jcrNode.setProperty("attestationdate",
								getUserLoginDate(csvReader
										.get("attestationDate")));
						jcrNode.setProperty("attestationReviewer",
								csvReader.get("attestationReviewer"));
						resourceResolver.commit();
					}
				}
			}
		} catch (IOException | RepositoryException | URISyntaxException e) {
			LOG.error("Error occur while updating Attestation details:: Trace:"
					+ e.getMessage());
		}

	}

	/**
	 * Get Attestation Date
	 * 
	 * @param dateString
	 * @return Calendar date
	 */
	private Calendar getUserLoginDate(String dateString) {

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			calendar.setTime(sdf.parse(dateString));
		} catch (ParseException e) {

			LOG.error("Login date parse error::: Trace:" + e.getMessage());
		}
		return calendar;
	}

}
