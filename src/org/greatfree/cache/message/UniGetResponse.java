package org.greatfree.cache.message;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.message.multicast.MulticastResponse;

/*
 * The response is the counterpart of UniGetRequest. 07/03/2017, Bing Li
 */

// Created; 07/03/2017, Bing Li
//public class UniGetResponse<Key, Value extends SerializedKey<Key>> extends ClusterBroadcastResponse
public class UniGetResponse<Value extends CacheKey<String>> extends MulticastResponse
{
	private static final long serialVersionUID = 8438610635177604528L;
	
	private Value value;

	public UniGetResponse(String key, String collaboratorKey, Value v)
	{
//		super(CacheMessageType.UNI_GET_RESPONSE, key, collaboratorKey);
		super(CacheMessageType.UNI_GET_RESPONSE, collaboratorKey);
		this.value = v;
	}

	public Value getValue()
	{
		return this.value;
	}
}
