package org.greatfree.cache.message;

import java.util.Map;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.message.multicast.MulticastResponse;

/*
 * The response is the counterpart of BroadGetResponse. 07/10/2017, Bing Li
 */

// Created: 07/10/2017, Bing Li
//public class BroadGetResponse<Key, Value extends SerializedKey<Key>> extends ClusterBroadcastResponse
public class BroadGetResponse<Value extends CacheKey<String>> extends MulticastResponse
{
	private static final long serialVersionUID = -7516838823519472603L;
	
	private Map<String, Value> values;

	public BroadGetResponse(String key, String collaboratorKey, Map<String, Value> values)
	{
//		super(CacheMessageType.BROAD_GET_RESPONSE, key, collaboratorKey);
		super(CacheMessageType.BROAD_GET_RESPONSE, collaboratorKey);
		this.values = values;
	}

	public Map<String, Value> getValues()
	{
		return this.values;
	}
}
