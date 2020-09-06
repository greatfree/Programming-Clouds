package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class CachePointingByKeyRequest extends ServerMessage
{
	private static final long serialVersionUID = -3127159460502042376L;
	
	private String mapKey;
	private String resourceKey;
	private boolean isTiming;

	public CachePointingByKeyRequest(String mapKey, String resourceKey, boolean isTiming)
	{
		super(TestCacheMessageType.CACHE_POINTING_BY_KEY_REQUEST);
		this.mapKey = mapKey;
		this.resourceKey = resourceKey;
		this.isTiming = isTiming;
	}
	
	public String getMapKey()
	{
		return this.mapKey;
	}
	
	public String getResourceKey()
	{
		return this.resourceKey;
	}

	public boolean isTiming()
	{
		return this.isTiming;
	}
}
