package org.greatfree.framework.cps.cache.message.front;

import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/20/2018, Bing Li
public class LoadMyPointingMapRequest extends ServerMessage
{
	private static final long serialVersionUID = -3353596774015881292L;
	
	private String resourceKey;

	public LoadMyPointingMapRequest(String resourceKey)
	{
		super(TestCacheMessageType.LOAD_MY_POINTING_MAP_REQUEST);
		this.resourceKey = resourceKey;
	}

	public String getResourceKey()
	{
		return this.resourceKey;
	}
}
