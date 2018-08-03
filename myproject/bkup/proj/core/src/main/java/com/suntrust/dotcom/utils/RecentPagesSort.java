package com.suntrust.dotcom.utils;

import java.util.Comparator;

import com.suntrust.dotcom.beans.DynamicListItemsBean;

/**
 * RecentPagesSort sorts the dates based on recently modified date.
 * 
 * @author Cognizant
 * @version 1.0
 * @since 10 July 2017
 * 
 */
public class RecentPagesSort  implements Comparator  {
	
	/**
	 * Compares its two DynamicListItemsBean arguments for order.
	 */
    public int compare(Object arg0, Object arg1) {
    	DynamicListItemsBean dynamicListItemsBean0 = (DynamicListItemsBean) arg0;
    	DynamicListItemsBean dynamicListItemsBean1= (DynamicListItemsBean) arg1;
    	if(dynamicListItemsBean0.getValueType().equals("UnPinnedItem") && dynamicListItemsBean1.getValueType().equals("UnPinnedItem")){
	        if(dynamicListItemsBean1.getReplicationDate() == null || dynamicListItemsBean0.getReplicationDate() == null )
	        	return -1;
	        if (dynamicListItemsBean1.getReplicationDate().equals(dynamicListItemsBean0.getReplicationDate()))
	            return 0;
	        return dynamicListItemsBean1.getReplicationDate().compareTo(dynamicListItemsBean0.getReplicationDate());
    	}
    	return 0;
    }
}
