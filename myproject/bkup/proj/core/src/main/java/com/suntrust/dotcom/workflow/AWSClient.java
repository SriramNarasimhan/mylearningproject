package com.suntrust.dotcom.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.suntrust.dotcom.config.SuntrustDotcomService;

public class AWSClient {
	
	private static final String AWSIAMROLE="aws-iam-role";
	private static final String AWSEXTERNALID="aws-external-id";
	private static final String AWSSESSIONNAME="aws-session-name";
	private static final String AWSCREDSDURATION="aws-creds-duration";
	private static final String AWSENDPOINT="aws-end-point";
	private static final Logger logger = LoggerFactory
			.getLogger(AWSClient.class);
	public AmazonCloudFrontClient getAWSClient(SuntrustDotcomService dotcomServiceconfig) {
		try {
			logger.debug("Read Config values: "
					+ dotcomServiceconfig.getPropertyValue(AWSIAMROLE)
					+ dotcomServiceconfig.getPropertyValue(AWSEXTERNALID)
					+ dotcomServiceconfig.getPropertyValue(AWSSESSIONNAME)
					+ dotcomServiceconfig.getPropertyValue(AWSCREDSDURATION)
					+ dotcomServiceconfig.getPropertyValue(AWSENDPOINT));
			AWSSecurityTokenServiceClient client = new AWSSecurityTokenServiceClient();			
			client.setEndpoint(dotcomServiceconfig.getPropertyValue(AWSENDPOINT));
			if (dotcomServiceconfig != null && client!=null) {
			AssumeRoleResult assumeRoleResult = client
					.assumeRole(new AssumeRoleRequest().withRoleArn(dotcomServiceconfig.getPropertyValue(AWSIAMROLE))
							.withExternalId(dotcomServiceconfig.getPropertyValue(AWSEXTERNALID))
							.withDurationSeconds(Integer.parseInt(dotcomServiceconfig.getPropertyValue(AWSCREDSDURATION)))
							.withRoleSessionName(dotcomServiceconfig.getPropertyValue(AWSSESSIONNAME)));
			Credentials stsCredentials = assumeRoleResult.getCredentials();
			BasicSessionCredentials credentials = new BasicSessionCredentials(
					stsCredentials.getAccessKeyId(),
					stsCredentials.getSecretAccessKey(),
					stsCredentials.getSessionToken());			
			return new AmazonCloudFrontClient(credentials);
			}
		} catch (Exception e) {
			logger.error("Exception captured when getting AmazonCloudFrontClient message: "+e.getMessage());
			logger.error("Trace: "+e.getStackTrace()); 
			return null;
		}
		return null;
	}
}