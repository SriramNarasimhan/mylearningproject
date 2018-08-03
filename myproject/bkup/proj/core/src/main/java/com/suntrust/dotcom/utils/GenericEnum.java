package com.suntrust.dotcom.utils;

/**
 * Purpose - The GenericEnum program provides dotcom global specific constants values.
 * 
 * @author UGRK104
 *
 */
public enum GenericEnum {
	
	JCR_CONTENT("jcr:content"),
	CQ_PAGE_CONTENT("cq:PageContent"),
	SLING_RESOURCE_TYPE("sling:resourceType"),
	PAR("par"),
	PAR_COMPONENT_PATH("foundation/components/parsys"),
	ITERATIVE_PAGE("iterativePage"),
	INDEX_VALUE("indexValue"),
	COUNT_VALUE("countValue"),
	PAGE_PATH("pagePath"),
	NODE_NAME("nodeName"),
	PARENT_PAGE_PATH("parentPagePath"),
	TEXT("text"),
	LINK_URL("linkURL"),
	URL_PARAMS("urlParams"),
	ANCHOR_TAG("anchorTag"),
	ABSOLUTE_PARENT_LEVEL("4"),
	COMPANY("suntrust"),
	DOTCOM_SITE_ROOT("dotcom"),
	SPANISH_LANGUAGE_CODE("es"),
	SPANISH_LANGUAGE("Spanish"),
	ENGLISH_LANGUAGE_CODE("en"),
	ENGLISH_LANGUAGE("English"),
	QUERYSTRING_QUESTION_MARK("?"),
	PIPE_SYMBOL("|"),
	COLON_SYMBOL(":"),
	DASH_SYMBOL("-"),
	DOT_SYMBOL("."),
	SEMI_COLON_SYMBOL(";"),
	BACKWORD_SLASH_SYMBOL("/"),
	HTML_EXTENSION(".html"),
	HTTP_PROTOCOL("http"),
	HTTPS_PROTOCOL("https"),
	COMMA_SYMBOL(","),
	EMPTY_STRING(""),
	EMPTY_SPACE_STRING(" "),
	ITEMS("Items"),
	DIALOG_TAB_COUNT("10"),
	SPEEDBUMP_PAGE_NAME("Speedbump"),
	HASH_SYMBOL("#"),
	TEL_SYMBOL("tel:"),
	SENDER_EMAIL_ADDRESS("cms@SunTrust.com"),
	SENDER_NAME("Suntrust CMS"),
	CONTENT_INTAKE_FORM_PATH("content.intake.form.path"),
	CONTENT_INTAKE_FORM_ASSET_UPLOAD_PATH("content.intake.form.asset.upload.path"),
	CONTENT_FORM_EMAIL_NOTIFICATION_TEMPLATE_PATH("content.form.email.notification.template.path"),
	DOTCOM_BLANK_TEMPLATE_PATH("dotcom.blank.template.path"),
	CONTENT_REQUEST_FORM_DATA_TEMPLATE_PATH("content.request.form.data.template.path"),	
	DL_DIGITAL_CONTENT_MGMT_EMAIL_ID("DL.DigitalContentMgmt"),
	US_ZIP_CODE_REGEX("^[0-9]{5}(?:-[0-9]{4})?$"),
	SEARCH_RESULT_TEXT("{0} Results for {1}"),
	NAC_REST_SERVICE_API_URL("nac.rate.rest.service.api.url");

	/** instance variable to hold enum value. * */
    private String value;

    /**
     * @param string {@link String}
     */
    GenericEnum(String string) {
        this.value = string;
    }

    /**
     * @return enum value
     */
    public String getValue() {
        return this.value;
    }

}
