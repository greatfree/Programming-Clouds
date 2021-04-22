package org.greatfree.framework.cps.cache.message.prefetch;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.UtilConfig;

// Created: 08/09/2018, Bing Li
public class PopMyStoreDataRequest extends ServerMessage
{
	private static final long serialVersionUID = -999041350668645589L;
	
	private String stackKey;
	private int count;
	
	private boolean isAccessReadStack;
	
	private int startIndex;
	private int endIndex;
	
	private boolean isPeeking;

//	public PopMyStoreDataRequest(String stackKey, int count, boolean isReading)
	public PopMyStoreDataRequest(String stackKey, int count, boolean isReading, boolean isPeeking)
	{
		super(TestCacheMessageType.POP_MY_STORE_DATA_REQUEST);
		this.stackKey = stackKey;
		this.count = count;
		this.isAccessReadStack = isReading;
		this.isPeeking = isPeeking;
		this.startIndex = UtilConfig.NO_INDEX;
		this.endIndex = UtilConfig.NO_INDEX;
	}
	
	public PopMyStoreDataRequest(String stackKey, int startIndex, int endIndex)
	{
		super(TestCacheMessageType.POP_MY_STORE_DATA_REQUEST);
		this.stackKey = stackKey;
		this.count = UtilConfig.NO_COUNT;
		this.isAccessReadStack = false;
		this.isPeeking = true;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public String getStackKey()
	{
		return this.stackKey;
	}
	
	public int getCount()
	{
		return this.count;
	}
	
	public boolean isReading()
	{
		return this.isAccessReadStack;
	}

	public boolean isPeeking()
	{
		return this.isPeeking;
	}
	
	public int getStartIndex()
	{
		return this.startIndex;
	}
	
	public int getEndIndex()
	{
		return this.endIndex;
	}
}
