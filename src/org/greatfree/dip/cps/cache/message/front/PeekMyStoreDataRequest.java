package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.UtilConfig;

// Created: 08/09/2018, Bing Li
public class PeekMyStoreDataRequest extends ServerMessage
{
	private static final long serialVersionUID = 795568419067746034L;
	
	private String stackKey;
	private int count;
	
	private int startIndex;
	private int endIndex;
	
	private boolean isReading;

	public PeekMyStoreDataRequest(String stackKey, int count, boolean isReading)
	{
		super(TestCacheMessageType.PEEK_MY_STORE_DATA_REQUEST);
		this.stackKey = stackKey;
		this.count = count;
		this.startIndex = UtilConfig.NO_INDEX;
		this.endIndex = UtilConfig.NO_INDEX;
		this.isReading = isReading;
	}
	
	public PeekMyStoreDataRequest(String stackKey, int startIndex, int endIndex, boolean isReading)
	{
		super(TestCacheMessageType.PEEK_MY_STORE_DATA_REQUEST);
		this.stackKey = stackKey;
		this.count = endIndex - startIndex + 1;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.isReading = isReading;
	}
	
	public PeekMyStoreDataRequest(String stackKey, boolean isReading, int index)
	{
		super(TestCacheMessageType.PEEK_MY_STORE_DATA_REQUEST);
		this.stackKey = stackKey;
		this.count = 1;
		this.startIndex = index;
		this.endIndex = index;
		this.isReading = isReading;
	}

	public String getStackKey()
	{
		return this.stackKey;
	}
	
	public int getCount()
	{
		return this.count;
	}
	
	public int getStartIndex()
	{
		return this.startIndex;
	}
	
	public int getEndIndex()
	{
		return this.endIndex;
	}
	
	public boolean isReading()
	{
		return this.isReading;
	}
}
