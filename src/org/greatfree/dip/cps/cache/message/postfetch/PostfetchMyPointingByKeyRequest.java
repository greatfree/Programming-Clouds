package org.greatfree.dip.cps.cache.message.postfetch;

import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/12/2018, Bing Li
public class PostfetchMyPointingByKeyRequest extends ServerMessage
{
	private static final long serialVersionUID = 8732123683037081064L;
	
	private String resourceKey;

	public PostfetchMyPointingByKeyRequest(String resourceKey)
	{
		super(TestCacheMessageType.POSTFETCH_MY_POINTING_BY_KEY_REQUEST);
		this.resourceKey = resourceKey;
	}

	public String getResourceKey()
	{
		return this.resourceKey;
	}
}
