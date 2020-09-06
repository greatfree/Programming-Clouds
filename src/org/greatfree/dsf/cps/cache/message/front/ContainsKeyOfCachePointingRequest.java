package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class ContainsKeyOfCachePointingRequest extends ServerMessage
{
	private static final long serialVersionUID = -5305119849113013415L;
	
	private String mapKey;
	private String resourceKey;
	private boolean isTiming;

	public ContainsKeyOfCachePointingRequest(String mapKey, String resourceKey, boolean isTiming)
	{
		super(TestCacheMessageType.CONTAINS_KEY_OF_CACHE_POINTING_REQUEST);
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
