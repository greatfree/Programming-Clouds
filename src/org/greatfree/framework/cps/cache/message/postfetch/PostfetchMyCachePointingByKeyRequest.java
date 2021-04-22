package org.greatfree.framework.cps.cache.message.postfetch;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/25/2018, Bing Li
public class PostfetchMyCachePointingByKeyRequest extends ServerMessage
{
	private static final long serialVersionUID = 6984638981529503037L;
	
	private String mapKey;
	private String resourceKey;
	
	private boolean isTiming;

	private boolean isTerminalMap;

	public PostfetchMyCachePointingByKeyRequest(String mapKey, String resourceKey, boolean isTiming, boolean isTerminalMap)
	{
		super(TestCacheMessageType.POSTFETCH_MY_CACHE_POINTING_BY_KEY_REQUEST);
		this.mapKey = mapKey;
		this.resourceKey = resourceKey;
		this.isTiming = isTiming;
		this.isTerminalMap = isTerminalMap;
	}
	
	public boolean isTerminalMap()
	{
		return this.isTerminalMap;
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
