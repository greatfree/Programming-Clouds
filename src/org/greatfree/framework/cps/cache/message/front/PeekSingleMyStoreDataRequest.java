package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/09/2018, Bing Li
public class PeekSingleMyStoreDataRequest extends ServerMessage
{
	private static final long serialVersionUID = 3482770291315184091L;
	
	private String stackKey;
	
	private boolean isReading;

	public PeekSingleMyStoreDataRequest(String stackKey, boolean isReading)
	{
		super(TestCacheMessageType.PEEK_SINGLE_MY_STORE_DATA_REQUEST);
		this.stackKey = stackKey;
		this.isReading = isReading;
	}

	public String getStackKey()
	{
		return this.stackKey;
	}
	
	public boolean isReading()
	{
		return this.isReading;
	}
}
