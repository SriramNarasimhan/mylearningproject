package com.suntrust.dotcom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.suntrust.dotcom.beans.DynamicListItemsBean;
import com.suntrust.dotcom.utils.RecentPagesSort;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TestRecentPagesSortNegative {
	
	RecentPagesSort recentPagesSort = new RecentPagesSort();
	private List<DynamicListItemsBean> unSortedBean  = new ArrayList();
	private List<DynamicListItemsBean> sortedBean  = new ArrayList();
	private DynamicListItemsBean iBean1 = new DynamicListItemsBean();
	private DynamicListItemsBean iBean2 = new DynamicListItemsBean();
	private DynamicListItemsBean iBean3 = new DynamicListItemsBean();
		
		
	@SuppressWarnings("unchecked")
	@Test
	public void testCompareNegative() {
		try {
			iBean1.setReplicationDate((new SimpleDateFormat("dd/MM/yyyy").parse("15/04/2017")));
			iBean2.setReplicationDate((new SimpleDateFormat("dd/MM/yyyy").parse("15/05/2017")));
			iBean3.setReplicationDate((new SimpleDateFormat("dd/MM/yyyy").parse("15/04/2016")));
			iBean1.setValueType("UnPinnedItem");
			iBean2.setValueType("UnPinnedItem");
			iBean3.setValueType("UnPinnedItem");
			unSortedBean.add(iBean1);
			unSortedBean.add(iBean2);
			unSortedBean.add(iBean3);
			sortedBean.add(iBean1);
			sortedBean.add(iBean2);
			sortedBean.add(iBean3);	
			Collections.sort(unSortedBean, recentPagesSort);
			assertThat(unSortedBean, not(sortedBean));
			//Assert.assertArrayEquals(sortedBean.toArray(), unSortedBean.toArray());
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
	}
}
