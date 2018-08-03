package com.suntrust.dotcom.utils;

import com.adobe.granite.workflow.collection.util.ResultSet;

public class ResultSetImpl<T> implements ResultSet<T>
{
	  private T[] items;
	  private long totalCount;
	  
	  public ResultSetImpl(T[] items, long totalCount)
	  {
	    this.items = items;
	    this.totalCount = totalCount;
	  }
	  
	  public T[] getItems()
	  {
	    return this.items;
	  }
	  
	  public long getTotalSize()
	  {
	    return this.totalCount;
	  }
	}