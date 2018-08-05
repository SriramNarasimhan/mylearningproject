package com.suntrust.dotcom.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import com.opencsv.CSVWriter;
import com.suntrust.dotcom.beans.WorkflowReportBean;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.utils.ProgressDetails;
import com.suntrust.dotcom.utils.S3Utils;
import com.suntrust.dotcom.utils.WorkflowConstants;

/**
 * Class used by the authors if they want to download a report containing list
 * of completed workflows.
 */

@SuppressWarnings("serial")
@SlingServlet(metatype = false, label = "SunTrust - Workflow Report Servlet", paths = {
		"/dotcom/workflowreport" }, methods = { "GET" })
public final class WorkflowReportServlet extends SlingAllMethodsServlet {

	@Reference
	SuntrustDotcomService dotcomService;

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowReportServlet.class);

	/**
	 * Path where report will be stored in DAM
	 */
	private static String REPORT_DAM_PATH = null;
	/**
	 * Name of the report to be generated
	 */
	private static String REPORT_FILE_NAME = null;
	/**
	 * Extension of the report to be generated
	 */
	private static String EXTENSION = ".csv";
	/**
	 * Timestamp that will be appended to report file name
	 */
	private static String REPORT_FILE_TIMESTAMP = null;
	/**
	 * Workflow report XML files path in server
	 */
	private static String XML_FILE_LOCATION = null;
	
	String startDateString = null;
	String endDateString = null;
	/** Date pattern for folder creation */
	private static final SimpleDateFormat FOLDER_FORMAT = new SimpleDateFormat("MMddyyyy");
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		try {
			LOGGER.debug("inside WorkflowReportServlet");
			
			LOGGER.debug("req param::" + request.getParameter("data"));
			
			// read the taskId;
			String taskId = request.getParameter("taskIdentity");

			REPORT_DAM_PATH = dotcomService.getPropertyValue("workflow-report-save-path");
			REPORT_FILE_NAME = dotcomService.getPropertyValue("workflow-report-filename");
			
			LOGGER.debug("REPORT_DAM_PATH::" + REPORT_DAM_PATH +" and REPORT_FILE_NAME::" + REPORT_FILE_NAME);

			final String PREFIX = dotcomService.getPropertyValue("archive-file-prefix");
			
			XML_FILE_LOCATION = dotcomService.getPropertyValue("workflow-report-download-path") + "/" + PREFIX;
			LOGGER.debug("XML_FILE_LOCATION::" + XML_FILE_LOCATION);

			FileUtils.cleanDirectory(new File(XML_FILE_LOCATION));

			JSONObject json = (JSONObject) new JSONParser().parse(request.getParameter("data"));

			startDateString = (String) json.get("startDate");
			endDateString = (String) json.get("endDate");

			String s3StartDateString = (String) json.get("startdate");
			String s3EndDateString = (String) json.get("enddate");

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			DateTimeFormatter XmlDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			String awsStartDateString = LocalDate.parse(startDateString, formatter).format(XmlDateFormatter);
			String awsEndDateString = LocalDate.parse(endDateString, formatter).plusDays(1).format(XmlDateFormatter);
			
			ProgressDetails progressDetails = new ProgressDetails();
			LOGGER.debug("Reading workflow reports from S3 from "+awsStartDateString+" to "+awsEndDateString);

			if (StringUtils.isNotBlank(s3StartDateString) && StringUtils.isNotBlank(s3EndDateString)) {
				s3StartDateString = LocalDate.parse(s3StartDateString, formatter).format(XmlDateFormatter);
				s3EndDateString = LocalDate.parse(s3EndDateString, formatter).plusDays(1).format(XmlDateFormatter);
				S3Utils.readWorkflowReport(s3StartDateString, s3EndDateString, dotcomService, progressDetails);
			} else {
				S3Utils.readWorkflowReport(awsStartDateString, awsEndDateString, dotcomService, progressDetails);
			}

			LOGGER.debug("Completed reading workflow reports from S3. Report will be generated from "+startDateString+" to "+endDateString);
			
			boolean excelCreated = readFilteredXml(startDateString, endDateString, request);

			if (excelCreated) {
				response.getWriter().write("Success|" + REPORT_DAM_PATH + "/" + REPORT_FILE_TIMESTAMP);
			}

		} catch (org.json.simple.parser.ParseException e) {
			LOGGER.error("Workflow report Exception : Message: {}, Trace: {}", e.getMessage(), e);
		}
	}

	private boolean readFilteredXml(final String startDateString, final String endDateString,
			final SlingHttpServletRequest request) {

		List<WorkflowReportBean> lBean = new ArrayList<WorkflowReportBean>();

		boolean excelCreated = false;
		SimpleDateFormat dtformat = new SimpleDateFormat("MM/dd/yyyy h:mm a");
		SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");

		try {

			File folder = new File(XML_FILE_LOCATION);
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			if (folder.isDirectory()) {
				File[] listOfFiles = folder.listFiles();
				for (File file : listOfFiles) {
					String filepath = file.getPath();
					if (filepath.endsWith(".xml") || filepath.endsWith(".XML")) {

						File xmlFile = new File(filepath);
						LOGGER.debug("Reading xml file ::"+xmlFile.getPath());
						Document doc;
						try {
							doc = docBuilder.parse(xmlFile);

							// normalize text representation
							doc.getDocumentElement().normalize();

							ArrayList<String> startTimes = new ArrayList<String>();
							ArrayList<String> endTimes = new ArrayList<String>();
							ArrayList<String> taskNames = new ArrayList<String>();
							ArrayList<String> comments = new ArrayList<String>();
							ArrayList<String> statuses = new ArrayList<String>();
							ArrayList<String> approvers = new ArrayList<String>();

							WorkflowReportBean reportBean = new WorkflowReportBean();

							reportBean.setWorkflowDate(getNodeString(doc, WorkflowConstants.WORKFLOW_DATE));
							reportBean.setWorkflowTitle(getNodeString(doc, WorkflowConstants.WORKFLOW_TITLE));
							reportBean.setJobId(getNodeString(doc, WorkflowConstants.JOB_ID));
							reportBean.setJobInitiator(getNodeString(doc, WorkflowConstants.JOB_INITIATOR));
							reportBean.setJobInitiatorId(getNodeString(doc, WorkflowConstants.JOB_INITIATOR_EMAIL_ID));
							reportBean.setWorkflowName(getNodeString(doc, WorkflowConstants.WORKFLOW_NAME));

							ArrayList<String> resources = new ArrayList<String>();

							NodeList flowList = doc.getElementsByTagName(WorkflowConstants.RESOURCES);
							for (int i1 = 0; i1 < flowList.getLength(); i1++) {
								NodeList childList = flowList.item(i1).getChildNodes();
								for (int j = 0; j < childList.getLength(); j++) {
									Node childNode = childList.item(j);
									if (WorkflowConstants.RESOURCE_NODE.equals(childNode.getNodeName())) {
										resources.add(childList.item(j).getTextContent());
									}
								}
							}
							if (doc.getElementsByTagName(WorkflowConstants.RESOURCE) != null) {
								resources.addAll(getNodeContent(doc, WorkflowConstants.RESOURCE));
							}

							Set<String> set = new LinkedHashSet<String>(resources);
							ArrayList<String> uniqueResources = new ArrayList<String>();
							uniqueResources.addAll(set);

							reportBean.setResources(uniqueResources);
							reportBean.setChangeType(getNodeString(doc, WorkflowConstants.CHANGE_TYPE));
							reportBean.setSkipLegalReview(getNodeString(doc, WorkflowConstants.SKIP_LEGAL_REVIEW));
							reportBean.setSkipQaReview(getNodeString(doc, WorkflowConstants.SKIP_QA_REVIEW));
							reportBean.setSkipUiUxReview(getNodeString(doc, WorkflowConstants.SKIP_UI_UX_REVIEW));

							NodeList historySteps = doc.getElementsByTagName(WorkflowConstants.STEP);

							for (int s = 0; s < historySteps.getLength(); s++) {
								Node historyStep = historySteps.item(s);
								if (historyStep.getNodeType() == Node.ELEMENT_NODE) {
									Element historyNodeElement = (Element) historyStep;

									if (getNodeContent(historyNodeElement, WorkflowConstants.TITLE)
											.equalsIgnoreCase(WorkflowConstants.END)) {
										reportBean.setEndTime2(
												getNodeContent(historyNodeElement, WorkflowConstants.END_TIME));
										reportBean
												.setTitle(getNodeContent(historyNodeElement, WorkflowConstants.TITLE));
									} else {

										String startTime = getNodeContent(historyNodeElement,
												WorkflowConstants.START_TIME);
										if (startTime != null) {
											startTimes.add(startTime);
										}

										String endTime = getNodeContent(historyNodeElement, WorkflowConstants.END_TIME);
										if (endTime != null) {
											endTimes.add(endTime);
										}

										String taskName = getNodeContent(historyNodeElement,
												WorkflowConstants.TASK_NAME);
										if (taskName != null) {
											taskNames.add(taskName);
										}

										String comment = getNodeContent(historyNodeElement, WorkflowConstants.COMMENT);
										if (comment != null) {
											comments.add(comment);
										}

										String status = getNodeContent(historyNodeElement, WorkflowConstants.STATUS);
										if (status != null) {
											statuses.add(status);
										}

										String approver = getNodeContent(historyNodeElement,
												WorkflowConstants.APPROVER);
										if (approver != null) {
											approvers.add(approver);
										}
									}

								}

							}

							reportBean.setStartTime(startTimes);
							reportBean.setEndTime(endTimes);
							reportBean.setTaskName(taskNames);
							reportBean.setComment(comments);
							reportBean.setStatus(statuses);
							reportBean.setApprover(approvers);

							// String xmlStartDateString =
							// reportBean.getWorkflowDate();
							String xmlEndDateString = reportBean.getEndTime2();

							Date startDateFormatted = dateformat.parse(startDateString);
							Date endDateFormatted = dateformat.parse(endDateString);

							String startDateStringFormatted = formatter.format(startDateFormatted);
							String endDateStringFormatted = formatter.format(endDateFormatted);

							Date startDate = formatter.parse(startDateStringFormatted);
							Date endDate = formatter.parse(endDateStringFormatted);

							Date xmlEndDateFormatted = dtformat.parse(xmlEndDateString);
							String xmlEndDateStringFormatted = formatter.format(xmlEndDateFormatted);
							Date xmlEndDate = formatter.parse(xmlEndDateStringFormatted);

							LOGGER.info("xmlEndDate :" + xmlEndDate);

							/*
							 * if((xmlStartDate.equals(startDate) ||
							 * xmlStartDate.after(startDate)) &&
							 * (xmlEndDate.equals(endDate) ||
							 * xmlEndDate.before(endDate))) {
							 */// considering xml's both start and end date
							if ((xmlEndDate.after(startDate))
									&& (xmlEndDate.equals(endDate) || xmlEndDate.before(endDate))) {// only
																									// considering
																									// xml's
																									// end
																									// date
								LOGGER.info("adding xml in excel"+reportBean.getWorkflowName());
								lBean.add(reportBean);
							}

						} catch (SAXException e) {
							LOGGER.error("Exception in readFilteredXml() method" + e);
						} catch (ParseException e) {
							LOGGER.error("ParseException in readFilteredXml() method" + e);
						} catch (IOException e) {
							LOGGER.error("IOException in readFilteredXml() method" + e);
						}
					}
				}
			}

			LOGGER.info("lBean size:" + lBean.size());

			if (!lBean.isEmpty()) {
				excelCreated = createandDownloadExcel(lBean, request);
				LOGGER.info("excelCreated:" + excelCreated);
			}

		} catch (ParserConfigurationException e) {
			LOGGER.error("ParserConfigurationException: " + e.getMessage());
		} catch (IOException e) {
			LOGGER.error("IOException in readFilteredXml() method" + e);
		} catch (ParseException e) {
			LOGGER.error("ParseException in readFilteredXml() method" + e);
		}

		return excelCreated;
	}

	private boolean createandDownloadExcel(List<WorkflowReportBean> xmlList, SlingHttpServletRequest request)
			throws IOException, ParseException {

		boolean csvSaved = false;
		List<String[]> entries = new ArrayList<>();
		if (xmlList.size() > 0) {
			entries.add(new String[] { WorkflowConstants.WORKFLOW_DATE, WorkflowConstants.WORKFLOW_TITLE,
					WorkflowConstants.JOB_ID, WorkflowConstants.JOB_INITIATOR, WorkflowConstants.JOB_INITIATOR_EMAIL_ID,
					WorkflowConstants.WORKFLOW_NAME, WorkflowConstants.RESOURCE, WorkflowConstants.CHANGE_TYPE,
					WorkflowConstants.START_TIME, WorkflowConstants.END_TIME, WorkflowConstants.TASK_NAME,
					WorkflowConstants.COMMENT, WorkflowConstants.STATUS, WorkflowConstants.APPROVER,
					WorkflowConstants.END_TIME2, WorkflowConstants.TITLE, WorkflowConstants.SKIP_LEGAL_REVIEW,
					WorkflowConstants.SKIP_QA_REVIEW, WorkflowConstants.SKIP_UI_UX_REVIEW });
		}

		String[] csvValues;

		LOGGER.info("xmlList size" + xmlList.size());

		for (int i = 0; i < xmlList.size(); i++) {
			for (int k = 0; k < xmlList.get(i).getResources().size(); k++) {
				for (int j = 0; j < xmlList.get(i).getStartTime().size(); j++) {
					csvValues = new String[] { xmlList.get(i).getWorkflowDate(), xmlList.get(i).getWorkflowTitle(),
							xmlList.get(i).getJobId(), xmlList.get(i).getJobInitiator(),
							xmlList.get(i).getJobInitiatorId(), xmlList.get(i).getWorkflowName(),
							xmlList.get(i).getResources().get(k), xmlList.get(i).getChangeType(),
							xmlList.get(i).getStartTime().get(j), xmlList.get(i).getEndTime().get(j),
							xmlList.get(i).getTaskName().get(j), xmlList.get(i).getComment().get(j),
							xmlList.get(i).getStatus().get(j), xmlList.get(i).getApprover().get(j),
							xmlList.get(i).getEndTime2(), xmlList.get(i).getTitle() };
					entries.add(csvValues);
				}
			}
		}

		if (REPORT_FILE_NAME.contains(".")) {
			EXTENSION = "";
		}
		String reportTimeStamp = "-" + FOLDER_FORMAT.format(DATE_FORMAT.parse(startDateString)) + "-"
				+ FOLDER_FORMAT.format(DATE_FORMAT.parse(endDateString)) + EXTENSION;

		REPORT_FILE_TIMESTAMP = REPORT_FILE_NAME + reportTimeStamp;

		File csvFile = new File(REPORT_FILE_NAME);
		
		LOGGER.debug(csvFile.getPath()+" CSV file before saving in DAM");

		FileOutputStream fos = new FileOutputStream(csvFile);
		OutputStreamWriter writer = new OutputStreamWriter(fos);
		CSVWriter csvWriter = new CSVWriter(writer);
		csvWriter.writeAll(entries);
		csvWriter.close();
		IOUtils.closeQuietly(writer);

		RandomAccessFile randomAccessFile = new RandomAccessFile(csvFile, "rw");
		FileChannel inputChannel = randomAccessFile.getChannel();
		InputStream fis = Channels.newInputStream(inputChannel);

		LOGGER.debug("CSV written. Saving in DAM");
		try {
			AssetManager manager = request.getResourceResolver().adaptTo(AssetManager.class);
			LOGGER.debug("Creating report in :" + REPORT_DAM_PATH + "/" + REPORT_FILE_TIMESTAMP);
			Asset asset = manager.createAsset(REPORT_DAM_PATH + "/" + REPORT_FILE_TIMESTAMP, fis,
					"application/vnd.ms-excel", true);

			if (asset != null) {
				LOGGER.info("Asset created in " + asset.getPath());
				csvSaved = true;
			}
		} finally {
			randomAccessFile.close();
			inputChannel.close();
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(fos);
		}

		return csvSaved;
	}

	static String getNodeContent(Element historyNodeElement, String nodeName) {
		Element nodeListElement = null;
		String content = "";
		if (historyNodeElement.getElementsByTagName(nodeName) != null) {
			NodeList nodeList = historyNodeElement.getElementsByTagName(nodeName);
			if (nodeList != null) {
				nodeListElement = (Element) nodeList.item(0);
				if (nodeListElement != null) {
					content = nodeListElement.getTextContent();
				}
			}
		}
		return content;
	}

	private static ArrayList<String> getNodeContent(Document doc, String nodeName) {
		ArrayList<String> list = new ArrayList<String>();
		Element nodeListElement = null;
		if (doc.getElementsByTagName(nodeName) != null) {
			NodeList nodeList = doc.getElementsByTagName(nodeName);
			if (nodeList != null) {
				nodeListElement = (Element) nodeList.item(0);
				if (nodeListElement != null) {
					if (nodeListElement.hasChildNodes()) {
						NodeList textLNList = nodeListElement.getChildNodes();
						list.add(((Node) textLNList.item(0)).getNodeValue().trim());
					} else {
						list.add(nodeListElement.getTextContent());
					}
				}
			}
		}
		return list;
	}

	private static String getNodeString(Document doc, final String nodeName) {
		Element nodeListElement = null;
		String content = "";
		if (doc.getElementsByTagName(nodeName) != null) {
			NodeList nodeList = doc.getElementsByTagName(nodeName);
			if (nodeList != null) {
				nodeListElement = (Element) nodeList.item(0);
				if (nodeListElement != null) {
					if (nodeListElement.hasChildNodes()) {
						NodeList textLNList = nodeListElement.getChildNodes();
						content = (((Node) textLNList.item(0)).getNodeValue().trim());
					} else {
						content = (nodeListElement.getTextContent());
					}
				}
			}
		}
		return content;
	}

	@Activate
	protected void activate(final Map<String, Object> config) {
		LOGGER.debug("The activate method is invoked");
	}
}
