package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class MaxCachePointingRequest extends ServerMessage
{
	private static final long serialVersionUID = -4849093447267621996L;
	
	private String mapKey;
	private boolean isTiming;

	public MaxCachePointingRequest(String mapKey, boolean isTiming)
	{
		super(TestCacheMessageType.MAX_CACHE_POINTING_REQUEST);
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
