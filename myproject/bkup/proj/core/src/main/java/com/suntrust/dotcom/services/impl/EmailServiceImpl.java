package com.suntrust.dotcom.services.impl;

import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MailingException;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.suntrust.dotcom.services.EmailService;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jcr.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Email Service Implementation Class
 * 
 * @author Anomitra (uiam82)
 */
@Component
@Service(value=EmailService.class)
public final class EmailServiceImpl implements EmailService
{
	/** Logger class reference variable */
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
	
	/** MessageGatewayService class reference variable */
	@Reference
	private MessageGatewayService messageGatewayService;
	
	/** ResourceResolverFactory class reference variable */
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	
	/** ConfigurationAdmin class reference variable */
	@Reference
	private ConfigurationAdmin configAdmin;
	
	/** Custom user service mapping name */
	private static String serviceName = "dotcomreadservice";
  
/**
 * Sends email to TOO and CC list
 */
  @Override
  public void sendEmail(String templatePath, Map<String, String> emailParams, List<String> recipients, List<String> ccUsers)
  {
    if (recipients.isEmpty()) {
      throw new IllegalArgumentException("Invalid Recipients");
    }
    MailTemplate mailTemplate = getMailTemplate(templatePath);
    try {
		Email email = getEmail(mailTemplate, HtmlEmail.class, emailParams);
		List<InternetAddress> toAddresses= recipients.stream()
								.map(recipient->{
									try{
										return new InternetAddress(recipient);
									}
									catch(AddressException e){
										LOGGER.error("Error is mail address. Messgae: {}, Trace: {}",e.getMessage(),e);
									}
									return null;
								}
								)
								.collect(Collectors.toList());
		
		List<InternetAddress> ccAddresses= ccUsers.stream()
				.map(recipient->{
					try{
						return new InternetAddress(recipient);
					}
					catch(AddressException e){
						LOGGER.error("Error in mail address. Messgae: {}, Trace: {}",e.getMessage(),e);
					}
					return null;
				}
				)
				.collect(Collectors.toList());
		
		email.setTo(toAddresses);
		if(emailParams.containsKey("subject") && emailParams.containsKey("wfType") && emailParams.containsKey("jobType") )
			email.setSubject(MimeUtility.encodeText(emailParams.get("subject"), "UTF-8", "Q"));
		if(!ccAddresses.isEmpty() && !ccAddresses.contains(null))
			email.setCc(ccAddresses);
		//messageGateway.send(email);
		sendEmail(email);
		
	} catch (Exception e) {
		LOGGER.error("Error sending email. Messgae: {}, Trace: {}",e.getMessage(),e);
	} 
    
  }
  
  /**
   * Sends email to TOO list
   */
  @Override
  public void sendEmail(String templatePath, Map<String, String> emailParams, List<String> recipients)
  {
    if (recipients.isEmpty()) {
      throw new IllegalArgumentException("Invalid Recipients");
    }
    MailTemplate mailTemplate = getMailTemplate(templatePath);
    try {
		Email email = getEmail(mailTemplate, HtmlEmail.class, emailParams);
		List<InternetAddress> toAddresses= recipients.stream()
								.map(recipient->{
									try{
										return new InternetAddress(recipient);
									}
									catch(AddressException e){
										LOGGER.error("Error is mail address. Messgae: {}, Trace: {}",e.getMessage(),e);
									}
									return null;
								}
								)
								.collect(Collectors.toList());
		
		
		email.setTo(toAddresses);
		//messageGateway.send(email);
		sendEmail(email);
	} catch (Exception e) {
		LOGGER.error("Error sending email. Messgae: {}, Trace: {}",e.getMessage(),e);
		throw new MailingException("Error in sending email");
	} 
  }
  
  /**
   * Send email using messageGateway
   * 
   * @param email
   */
  private void sendEmail(Email email){
	try {
		MessageGateway<Email> messageGateway = this.messageGatewayService.getGateway(HtmlEmail.class);
		 Map<String, String> smtpDetails = getSmtpDetails();
	  if (smtpDetails == null) {
			LOGGER.error("SMTP SERVER IS NOT REACHABLE");
		} else {
			if(serverListening(smtpDetails.get("host"), Integer.parseInt(smtpDetails.get("port")))) {
				messageGateway.send(email);
			} else {
				LOGGER.error("SMTP SERVER IS NOT REACHABLE");
			}
		}
	} catch (IOException e) {
		LOGGER.error("IOException captured. Messgae: {}, Trace: {}",e.getMessage(),e);
	}
  }
  
  /**
   * Returns email content with the given template 
   * 
   * @param mailTemplate
   * @param mailType
   * @param params
   * @return
   * @throws Exception
   */
  private Email getEmail(MailTemplate mailTemplate, Class<? extends Email> mailType, Map<String, String> params)
    throws Exception
  {
    Email email = mailTemplate.getEmail(StrLookup.mapLookup(params), mailType);
    if (params.containsKey("senderEmailAddress") && params.containsKey("senderName")) {
      email.setFrom(
        (String)params.get("senderEmailAddress"), 
        (String)params.get("senderName"));
    } else if (params.containsKey("senderEmailAddress")) {
      email.setFrom((String)params.get("senderEmailAddress"));
    }
    return email;
  }
  
  /**
   * Returns email template from given path
   * 
   * @param templatePath
   * @return
   * @throws IllegalArgumentException
   */
  private MailTemplate getMailTemplate(String templatePath)
    throws IllegalArgumentException
  {
    MailTemplate mailTemplate = null;
    ResourceResolver resourceResolver = null;
    try
    {
      Map<String, Object> authInfo = Collections.singletonMap("sling.service.subservice", serviceName);
      resourceResolver = this.resourceResolverFactory.getServiceResourceResolver(authInfo);
      mailTemplate = MailTemplate.create(templatePath, (Session)resourceResolver.adaptTo(Session.class));
      if (mailTemplate == null) {
        throw new IllegalArgumentException("Mail template path [ " + templatePath + " ] could not resolve to a valid template");
      }
    }
    catch (LoginException e)
    {
			LOGGER.error(
					"Unable to obtain an administrative resource resolver to get the Mail Template at [ "
							+ templatePath + " ]. Messgae: {}, Trace: {}",
					e.getMessage(), e);
    }
    finally
    {
      if (resourceResolver != null) {
        resourceResolver.close();
      }
    }
    return mailTemplate;
  }
  
  /**
   * Opens socket connection
   * 
   * @param host
   * @param port
   * @return
   */
  private boolean serverListening(String host, int port)
  {
      Socket socket = null;
      try
      {
          socket = new Socket(host, port);
          return true;
      }
      catch (Exception e)
      {
    	  LOGGER.error("Error in creating new socket. Messgae: {}, Trace: {}",e.getMessage(),e);
    	  return false;
      }
      finally
      {
          if(socket != null)
              try {socket.close();}
              catch(Exception e){
            	  LOGGER.error("Error in closing socket. Messgae: {}, Trace: {}",e.getMessage(),e);
              }
      }
  }
  
  /**
   * Returns SMTP details
   * 
   * @return
   * @throws IOException
   */
  private Map<String,String> getSmtpDetails() throws IOException{
		 if (null != configAdmin) {
			 Configuration config = configAdmin.getConfiguration("com.day.cq.mailer.DefaultMailService");
			  
			 if (null !=config && null !=config.getProperties()) {
				 Dictionary<String, Object> props = config.getProperties();
				 Map<String, String> stmpDetails=new HashMap<String, String>();
				 stmpDetails.put("host", PropertiesUtil.toString(props.get("smtp.host"), ""));
				 stmpDetails.put("port", PropertiesUtil.toString(props.get("smtp.port"), ""));
				 
			 return stmpDetails;
			 }
		 }
		 return null;
  }

}