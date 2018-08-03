package com.suntrust.dotcom.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.suntrust.dotcom.config.AdvisorConfigService;
import com.suntrust.dotcom.config.SuntrustDotcomService;
import com.suntrust.dotcom.services.ServiceAgentService;
import com.suntrust.dotcom.utils.Utils;

/**
 * This SearchComponentImpl is used search the JCR using user's search term and
 * return the JSON response to front-end.
 * 
 * @author Cognizant
 * @version 1.0
 * @since 10 July 2017
 * 
 */
@SuppressWarnings("serial")
@SlingServlet(paths = "/dotcom/search")
public class SearchComponentImpl extends SlingSafeMethodsServlet {
	/** Service variable to resolve resources * */
	@Reference
	private ResourceResolverFactory resourceResolverFactory;
	/** Service variable to read JCR repository * */
	@Reference
	private ServiceAgentService serviceAgent;
	/** Service variable to create query * */
	@Reference
	private QueryBuilder builder;
	/** Service variable to read the run-mode configurations * */
	@Reference
	private SuntrustDotcomService dotcomService;
	/** AdvisorConfigService class variable */
	@Reference
	private AdvisorConfigService advisorConfigService;
	/** Service variable to get settings service * */
	@Reference
	private SlingSettingsService settingsService;
	/** List variable to store urls * */
	private List<String> canonicalUrls = null;
	/** Constant variable to store cqTemplate * */
	public static final String CQTEMPLATE = "cq:template";
	/** Constant variable to store SLINGRESOURCETYPE * */
	private static final String SLINGRESOURCETYPE = "sling:resourceType";
	/** Constant variable to store JCRCONTENT * */
	private static final String JCRCONTENT = "jcr:content";
	/** Constant variable to store PROPERTY1 * */
	public static final String PROPERTY1 = "1_property";
	/** Constant variable to store PROPERTY2 * */
	public static final String PROPERTY2 = "2_property";
	/** Constant variable to store PROPERTY3 * */
	public static final String PROPERTY3 = "3_property";
	/** Constant variable to store PROPERTY1OPERATION * */
	public static final String PROPERTY1OPERATION = "1_property.operation";
	/** Constant variable to store PROPERTY2VALUE * */
	public static final String PROPERTY2VALUE = "2_property.value";
	/** Constant variable to store PROPERTY3VALUE * */
	public static final String PROPERTY3VALUE = "3_property.value";
	/** Constant variable to store LOCATIONS * */
	public static final String LOCATIONS = "Locations";
	/** Constant variable to store PEOPLE * */
	public static final String PEOPLE = "People";
	/** Constant variable to store PRODUCTS * */
	public static final String PRODUCTS = "Products";
	/** Constant variable to store FAQS * */
	public static final String FAQS = "Faqs";
	/** Constant variable to store DOCUMENTS * */
	public static final String DOCUMENT = "Document";
	/** Constant variable to store ALLRESULTS * */
	public static final String ALLRESULTS = "allResults";
	/** Constant variable to store RESOURCE * */
	public static final String RESOURCE = "Resource";
	/** Constant variable to store ALWAYSCLOSED * */
	public static final String ALWAYSCLOSED = "AlwaysClosed";
	/** Constant variable to store CLOSED * */
	public static final String CLOSED = "Closed";
	/** Constant variable to store RESULTTYPE * */
	public static final String RESULTTYPE = "result_type";
	/** Constant variable to store GROUP * */
	public static final String GROUP = "group.";
	/** Logger variable to log program state * */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SearchComponentImpl.class);

	/**
	 * To handle HTTP GET request
	 */
	@Override
	protected void doGet(final SlingHttpServletRequest request,
			final SlingHttpServletResponse response) {
		String searchString = null;
		String currentDay = null;
		String searchingFor = null;
		String productTag = null;
		String resourceTag = null;
		String locationService = null;
		String[] aboutMe = null;
		String contextUrl = "/content/suntrust/dotcom/us/";
		String damContextUrl = "/content/dam/suntrust";
		List<String> resultPages = new ArrayList<String>();
		JSONObject json = new JSONObject();
		response.setContentType("json");
		response.setCharacterEncoding("UTF-8");
		searchString = request.getParameter("searchString");
		try {
			searchString = URLDecoder.decode(searchString, "UTF-8");
			currentDay = request.getParameter("currentDay");
			searchingFor = request.getParameter("searchingFor");
			productTag = StringUtils.defaultIfEmpty(
					request.getParameter("productTag"), "");
			resourceTag = StringUtils.defaultIfEmpty(
					request.getParameter("resourceTag"), "");
			locationService = request.getParameter("LocationService");
			aboutMe = request.getParameterValues("aboutMe[]");
			canonicalUrls = dotcomService.getPropertyArray("canonical.urls");
			json.put("all_results", "");
			json.put("location_results", "");
			json.put("people_results", "");
			json.put("product_results", "");
			json.put("faq_results", "");
			json.put("resource_results", "");
			if (StringUtils.isNotBlank(searchString)) {
				resultPages = getResultPageNodes(contextUrl, damContextUrl,
						searchString, searchingFor, locationService,
						productTag, aboutMe, resourceTag);
				if (!resultPages.isEmpty()) {
					json = new JSONObject();
					json = getPropertiesOfResultPages(resultPages,
							searchingFor, currentDay);
				}
			}
			response.getWriter().write(json.toString());
		} catch (UnsupportedEncodingException unsupportedEncodingException) {
			LOGGER.error(unsupportedEncodingException.getMessage(),
					unsupportedEncodingException);
		} catch (IOException iOException) {
			LOGGER.error(iOException.getMessage(), iOException);
		} catch (JSONException jsonException) {
			LOGGER.error(jsonException.getMessage(), jsonException);
		}
	}

	/**
	 * @param contextUrl
	 * @param searchString
	 * @param searchingFor
	 * @param locationService
	 * @param productTag
	 * @param aboutMe
	 * @return resultPages
	 */
	public List<String> getResultPageNodes(String contextUrl,
			String damContextUrl, String searchString, String searchingFor,
			String locationService, String productTag, String[] aboutMe,
			String resourceTag) {
		Session session = null;
		String propertyName = null;
		String formattedSearchString = null;
		ResourceResolver resourceResolver = null;
		List<String> postalCodeList = new ArrayList<String>();
		List<String> aboutMeList = new ArrayList<String>();
		List<String> resultPages = new ArrayList<String>();
		Map<String, String> map = new HashMap<String, String>();
		if (searchString.contains("mqPostalCodeData")
				&& !"mqPostalCodeData".equals(searchString)) {
			if (searchingFor.equalsIgnoreCase(LOCATIONS))
				propertyName = "jcr:content/loc_zipcode";
			else if (searchingFor.equalsIgnoreCase(PEOPLE))
				propertyName = "jcr:content/adv_zipcodes";
			formattedSearchString = searchString
					.replace("mqPostalCodeData", "");
			postalCodeList = Arrays.asList(formattedSearchString.split(","));
			for (int i = 0; i < postalCodeList.size(); i++) {
				map.put("group." + (i + 1) + "_property", propertyName);
				map.put("group." + (i + 1) + "_property.value", "%"
						+ postalCodeList.get(i) + "%");
				map.put("group." + (i + 1) + "_property.operation", "like");
			}
			map.put("group.p.or", "true");
		} else {
			map.put("fulltext", searchString);
		}

		try {
			if (searchingFor.equalsIgnoreCase(ALLRESULTS)) {
				map.put("1_group.1_group.1_path", contextUrl);
				map.put("1_group.2_group.1_path", damContextUrl);
				map.put("1_group.1_group.type", "cq:Page");
				map.put("1_group.2_group.type", "dam:Asset");
				map.put("1_group.1_group.property", "jcr:content/searchable");
				map.put("1_group.1_group.property.operation", "not");
				map.put("1_group.p.or", "true");
				map.put("1_group.2_group.property",
						"jcr:content/renditions/original/jcr:content/jcr:mimeType");
				map.put("1_group.2_group.property.value", "application/%");
				map.put("1_group.2_group.property.operation", "like");
			} else {
				map.put("path", contextUrl);
				map.put("type", "cq:Page");
				map.put(PROPERTY1, "jcr:content/searchable");
				map.put(PROPERTY1OPERATION, "not");
			}
			map.put("p.limit", "-1");
			if (StringUtils.isNotBlank(searchingFor)) {
				if (LOCATIONS.equalsIgnoreCase(searchingFor)) {
					if (StringUtils.isNotBlank(locationService)
							&& !"All".equalsIgnoreCase(locationService)) {
						map.put(PROPERTY2, "jcr:content/loc_services");
						map.put(PROPERTY2VALUE, locationService);
					}
					map.put(PROPERTY3, JCRCONTENT + "/" + CQTEMPLATE);
					map.put(PROPERTY3VALUE,
							"/apps/dotcom/templates/locationdetailstemplate");
				} else if (FAQS.equalsIgnoreCase(searchingFor)) {
					map.put(PROPERTY2, JCRCONTENT + "/" + CQTEMPLATE);
					map.put(PROPERTY2VALUE,
							"/apps/dotcom/templates/faqtemplate");
				} else if (PRODUCTS.equalsIgnoreCase(searchingFor)) {
					if (StringUtils.isNotBlank(productTag)
							&& !"All".equalsIgnoreCase(productTag)) {
						map.put(PROPERTY2, "jcr:content/cq:tags");
						map.put(PROPERTY2VALUE, productTag);
					}
					map.put(PROPERTY3, JCRCONTENT + "/" + CQTEMPLATE);
					map.put(PROPERTY3VALUE,
							"/conf/dotcom-project/settings/wcm/templates/product-page");
				} else if (PEOPLE.equalsIgnoreCase(searchingFor)) {
					if (ArrayUtils.isNotEmpty(aboutMe)
							&& !aboutMeList.contains("all")) {
						int aboutMeSize = aboutMe.length;
						aboutMeList = Arrays.asList(aboutMe);
						map.put(PROPERTY2, "jcr:content/adv_specialty");
						for (int i = 0; i < aboutMeSize; i++) {
							map.put("2_property." + (i + 1) + "_value",
									aboutMeList.get(i));
						}
						map.put("2_property.or", "true");
					}
					map.put(PROPERTY3, JCRCONTENT + "/" + CQTEMPLATE);
					map.put(PROPERTY3VALUE,
							"/apps/dotcom/templates/peopleprofiletemplate");
				} else if (RESOURCE.equalsIgnoreCase(searchingFor)) {
					if (StringUtils.isNotEmpty(resourceTag)
							&& !"All".equalsIgnoreCase(resourceTag)) {
						map.put(PROPERTY2, "jcr:content/cq:tags");
						map.put(PROPERTY2VALUE, resourceTag);
						map.put(PROPERTY2, "jcr:content/primarytag");
						map.put(PROPERTY2VALUE, resourceTag);
						map.put(PROPERTY2, "jcr:content/contenttype");
						map.put(PROPERTY2VALUE, resourceTag);
					}
					map.put(PROPERTY3, JCRCONTENT + "/" + SLINGRESOURCETYPE);
					map.put(PROPERTY3VALUE,
							"dotcom/components/page/resourcecentertemplate");
				}
			}

			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "dotcomreadservice");
			resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);
			session = resourceResolver.adaptTo(Session.class);
			Query query = builder.createQuery(PredicateGroup.create(map),
					session);
			LOGGER.info("Query " + query.getPredicates());
			SearchResult result = query.getResult();
			for (Hit hit : result.getHits()) {
				resultPages.add(hit.getPath());
			}

		} catch (LoginException loginException) {
			LOGGER.error(loginException.getMessage(), loginException);
		} catch (RepositoryException repositoryException) {
			LOGGER.error(repositoryException.getMessage(), repositoryException);
		} finally {
			if (resourceResolver != null) {
				resourceResolver.close();
			}
			if (session != null && session.isLive()) {
				session.logout();
			}
		}
		return resultPages;
	}

	/**
	 * @param resultPages
	 * @param searchingFor
	 * @param currentDay
	 * @return jsonFinalObject
	 */
	public JSONObject getPropertiesOfResultPages(List<String> resultPages,
			String searchingFor, String currentDay) {
		ResourceResolver resourceResolver = null;
		ResourceResolver tagResourceResolver = null;
		String locLocationname = null;
		String locAddress = null;
		String locCity = null;
		String locState = null;
		String locStateTag = null;
		String locZipcode = null;
		String locCountry = null;
		String locLatitude = null;
		String locLongitude = null;
		String locLatitudeLongitude = null;
		String locPhone = null;
		String locFax = null;

		String[] tempLocServicesUnformatted = null;
		String locBranchHours = null;
		String locDriveInHours = null;
		String locTellerHours = null;
		String locDetailPage = null;
		String branchHours = "branchhours";
		branchHours = propertyNameBuilderForClientDate(currentDay, branchHours);
		String driveInHours = "driveinhours";
		driveInHours = propertyNameBuilderForClientDate(currentDay,
				driveInHours);
		String tellerHours = "tellerhours";
		tellerHours = propertyNameBuilderForClientDate(currentDay, tellerHours);

		String advisorTitle = null;
		String advisorTitle1 = null;
		String advisorTitle2 = null;
		String advisorCell = null;
		String advisorPhone = null;
		String advisorFirstname = null;
		String advisorNmls = null;
		String advisorAppstring = null;
		String advisorLastname = null;
		String advisorEmailaddress = null;
		String advisorSpecialty = null;
		String[] advisorAddressList = null;
		String imgFilePath = "";
		String advisorProfilePage = null;
		String advisorState = null;
		String advisorCity = null;
		String onlineContactFormDomain = null;
		String onlineContactFormAppName = null;
		String onlineContactFormEmailName = null;
		String onlineContactFormSubject = null;
		String onlineContactFormUrl = null;
		String advSpeciality = null;

		String faqAnswer = null;
		String faqQuestion = null;
		String docTitle = null;
		String docDescription = null;

		String productTitle = null;
		String productDesc = null;

		String resourceTitle = null;
		String resourceDesc = null;
		String resourcePrimaryTagRaw = null;
		String resourceContentTypeRaw = null;
		String[] resourceSecondaryTagRaw = null;
		String resourcePrimaryTag = "";
		String resourceSecondaryTag = "";
		String resourceContentType = "";
		String resourceThumbnailPath = "";

		JSONArray jsonArrayLocation = new JSONArray();
		JSONArray jsonArrayPeople = new JSONArray();
		JSONArray jsonArrayProduct = new JSONArray();
		JSONArray jsonArrayFaq = new JSONArray();
		JSONArray jsonArrayResource = new JSONArray();
		JSONArray jsonArrayAllResults = new JSONArray();
		JSONObject jsonFinalObject = new JSONObject();
		try {
			resourceResolver = serviceAgent
					.getServiceResourceResolver("dotcomreadservice");
			tagResourceResolver = serviceAgent
					.getServiceResourceResolver("dotcomreadservice");
			for (String resultPage : resultPages) {
				resourcePrimaryTag = "";
				resourceSecondaryTag = "";
				try {
					List<String> locServices = new ArrayList<String>();
					List<String> tempLocServicesWithTagId = new ArrayList<String>();
					JSONObject jsonObject = new JSONObject();
					String resultDocPath = null;
					Resource resDocPath = null;
					Resource res = resourceResolver.getResource(resultPage
							+ "/jcr:content");
					Resource resFilePath = resourceResolver
							.getResource(resultPage + "/jcr:content/image");
					if (resultPage.contains("/content/dam/")) {
						if (resultPage.contains("/jcr:content")) {
							resultDocPath = resultPage.substring(0,
									resultPage.indexOf("/jcr:content"));
						} else {
							resultDocPath = resultPage;
						}
						resDocPath = resourceResolver.getResource(resultDocPath
								+ "/jcr:content/metadata");
					}
					TagManager tagManager = tagResourceResolver
							.adaptTo(TagManager.class);
					if (StringUtils.isNotBlank(searchingFor) && res != null
							&& res.adaptTo(Node.class).hasProperty(CQTEMPLATE)) {
						if ((searchingFor.equalsIgnoreCase(LOCATIONS) || searchingFor
								.equalsIgnoreCase(ALLRESULTS))
								&& (res.adaptTo(Node.class)
										.getProperty(CQTEMPLATE).getString()
										.endsWith("locationdetailstemplate") && (res
										.adaptTo(Node.class)
										.hasProperty("loc_locationname")))) {
							ValueMap properties = res.adaptTo(ValueMap.class);
							locLocationname = properties.get(
									"loc_locationname", String.class);
							locAddress = properties.get("loc_address",
									String.class);
							locCity = properties.get("loc_city", String.class);
							locStateTag = properties.get("loc_state",
									String.class);
							locState = properties
									.get("loc_state", String.class);
							if (StringUtils.isNotBlank(locState)) {
								String stateTagID = locState.toLowerCase();
								Tag stateTag = tagManager.resolve(stateTagID
										.trim());
								if (stateTag != null) {
									locState = stateTag.getTitle();
									jsonObject.put(
											"loc_state_tag",
											locStateTag = locStateTag.replace(
													"states:", ""));
									jsonObject.put("loc_state", locState);
								} else {
									jsonObject.put("loc_state_tag", "");
									jsonObject.put("loc_state", "");
								}
							}
							locZipcode = properties.get("loc_zipcode",
									String.class);
							locCountry = properties.get("loc_country",
									String.class);
							locLatitude = properties.get("loc_latitude",
									String.class);
							locLongitude = properties.get("loc_longitude",
									String.class);
							locLatitudeLongitude = locLatitude + locLongitude;
							locPhone = properties
									.get("loc_phone", String.class);
							locFax = properties.get("loc_fax", String.class);
							tempLocServicesUnformatted = properties.get(
									"loc_services", String[].class);
							if (ArrayUtils
									.isNotEmpty(tempLocServicesUnformatted)) {
								for (String locService : tempLocServicesUnformatted) {
									Tag servicestag = tagManager
											.resolve(locService.toLowerCase()
													.trim());
									if (servicestag != null) {
										if (locServices.isEmpty())
											locServices.add(servicestag
													.getTitle());
										else
											locServices.add(" "
													+ servicestag.getTitle());
										locService = locService.replace(
												"services:", "");
										tempLocServicesWithTagId
												.add(locService);
									}
								}
							}
							if (properties.get("loc_sunbranchhours",
									String.class).equals(CLOSED)
									&& properties.get("loc_monbranchhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_tuebranchhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_wedbranchhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_thubranchhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_fribranchhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_satbranchhours",
											String.class).equals(CLOSED)) {
								locBranchHours = ALWAYSCLOSED;
							} else {
								locBranchHours = properties.get(branchHours,
										String.class);
							}

							if (properties.get("loc_sundriveinhours",
									String.class).equals(CLOSED)
									&& properties.get("loc_mondriveinhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_tuedriveinhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_weddriveinhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_thudriveinhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_fridriveinhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_satdriveinhours",
											String.class).equals(CLOSED)) {
								locDriveInHours = ALWAYSCLOSED;
							} else {
								locDriveInHours = properties.get(driveInHours,
										String.class);
							}

							if (properties.get("loc_suntellerhours",
									String.class).equals(CLOSED)
									&& properties.get("loc_montellerhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_tuetellerhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_wedtellerhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_thutellerhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_fritellerhours",
											String.class).equals(CLOSED)
									&& properties.get("loc_sattellerhours",
											String.class).equals(CLOSED)) {
								locTellerHours = ALWAYSCLOSED;
							} else {
								locTellerHours = properties.get(tellerHours,
										String.class);
							}

							locDetailPage = resultPage;

							jsonObject.put("loc_locationname", locLocationname);
							jsonObject.put("loc_address", locAddress);
							jsonObject.put("loc_city", locCity);
							jsonObject.put("loc_zipcode", locZipcode);
							jsonObject.put("loc_country", locCountry);
							jsonObject.put("loc_latitude", locLatitude);
							jsonObject.put("loc_longitude", locLongitude);
							jsonObject.put("loc_latitude_longitude",
									locLatitudeLongitude);
							jsonObject.put("loc_phone", locPhone);
							jsonObject.put("loc_fax", locFax);
							jsonObject.put("loc_services", locServices);
							jsonObject.put("loc_services_tag",
									tempLocServicesWithTagId);
							jsonObject.put("loc_branch_hours", locBranchHours);
							jsonObject.put("loc_drive_in_hours",
									locDriveInHours);
							jsonObject.put("loc_teller_hours", locTellerHours);
							locDetailPage = Utils.getPayloadUrl(locDetailPage,
									resourceResolver, settingsService,
									canonicalUrls);
							jsonObject.put("loc_detail_page", locDetailPage);
							jsonObject.put(RESULTTYPE, "location_result");
							if (LOCATIONS.equalsIgnoreCase(searchingFor))
								jsonArrayLocation.put(jsonObject);
							else if (ALLRESULTS.equalsIgnoreCase(searchingFor))
								jsonArrayAllResults.put(jsonObject);
						} else if ((PEOPLE.equalsIgnoreCase(searchingFor) || ALLRESULTS
								.equalsIgnoreCase(searchingFor))
								&& (res.adaptTo(Node.class)
										.getProperty(CQTEMPLATE).getString()
										.endsWith("peopleprofiletemplate") && (res
										.adaptTo(Node.class)
										.hasProperty("adv_firstname")))) {
							ValueMap properties = res.adaptTo(ValueMap.class);
							ValueMap filePathProp = null;
							if (resFilePath != null) {
								filePathProp = resFilePath
										.adaptTo(ValueMap.class);
								imgFilePath = filePathProp.get("fileReference",
										String.class);
							}
							List<String> advDesignation = new ArrayList<String>();
							String[] advisorDesignationcodes = null;
							String advisorDesignationcode = null;

							advisorTitle = properties.get("jcr:title",
									String.class);
							advisorTitle1 = properties.get("adv_title1",
									String.class);
							advisorTitle2 = properties.get("adv_title2",
									String.class);
							advisorCell = properties.get("adv_cell",
									String.class);
							advisorPhone = properties.get("adv_phone",
									String.class);
							advisorFirstname = properties.get("adv_firstname",
									String.class);
							advisorNmls = properties.get("adv_nmls",
									String.class);
							advisorAppstring = properties.get("adv_appstring",
									String.class);
							advisorLastname = properties.get("adv_lastname",
									String.class);
							advisorEmailaddress = properties.get(
									"adv_emailaddress", String.class);
							advisorSpecialty = properties.get("adv_specialty",
									String.class);
							advisorDesignationcodes = properties.get(
									"adv_designationcodes", String[].class);
							advisorAddressList = properties.get(
									"adv_addressItems", String[].class);

							StringBuilder sbAddress = new StringBuilder(
									"{\"addressListArray\":[");
							for (int j = 0; j < advisorAddressList.length; j++) {
								sbAddress.append(advisorAddressList[j]);
								if (j < (advisorAddressList.length - 1)) {
									sbAddress.append(",");
								}
							}
							sbAddress.append("]}");
							String adrBuffer = sbAddress.toString().replaceAll(
									"\"", "\\\"");
							JSONObject addressObj = new JSONObject(adrBuffer);
							JSONArray addressArray = new JSONArray();
							addressArray.put(0, addressObj);
							jsonObject.put("advisor_addressListArray",
									addressObj);

							StringBuilder stringBuilder = new StringBuilder(
									"{\"lat_log_array\":[");
							for (int j = 0; j < advisorAddressList.length; j++) {
								stringBuilder.append(advisorAddressList[j]);
								if (j < (advisorAddressList.length - 1)) {
									stringBuilder.append(",");
								}
							}
							stringBuilder.append("]}");
							String strBuffer = stringBuilder.toString()
									.replaceAll("\"", "\\\"");
							JSONObject jObject = new JSONObject(strBuffer);
							JSONArray jArray = jObject
									.getJSONArray("lat_log_array");
							JSONObject llObject = new JSONObject();
							JSONObject jObj = new JSONObject();
							JSONArray tObj = new JSONArray();

							for (int i = 0; i < jArray.length(); i++) {
								jObj = jArray.getJSONObject(i);
								String strConcat = jObj
										.getDouble("adv_latitude")
										+ ""
										+ jObj.getDouble("adv_longitude");
								tObj.put(i, strConcat);
							}
							llObject.put("lat_long", tObj);

							advisorCity = properties.get("adv_cities",
									String.class);
							advisorState = properties.get("adv_state",
									String.class);
							advisorProfilePage = resultPage;
							jsonObject.put("advisor_title", advisorTitle);
							jsonObject.put("advisor_title1", advisorTitle1);
							jsonObject.put("advisor_title2", advisorTitle2);
							jsonObject.put("advisor_cell", advisorCell);
							jsonObject.put("advisor_phone", advisorPhone);
							jsonObject.put("advisor_firstname",
									advisorFirstname);
							jsonObject.put("advisor_nmls", advisorNmls);
							jsonObject.put("advisor_appstring",
									advisorAppstring);
							jsonObject.put("advisor_lastname", advisorLastname);
							jsonObject.put("advisor_emailaddress",
									advisorEmailaddress);
							if (StringUtils.isNotBlank(advisorSpecialty)) {
								advSpeciality = advisorSpecialty.split(":").length > 1 ? advisorSpecialty
										.split(":")[1] : null;
								if (advisorConfigService != null
										&& StringUtils
												.isNotBlank(advSpeciality)) {
									onlineContactFormDomain = advisorConfigService
											.getPropertyValue("advisor.form.url");
									onlineContactFormAppName = advisorConfigService
											.getPropertyValue(advSpeciality
													+ ".appname");
									onlineContactFormEmailName = advisorConfigService
											.getPropertyValue(advSpeciality
													+ ".emailformname");
									onlineContactFormSubject = advisorConfigService
											.getPropertyValue(advSpeciality
													+ ".subject");
									onlineContactFormUrl = onlineContactFormDomain
											+ "?appname="
											+ onlineContactFormAppName
											+ "&formName="
											+ onlineContactFormEmailName
											+ "&subject="
											+ onlineContactFormSubject
											+ "&sendTo=" + advisorEmailaddress;
								}
								String advSpecialityTagID = advisorSpecialty
										.toLowerCase();
								Tag advisorSpecialityTagTitle = tagManager
										.resolve(advSpecialityTagID.trim());
								if (advisorSpecialityTagTitle != null)
									advisorSpecialty = advisorSpecialityTagTitle
											.getTitle();
							}
							if (advisorDesignationcodes != null) {
								for (String eachTag : advisorDesignationcodes) {
									Tag designationTag = tagManager
											.resolve(eachTag);
									if (designationTag != null)
										advDesignation.add(designationTag
												.getTitle());
								}
							}
							advisorDesignationcode = String.join(", ",
									advDesignation);
							jsonObject.put("advisor_designationcodes",
									advisorDesignationcode);
							jsonObject.put("advisor_specialty",
									advisorSpecialty);
							jsonObject.put("advisor_onlineContactFormUrl",
									onlineContactFormUrl);
							jsonObject.put("advisor_addressList", llObject);
							jsonObject.put("advisor_pictureFilePath",
									imgFilePath);
							advisorProfilePage = Utils.getPayloadUrl(
									advisorProfilePage, resourceResolver,
									settingsService, canonicalUrls);
							jsonObject.put("advisor_profilePage",
									advisorProfilePage);
							jsonObject.put("advisor_city", advisorCity);
							jsonObject.put(RESULTTYPE, "people_result");
							if (StringUtils.isNotBlank(advisorState)) {
								String advStateTagID = advisorState
										.toLowerCase();
								Tag advisorStateTag = tagManager
										.resolve(advStateTagID.trim());
								if (advisorStateTag != null)
									advisorState = advisorStateTag.getTitle();
							}
							jsonObject.put("advisor_state", advisorState);
							if (PEOPLE.equalsIgnoreCase(searchingFor))
								jsonArrayPeople.put(jsonObject);
							else if (ALLRESULTS.equalsIgnoreCase(searchingFor))
								jsonArrayAllResults.put(jsonObject);
						} else if ((PRODUCTS.equalsIgnoreCase(searchingFor) || ALLRESULTS
								.equalsIgnoreCase(searchingFor))
								&& (res.adaptTo(Node.class)
										.getProperty(CQTEMPLATE).getString()
										.endsWith("product-page"))) {
							ValueMap properties = res.adaptTo(ValueMap.class);
							productTitle = properties.get("pageTitle", "");
							if (StringUtils.isBlank(productTitle)) {
								productTitle = properties.get("jcr:title", "");
							}
							productDesc = properties.get("jcr:description", "");
							jsonObject.put("product_title", productTitle);
							jsonObject.put("product_desc", productDesc);
							resultPage = Utils.getPayloadUrl(resultPage,
									resourceResolver, settingsService,
									canonicalUrls);
							jsonObject.put("product_page", resultPage);
							jsonObject.put(RESULTTYPE, "product_result");
							if (PRODUCTS.equalsIgnoreCase(searchingFor))
								jsonArrayProduct.put(jsonObject);
							else if (ALLRESULTS.equalsIgnoreCase(searchingFor))
								jsonArrayAllResults.put(jsonObject);
						} else if ((FAQS.equalsIgnoreCase(searchingFor) || ALLRESULTS
								.equalsIgnoreCase(searchingFor))
								&& (res.adaptTo(Node.class)
										.getProperty(CQTEMPLATE).getString()
										.endsWith("faqtemplate"))) {
							ValueMap properties = res.adaptTo(ValueMap.class);
							faqQuestion = properties.get("question",
									String.class);
							faqAnswer = properties.get("answer", String.class);
							jsonObject.put("faq_question", faqQuestion);
							jsonObject.put("faq_answer", faqAnswer);
							jsonObject.put(RESULTTYPE, "faq_result");
							if (FAQS.equalsIgnoreCase(searchingFor))
								jsonArrayFaq.put(jsonObject);
							else if (ALLRESULTS.equalsIgnoreCase(searchingFor))
								jsonArrayAllResults.put(jsonObject);
						} else if ((RESOURCE.equalsIgnoreCase(searchingFor) || ALLRESULTS
								.equalsIgnoreCase(searchingFor))
								&& (res.adaptTo(Node.class).hasProperty(
										SLINGRESOURCETYPE) && res
										.adaptTo(Node.class)
										.getProperty(SLINGRESOURCETYPE)
										.getString()
										.endsWith("resourcecentertemplate"))) {
							ValueMap properties = res.adaptTo(ValueMap.class);
							ValueMap resourceThumbnailPathProp = null;
							resourceTitle = properties.get("jcr:title", "");
							resourceDesc = properties
									.get("jcr:description", "");
							resourcePrimaryTagRaw = properties.get(
									"primarytag", "");
							resourceSecondaryTagRaw = properties.get("cq:tags",
									new String[] {});
							resourceContentTypeRaw = properties.get(
									"contenttype", "");
							if (ArrayUtils.isNotEmpty(resourceSecondaryTagRaw)) {
								for (int i = 0; i < resourceSecondaryTagRaw.length; i++) {
									if (resourceSecondaryTagRaw[i]
											.startsWith("resource-center")) {
										if (StringUtils
												.isNotBlank(resourceSecondaryTag))
											resourceSecondaryTag = resourceSecondaryTag
													+ ","
													+ getTagTitle(
															resourceSecondaryTagRaw[i],
															tagManager);
										else
											resourceSecondaryTag = getTagTitle(
													resourceSecondaryTagRaw[i],
													tagManager);
									}
								}
							}
							resourcePrimaryTag = getTagTitle(
									resourcePrimaryTagRaw, tagManager);
							resourceContentType = getTagTitle(
									resourceContentTypeRaw, tagManager);
							if (resFilePath != null) {
								resourceThumbnailPathProp = resFilePath
										.adaptTo(ValueMap.class);
								resourceThumbnailPath = resourceThumbnailPathProp
										.get("fileReference", String.class);
							}
							jsonObject.put("resource_title", resourceTitle);
							jsonObject.put("resource_desc", resourceDesc);
							resultPage = Utils.getPayloadUrl(resultPage,
									resourceResolver, settingsService,
									canonicalUrls);
							jsonObject.put("resource_page", resultPage);
							jsonObject.put("resource_thumbnail_image",
									resourceThumbnailPath);
							jsonObject.put("resource_primary_tag",
									resourcePrimaryTag);
							jsonObject.put("resource_secondary_tag",
									resourceSecondaryTag);
							jsonObject.put("resource_content_type",
									resourceContentType);
							jsonObject.put(RESULTTYPE, "resource_result");
							if (RESOURCE.equalsIgnoreCase(searchingFor))
								jsonArrayResource.put(jsonObject);
							else if (ALLRESULTS.equalsIgnoreCase(searchingFor))
								jsonArrayAllResults.put(jsonObject);
						}
					} else if (StringUtils.isNotBlank(searchingFor)
							&& resDocPath != null) {
						ValueMap properties = resDocPath
								.adaptTo(ValueMap.class);
						docTitle = properties.get("pdf:Title", String.class);
						if (StringUtils.isBlank(docTitle))
							docTitle = properties.get("dc:title", String.class);
						if (StringUtils.isBlank(docTitle))
							docTitle = properties
									.get("jcr:title", String.class);
						if (StringUtils.isBlank(docTitle))
							docTitle = resourceResolver
									.getResource(resultDocPath + "/jcr:content")
									.adaptTo(ValueMap.class)
									.get("cq:name", String.class);
						docDescription = properties.get("dc:description",
								String.class);
						jsonObject.put("doc_title", docTitle);
						jsonObject.put("doc_description", docDescription);
						jsonObject.put("doc_path", resultDocPath);
						jsonObject.put(RESULTTYPE, "document_result");
						jsonArrayAllResults.put(jsonObject);
					}
				} catch (Exception exception) {
					LOGGER.error(exception.getMessage(), exception);
				}
			}
			Set<String> docPaths = new HashSet<String>();
			JSONArray tempArray = new JSONArray();
			String docPath = null;
			for (int i = 0; i < jsonArrayAllResults.length(); i++) {
				if (jsonArrayAllResults.getJSONObject(i).has("doc_path")) {
					docPath = jsonArrayAllResults.getJSONObject(i).getString(
							"doc_path");
					if (docPaths.contains(docPath)) {
						continue;
					} else {
						docPaths.add(docPath);
						tempArray.put(jsonArrayAllResults.getJSONObject(i));
					}
				} else {
					tempArray.put(jsonArrayAllResults.getJSONObject(i));

				}

			}

			jsonArrayAllResults = tempArray;
			jsonFinalObject.put("all_results", jsonArrayAllResults);
			jsonFinalObject.put("location_results", jsonArrayLocation);
			jsonFinalObject.put("people_results", jsonArrayPeople);
			jsonFinalObject.put("product_results", jsonArrayProduct);
			jsonFinalObject.put("resource_results", jsonArrayResource);
			jsonFinalObject.put("faq_results", jsonArrayFaq);

		} catch (JSONException | LoginException | RepositoryException jsonException) {
			LOGGER.error(jsonException.getMessage(), jsonException);
		} catch (Exception exception) {
			LOGGER.error(exception.getMessage(), exception);
		} finally {
			resourceResolver.close();
			tagResourceResolver.close();
		}
		return jsonFinalObject;
	}

	public String getTagTitle(String resourcePrimaryTagRaw,
			TagManager tagManager) {
		String resourcePrimaryTag = "";
		if (StringUtils.isNotBlank(resourcePrimaryTagRaw)) {
			if (resourcePrimaryTagRaw.contains("/")) {
				for (int i = 1; i <= StringUtils.countMatches(
						resourcePrimaryTagRaw, "/") + 1; i++) {
					String resourcePrimaryTagSplited = null;
					if (i == StringUtils.countMatches(resourcePrimaryTagRaw,
							"/") + 1)
						resourcePrimaryTagSplited = resourcePrimaryTagRaw
								.substring(0, resourcePrimaryTagRaw.length());
					else
						resourcePrimaryTagSplited = resourcePrimaryTagRaw
								.substring(0, StringUtils.ordinalIndexOf(
										resourcePrimaryTagRaw, "/", i));
					Tag primaryTag = tagManager
							.resolve(resourcePrimaryTagSplited.toLowerCase()
									.trim());
					if (primaryTag != null) {
						if (StringUtils.isBlank(resourcePrimaryTag))
							resourcePrimaryTag = primaryTag.getTitle() + "/";
						else
							resourcePrimaryTag = resourcePrimaryTag
									+ primaryTag.getTitle() + "/";
					}
				}
				if (StringUtils.isNotBlank(resourcePrimaryTag))
					resourcePrimaryTag = resourcePrimaryTag.substring(0,
							resourcePrimaryTag.length() - 1);
			} else {
				Tag primaryTag = tagManager.resolve(resourcePrimaryTagRaw
						.toLowerCase().trim());
				if (primaryTag != null)
					resourcePrimaryTag = primaryTag.getTitle();
			}
		} else
			resourcePrimaryTag = resourcePrimaryTagRaw;
		return resourcePrimaryTag;
	}

	/**
	 * @param currentDay
	 * @param categoryType
	 * @return formattedCategoryType
	 */
	public String propertyNameBuilderForClientDate(String currentDay,
			String categoryType) {
		String formattedCategoryType = null;
		if ("0".equalsIgnoreCase(currentDay)) {
			formattedCategoryType = "loc_sun" + categoryType;
		} else if ("1".equalsIgnoreCase(currentDay)) {
			formattedCategoryType = "loc_mon" + categoryType;
		} else if ("2".equalsIgnoreCase(currentDay)) {
			formattedCategoryType = "loc_tue" + categoryType;
		} else if ("3".equalsIgnoreCase(currentDay)) {
			formattedCategoryType = "loc_wed" + categoryType;
		} else if ("4".equalsIgnoreCase(currentDay)) {
			formattedCategoryType = "loc_thu" + categoryType;
		} else if ("5".equalsIgnoreCase(currentDay)) {
			formattedCategoryType = "loc_fri" + categoryType;
		} else if ("6".equalsIgnoreCase(currentDay)) {
			formattedCategoryType = "loc_sat" + categoryType;
		}
		return formattedCategoryType;
	}
}
