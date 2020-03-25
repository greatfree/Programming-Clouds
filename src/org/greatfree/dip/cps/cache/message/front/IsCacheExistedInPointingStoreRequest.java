package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class IsCacheExistedInPointingStoreRequest extends ServerMessage
{
	private static final long serialVersionUID = 9181369274874885957L;
	
	private String mapKey;
	private boolean isTiming;

	public IsCacheExistedInPointingStoreRequest(String mapKey, boolean isTiming)
	{
		super(TestCacheMessageType.IS_CACHE_EXISTED_REQUEST);
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
