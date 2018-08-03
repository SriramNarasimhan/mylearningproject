package com.suntrust.dotcom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.suntrust.dotcom.beans.DynamicListItemsBean;
import com.suntrust.dotcom.utils.TitleAscendingOrderSort;

public class TestTitleAscendingOrderSortPositive {
	
	TitleAscendingOrderSort titleAscendingOrderSort = new TitleAscendingOrderSort();
	private List<DynamicListItemsBean> unSortedBean  = new ArrayList();
	private List<DynamicListItemsBean> sortedBean  = new ArrayList();
	private DynamicListItemsBean iBean1 = new DynamicListItemsBean();
	private DynamicListItemsBean iBean2 = new DynamicListItemsBean();
	private DynamicListItemsBean iBean3 = new DynamicListItemsBean();
	@SuppressWarnings("unchecked")
	@Test
	public void testComparePositive() {
			iBean1.setTitle("Apple");
			iBean2.setTitle("Banana");
			iBean3.setTitle("Cucumber");
			iBean1.setValueType("UnPinnedItem");
			iBean2.setValueType("UnPinnedItem");
			iBean3.setValueType("UnPinnedItem");
			unSortedBean.add(iBean2);
			unSortedBean.add(iBean1);
			unSortedBean.add(iBean3);
			sortedBean.add(iBean1);
			sortedBean.add(iBean2);
			sortedBean.add(iBean3);	
			Collections.sort(unSortedBean, titleAscendingOrderSort);
			Assert.assertArrayEquals(sortedBean.toArray(), unSortedBean.toArray());					
	}
}
