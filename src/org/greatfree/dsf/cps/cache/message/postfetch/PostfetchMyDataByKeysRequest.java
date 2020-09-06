package org.greatfree.dsf.cps.cache.message.postfetch;

import java.util.Set;

import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/21/2018, Bing Li
public class PostfetchMyDataByKeysRequest extends ServerMessage
{
	private static final long serialVersionUID = 1629337760418540232L;
	
	private Set<String> resourceKeys;

	public PostfetchMyDataByKeysRequest(Set<String> resourceKeys)
	{
		super(TestCacheMessageType.POSTFETCH_MY_DATA_BY_KEYS_REQUEST);
		this.resourceKeys = resourceKeys;
	}

	public Set<String> getResourceKeys()
	{
		return this.resourceKeys;
	}
}
