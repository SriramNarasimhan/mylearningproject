package com.suntrust.dotcom.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.CreateInvalidationRequest;
import com.amazonaws.services.cloudfront.model.CreateInvalidationResult;
import com.amazonaws.services.cloudfront.model.InvalidationBatch;
import com.amazonaws.services.cloudfront.model.Paths;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.workflow.AWSClient;


/**
 * Purpose - The S3 util code provides the common AWS related utility method to read and return files.
 */

public class S3Utils{

	/** Default log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(S3Utils.class);
	
	

	
	public static void readWorkflowReport(String startDate, String endDate, SuntrustDotcomService dotcomService) {

		String bucketName = dotcomService.getPropertyValue("archive-bucket");//"stcom.archive.stage-bucket";
		String downloadLoc = dotcomService.getPropertyValue("workflow-report-download-path");//"/mnt/crx/author/crx-quickstart/snapshots/workflow-report";
		String prefix = dotcomService.getPropertyValue("archive-file-prefix");
		
		/*
		 * AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
		 * .withRegion(clientRegion) .withCredentials(new
		 * ProfileCredentialsProvider()) .build();
		 * 
		 */

		//final String startDate = "2018-05-04";
		//final String endDate = "2018-05-10";

		try {

			AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
			ObjectListing objects = s3Client.listObjects(bucketName, prefix);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date start = formatter.parse(startDate);
			Date end = formatter.parse(endDate);
			LOGGER.info("Fetching files from "+start+ " to "+end+"from AWS.");
			ArrayList<String> keyArray = new ArrayList<String>();

			do {

				for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {

					if (objectSummary.getLastModified().after(start) && objectSummary.getLastModified().before(end)) {

						keyArray.add(objectSummary.getKey());
					}

					if (objectSummary.getLastModified().equals(start) || objectSummary.getLastModified().equals(end)) {

						/*
						 * LOGGER.info(objectSummary.getKey() + "\t" +
						 * objectSummary.getSize() + "\t" +
						 * StringUtils.fromDate(objectSummary.getLastModified())
						 * );
						 */
						keyArray.add(objectSummary.getKey());

					}
				}
				objects = s3Client.listNextBatchOfObjects(objects);
			} while (objects.isTruncated());

			Iterator<String> iterator = keyArray.iterator();
			S3Object s3Object = null;
			while (iterator.hasNext()) {
				String key = iterator.next();
				LOGGER.info("Key:"+ key);
				LOGGER.info("bucketName:"+ bucketName);
				
				s3Object = s3Client.getObject(bucketName, key);

				S3ObjectInputStream s3InStr = s3Object.getObjectContent();
				FileOutputStream fos = new FileOutputStream(new File(downloadLoc + "/" + key));
				byte[] read_buf = new byte[1024];
				int read_len = 0;
				while ((read_len = s3InStr.read(read_buf)) > 0) {
					fos.write(read_buf, 0, read_len);
				}
				s3Object= null;
				s3InStr.close();
				fos.close();
				
			}
			
		} catch (AmazonServiceException ase) {
			LOGGER.info("Caught an AmazonServiceException, which " + "means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason.");
			LOGGER.info("Error Message:    " + ase.getMessage());
			LOGGER.info("HTTP Status Code: " + ase.getStatusCode());
			LOGGER.info("AWS Error Code:   " + ase.getErrorCode());
			LOGGER.info("Error Type:       " + ase.getErrorType());
			LOGGER.info("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			LOGGER.info("Caught an AmazonClientException, which " + "means the client encountered "
					+ "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.");
			LOGGER.info("Error Message: " + ace.getMessage());
		} catch (Exception ec) {
			LOGGER.info("Caught an Exception, which " + "means the client encountered "
					+ "an internal error while trying to " + "run the program "
					+ "such as not being able to download the file.");
			LOGGER.info("Error Message: " + ec.getMessage());
		}

	}

	}