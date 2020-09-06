package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class IsCacheEmptyInPointingStoreRequest extends ServerMessage
{
	private static final long serialVersionUID = -1746531475127685513L;
	
	private String mapKey;
	private boolean isTiming;

	public IsCacheEmptyInPointingStoreRequest(String mapKey, boolean isTiming)
	{
		super(TestCacheMessageType.IS_CACHE_EMPTY_REQUEST);
		this.mapKey = mapKey;
		this.isTiming = isTiming;
	}

	public String getMapKey()
	{
		return this.mapKey;
	}
	
	public boolean isTiming()
	{
		return this.isTiming;
	}
}
