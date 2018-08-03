package com.suntrust.dotcom.servlets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MailingException;
import com.suntrust.dotcom.services.EmailService;

/**
 * @author uiam82
 *
 */
@SlingServlet(
        metatype = true,
        label = "SunTrust - Student Lending Contact Us Form",
        description = "Implementation of Contact Us Page in Student Lending.",
        paths = { "/dotcom/studentlending" },
        methods = { "POST" }
)
@Service
public class ContactUsSlingServlet extends SlingAllMethodsServlet{

	/**
	 * 
	 */
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	@Reference
	private EmailService emailService;
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ContactUsSlingServlet.class);
	
	@Property(label="Email Notification Template Path", description="Enter the path for email notification template")
	private static final String NOTIFICATION_TEMPLATEPATH = "email.template.path";
	@Property(label="Email Sender Address", description="Enter Email Sender Address")
	private static final String EMAIL_SENDER_ADDRESS = "email.sender.address";
	@Property(label="Email Sender Name", description="Enter Email Sender Name")
	private static final String EMAIL_SENDER_NAME = "email.sender.name";
	@Property(label="General Enquiy Subject", description="Enter General Enquiy Subject")
	private static final String SUBJECT_GENERALENQUIRY="subject.generalenquiry";
	@Property(label="Customer Choice Subject", description="Enter Customer Choice Subject")
	private static final String SUBJECT_CUSTOMCHOICE="subject.customerchoice";
	@Property(label="Graduate Business Subject", description="Enter Graduate Business Subject")
	private static final String SUBJECT_GRADUATEBUSINESS="subject.gradbusiness";
	@Property(label="General Union Federal", description="Enter Union Federal Subject")
	private static final String SUBJECT_UNIONFEDERAL="subject.unionfederal";
	
	private String notificationTemplatePath=null;
	private String emailSenderAddress=null;
	private String emailSenderName=null;
	private String generalEnquirySubject=null;
	private String graduateBusinessSubject=null;
	private String customChoiceSubject=null;
	private String unionFederalSubject=null;
	
	private List<String> emailTo;
	private String emailid=null;
	private String firstname=null;
	private String lastname=null;
	private String homeadd=null;
	private String homeadd2=null;
	private String city=null;
	private String state=null;
	private String zipcode=null;
	private String phone=null;
	private String loanid=null;
	private String question=null;
	private String reason=null;
	private String thankyoupage=null;
	private String contactTime=null;
	private JSONObject json = new JSONObject();
	
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
	    
		try{
			
			response.setContentType("json"); 
			response.setCharacterEncoding("UTF-8");
			
		LOGGER.info("Reading the values from contact us form");
		
	    emailid=StringUtils.defaultString(request.getParameter("contact_email").toString(), "");
	    firstname=StringUtils.defaultString(request.getParameter("contact_fname").toString(), "");
	    lastname=StringUtils.defaultString(request.getParameter("contact_lname").toString(), "");
	    homeadd=StringUtils.defaultString(request.getParameter("contact_homeAddress").toString(), "");
	    homeadd2=StringUtils.defaultString(request.getParameter("contact_homeAddress2").toString(), "");
	    city=StringUtils.defaultString(request.getParameter("contact_city").toString(), "");
	    state=StringUtils.defaultString(request.getParameter("contact_state").toString(), "");
	    zipcode=StringUtils.defaultString(request.getParameter("contact_zipCode").toString(), "");
	    phone=StringUtils.defaultString(request.getParameter("phone_us").toString(), "");
	    loanid=StringUtils.defaultString(request.getParameter("contact_loginID").toString(), "");
	    reason=StringUtils.defaultString(request.getParameter("contactUs_reasonForContact").toString(), "");
	    question=StringUtils.defaultString(request.getParameter("contactUs_typeQuestion").toString(), "");
		thankyoupage=StringUtils.defaultString(request.getParameter("thankyouurl").toString(), "");
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss a");
		LocalDateTime now = LocalDateTime.now();
		contactTime = dtf.format(now);
		
		String emailrecipient=null;
		if(null!=request.getParameter(question))
			emailrecipient=request.getParameter(question).toString();
		
		emailTo=new ArrayList<String>();
		if(null!=emailrecipient)
			emailTo.add(emailrecipient.trim());
		else 
			throw new MailingException("No email address configured. Cannot sent email for contact us form.");
		
		LOGGER.info("Sending email to {}",emailrecipient);
		sendEmail(question);
		
		String resourceExtension="";
		if(!thankyoupage.endsWith(".html"))
			resourceExtension=".html";
		
		LOGGER.info("Redirecting to thank you page at {}",thankyoupage);
		
		/*adding json response*/
		json.put("emailstatus", "success");
		}
		catch(Exception e){
			try {
				json.put("emailstatus", "failed");
			} catch (JSONException e1) {
				LOGGER.error("Error while creating json response",e1);
			}
			LOGGER.error("Error while processing contact us form==>",e);
		}
		finally{
			response.getWriter().write(json.toString());
			
		}
	}
	
	@Activate
	protected void activate(Map<String, Object> properties)
	  {
	    LOGGER.info("[*** Suntrust Contact Us Form ConfigurationService]: activating configuration service");
	    readProperties(properties);
	  }
	
	protected void readProperties(Map<String, Object> properties)
	  {
		this.emailSenderAddress=PropertiesUtil.toString(properties.get(EMAIL_SENDER_ADDRESS), "");
		this.emailSenderName=PropertiesUtil.toString(properties.get(EMAIL_SENDER_NAME), "");
		this.notificationTemplatePath=PropertiesUtil.toString(properties.get(NOTIFICATION_TEMPLATEPATH), "");
		this.generalEnquirySubject=PropertiesUtil.toString(properties.get(SUBJECT_GENERALENQUIRY), "");
		this.customChoiceSubject=PropertiesUtil.toString(properties.get(SUBJECT_CUSTOMCHOICE), "");
		this.graduateBusinessSubject=PropertiesUtil.toString(properties.get(SUBJECT_GRADUATEBUSINESS), "");
		this.unionFederalSubject=PropertiesUtil.toString(properties.get(SUBJECT_UNIONFEDERAL), "");
	  }
	
	private void sendEmail(String question){
		Map<String, String> emailParams = new HashMap<>();
		String subject=null;
		
		switch(question){
		
		case "General Enquiry" :
			subject=generalEnquirySubject;
			break;
		case "Custom Choice Loan" :
			subject=customChoiceSubject;
			break;
		case "Graduate Business School Loan" :
			subject=graduateBusinessSubject;
			break;
		case "Union Federal Private Student Loan" :
			subject=unionFederalSubject;
			break;
		default :
			subject=generalEnquirySubject;
		
		
		}
		
		emailParams.put("subject", subject);
		emailParams.put("senderEmailAddress",emailSenderAddress);  
		emailParams.put("senderName",emailSenderName);
		emailParams.put("fname", firstname);
		emailParams.put("lname", lastname);
		emailParams.put("homeadd", homeadd);
		emailParams.put("homeadd2", homeadd2);
		emailParams.put("city", city);
		emailParams.put("state", state);
		emailParams.put("zipcode",zipcode);
		emailParams.put("phone", phone);
		emailParams.put("email", emailid);
		emailParams.put("questiontype", question);
		emailParams.put("loanid", loanid);
		emailParams.put("comment", reason);
		emailParams.put("date", contactTime);
		
		emailService.sendEmail(notificationTemplatePath, emailParams, emailTo);
	}

}
