package org.greatfree.cache.message;

import java.util.Set;

import org.greatfree.message.multicast.MulticastResponse;

/*
 * The response is the counterpart of BroadKeysRequest. 07/11/2017, Bing Li
 */

// Created: 07/11/2017, Bing Li
public class BroadKeysResponse extends MulticastResponse
{
	private static final long serialVersionUID = -1132608301122233108L;
	
	private Set<String> keys;

	public BroadKeysResponse(String key, String collaboratorKey, Set<String> keys)
	{
//		super(CacheMessageType.BROAD_KEYS_RESPONSE, key, collaboratorKey);
		super(CacheMessageType.BROAD_KEYS_RESPONSE, collaboratorKey);
		this.keys = keys;
	}

	public Set<String> getKeys()
	{
		return this.keys;
	}
}
