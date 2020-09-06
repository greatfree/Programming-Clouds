package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class IsCacheEmptyInPointingStoreResponse extends ServerMessage
{
	private static final long serialVersionUID = 1898569224146415441L;
	
	private boolean isEmpty;

	public IsCacheEmptyInPointingStoreResponse(boolean isEmpty)
	{
		super(TestCacheMessageType.IS_CACHE_EMPTY_REQUEST);
		this.isEmpty = isEmpty;
	}

	public boolean isEmpty()
	{
		return this.isEmpty;
	}
}
