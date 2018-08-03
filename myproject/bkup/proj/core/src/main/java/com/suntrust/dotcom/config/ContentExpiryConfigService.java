package com.suntrust.dotcom.config;

import java.util.Dictionary;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;

import com.suntrust.dotcom.utils.Utils;

/**
 * Implementation of Content Expiry ConfigService
 */
@Service(value = { ContentExpiryConfigService.class })
@Component(immediate = true, metatype = true, label = "Content Expiry Email Notification Configuration ", description = "Content Expiry Email Notification Configuration")
public class ContentExpiryConfigService {

	@Property(unbounded = PropertyUnbounded.ARRAY, label = "Site List", description = "Enter Sites where you want to seach expiry pages.")
	private static final String SITE_LIST = "sites.list";

	@Property(unbounded = PropertyUnbounded.ARRAY, label = "Suntrust root path", description = "Enter root path to search content expiry pages")
	private static final String SUNTRUST_ROOT_PATH = "suntrust.root.path";

	@Property(unbounded = PropertyUnbounded.ARRAY, label = "OnUp root path", description = "Enter root path to search content expiry pages")
	private static final String ONUP_ROOT_PATH = "onUp.root.path";

	@Property(unbounded = PropertyUnbounded.ARRAY, label = "Suntrust Rh root path", description = "Enter root path to search content expiry pages")
	private static final String SUNTRUSTRH_ROOT_PATH = "suntrustrh.root.path";

	@Property(label = "Workflow model path")
	private static final String SUNTRUST_MODEL_PATH = "suntrust.attestation.model.path";

	@Property(label = "Content Attestation e-mail Template Path")
	private static final String ON_ATTESTATION_DATE_EMAIL_TEMPLATE_PATH = "attestation.email.template";

	@Property(label = "Workflow payload path")
	private static final String WORKFLOW_PAYLOAD_PATH = "workflow.payload.path";

	@Property(label = "Suntrust sender email address")
	private static final String SUNTRUST_SENDER_EMAIL = "suntrust.sender.emailid";

	@Property(label = "Suntrust sender display name")
	private static final String SUNTRUST_EMAIL_DISPLAY_NAME = "suntrust.sender.displayName";
	@Property(label = "Attestation Manager Email.")
	private static final String SUNTRUST_MANAGER_EMAIL = "suntrust.cc.email.dl";

	@Property(label = "Path of CSV file")
	private static final String SUNTRUST_ATTESTATION_CSV_PATH = "suntrust.attestation.csv.path";

	@Property(label = "Suntrust Digital Attestation Email Id")
	private static final String SUNTRUST_DIGITAL_ATTESTATION_EMAIL_ID = "suntrust.digital.attestation.emailid";

	@Property(label = "Content Attestation Kickoff e-mail subject")
	private static final String ON_ATTESTATION_DATE_EMAIL_SUBJECT = "attestationDays.email.subject";

	@Property(label = "Content Attestation Kickoff e-mail ID")
	private static final String ON_ATTESTATION_DATE_EMAIL__ID = "attestationDays.email.id";

	@Property(label = "Content Attestation Kickoff e-mail Description")
	private static final String ON_ATTESTATION_DATE_EMAIL__DESCRIPTION = "attestationDays.email.description";

	@Property(label = "Content Attestation Kickoff e-mail Description note")
	private static final String ON_ATTESTATION_DATE_EMAIL_NOTES = "attestationDays.email.note";

	@Property(label = "No Of Week To Reminder Content Attestation")
	private static final String No_Week_Advance_Attestation_Owner_Reminder = "noOfWeekAdvance";

	@Property(label = "No of Week Advance Attestation Owner email subject")
	private static final String NO_WEEK_ADVANCE_ATTESTATION_EMAIL_SUBJECT = "noOfWeekAdvance.email.subject";

	@Property(label = "No of Week Advance Attestation Owner email notes")
	private static final String NO_WEEK_ADVANCE_ATTESTATION_EMAIL_ID = "noOfWeekAdvance.email.id";

	@Property(label = "No of Week Advance Attestation Owner email Description")
	private static final String NO_WEEK_ADVANCE_ATTESTATION__DESCRIPTION = "noOfWeekAdvance.email.description";

	@Property(label = "No of Week Advance Attestation Owner email note")
	private static final String NO_WEEK_ADVANCE_ATTESTATION_NOTES = "noOfWeekAdvance.email.note";

	@Property(label = "No of Week Due Attestation Owner Reminder")
	private static final String NO_OF_WEEK_DUE_OWNER = "noOfWeekDue";

	@Property(label = "No of Week Due Attestation Owner Reminder email subject")
	private static final String NO_OF_WEEK_DUE_OWNER_EMAIL_SUBJECT = "noOfWeekDue.email.subject";

	@Property(label = "No of Week Due Attestation Owner Reminder email ID")
	private static final String NO_OF_WEEK_DUE_OWNER_EMAIL_ID = "noOfWeekDue.email.id";

	@Property(label = "No of Week Due Attestation Owner Reminder email Description")
	private static final String NO_OF_WEEK_DUE_OWNER_EMAIL__DESCRIPTION = "noOfWeekDue.email.description";

	@Property(label = "No of Week Due Attestation Owner Reminder email note")
	private static final String NO_OF_WEEK_DUE_OWNER_EMAIL_NOTES = "noOfWeekDue.email.note";

	@Property(label = "No of Week Past Due to Reviewer’s Manager")
	private static final String NO_OF_WEEK_DUE_REVIEWER = "noOfWeekPast";

	@Property(label = "No of Week Past Due to Reviewer’s Manager email subject")
	private static final String NO_OF_WEEK_DUE_REVIEWER_EMAIL_SUBJECT = "noOfWeekPast.email.subject";

	@Property(label = "No of Week Past Due to Reviewer’s Manager email ID")
	private static final String NO_OF_WEEK_DUE_REVIEWER_EMAIL_ID = "noOfWeekPast.email.id";
	@Property(label = "No of Week Past Due to Reviewer’s Manager email Description")
	private static final String NO_OF_WEEK_DUE__EMAIL__DESCRIPTION = "noOfWeekPast.email.description";

	@Property(label = "No of Week Past Due to Reviewer’s Manager e-mail note")
	private static final String NO_OF_WEEK_DUE_EMAIL_NOTES = "noOfWeekPast.email.note";

	@Property(label = "No of Week Overdue to Reviewer’s Manager")
	private static final String NO_OF_WEEK_OVERDUE = "noOfWeekOverdue";

	@Property(label = "No of Week Overdue to Reviewer’s Manager email subject")
	private static final String NO_OF_WEEK_OVERDUE_EMAIL_SUBJECT = "noOfWeekOverdue.email.subject";

	@Property(label = "No of Week Overdue to Reviewer’s Manager email id")
	private static final String FOUR_WEEK_AFTER_ATTESTATION_DATE_EMAIL_ID = "noOfWeekOverdue.email.id";

	@Property(label = "No of Week Overdue to Reviewer’s Manager email Description")
	private static final String NO_OF_WEEK_OVERDUE_EMAIL__DESCRIPTION = "noOfWeekOverdue.email.description";

	@Property(label = "No of Week Overdue to Reviewer’s Manager email note")
	private static final String NO_OF_WEEK_OVERDUE_EMAIL_NOTES = "noOfWeekOverdue.email.note";

	private Dictionary<String, Object> properties;

	@SuppressWarnings("unchecked")
	protected void activate(ComponentContext context) {
		properties = context.getProperties();
	}

	protected void deactivate(ComponentContext context) {
		properties = null;
	}

	public String getPropertyValue(String key) {
		return Utils.getPropertyValue(key, properties);
	}

	public List<String> getPropertyArray(String key) {
		return Utils.getPropertyArray(key, properties);
	}
}