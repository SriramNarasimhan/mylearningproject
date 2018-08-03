package com.suntrust.dotcom.utils;

/** * 
 * Purpose - The AnalyticsEnum program provides analytic specific constants values.
 * @author UGRK104
 */

public enum AnalyticsEnum {
	
	
	STCOM_DOTCOM_AEM_ANALYTIC_PREFIX("STcom"),
	STCOM_DFS_AEM_ANALYTIC_PREFIX("NewAccountStart"),
	STCOM_DOTCOM_AEM_HOMEPAGE("Home"),
	CONTENT_SUNTRUST_DOTCOM_US_EN("content/suntrust/dotcom/us/en/"),
	CONTENT_SUNTRUST_DOTCOM_US_ES("content/suntrust/dotcom/us/es/"),
	CONTENT_SUNTRUST_DOTCOM_US_EN_LOCATIONS("content/suntrust/dotcom/us/en/locations/"),
	CONTENT_SUNTRUST_DOTCOM_US_ES_LOCATIONS("content/suntrust/dotcom/us/es/locations/"),
	CONTENT_SUNTRUST_DOTCOM_US_CONFIGURATION("content/suntrust/dotcom/us/configuration/"),
	STCOM_DOTCOM_AEM_ANALYTIC_PAGE_SET("Static"),
	STCOM_DOTCOM_AEM_ANALYTIC_TAXONOMY_PAGE_CLASS("AEM Taxonomy - Page class"),
	PRODUCTS_TAG_NAMESPACE_NAME("product"),
	LOB_TAG_NAMESPACE_NAME("line-of-business"),
	RESOURCE_CENTER_TAG_NAMESPACE_NAME("resource-center"),
	CONTENT_TYPE_TAG_NAMESPACE_NAME("content-type"),
	SEGMENT_TAG_NAMESPACE_NAME("advisor-specialty");

	/** instance variable to hold enum value. * */
    private String value;

    /**
     * @param string
     */
    AnalyticsEnum(String string) {
        this.value = string;
    }

    /**
     * @return enum value
     */
    public String getValue() {
        return this.value;
    }

}