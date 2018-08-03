package com.suntrust.dotcom.utils;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.beans.DynamicListItemsBean;

/**
 * TitleDescendingOrderSort sorts string based on descending order.
 * 
 * @author Cognizant
 * @version 1.0
 * @since 10 July 2017
 * 
 */
public class TitleDescendingOrderSort implements Comparator {
	/**
	 * Compares its two DynamicListItemsBean arguments for order.
	 */
	public int compare(Object arg0, Object arg1) {
		String item1 = null;
		String item2 = null;
		int flag = 0;
		DynamicListItemsBean dynamicListItemsBean0 = (DynamicListItemsBean) arg0;
		DynamicListItemsBean dynamicListItemsBean1 = (DynamicListItemsBean) arg1;
		item1 = StringUtils.isBlank(dynamicListItemsBean0.getTitle()) ? dynamicListItemsBean0
				.getPageTitle() : dynamicListItemsBean0.getTitle();
		item2 = StringUtils.isBlank(dynamicListItemsBean1.getTitle()) ? dynamicListItemsBean1
				.getPageTitle() : dynamicListItemsBean1.getTitle();
		if (StringUtils.isNotBlank(item1) && StringUtils.isNotBlank(item2)
				&& dynamicListItemsBean0.getValueType().equals("UnPinnedItem")
				&& dynamicListItemsBean1.getValueType().equals("UnPinnedItem")) {
			flag = item2.compareTo(item1);
		}
		return flag;
	}
}
