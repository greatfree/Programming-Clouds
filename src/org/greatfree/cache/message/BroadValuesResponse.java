package org.greatfree.cache.message;

import java.util.Map;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.message.multicast.MulticastResponse;

/*
 * The response is the counterpart of BroadValuesRequest. 07/11/2017, Bing Li
 */

// Created: 07/11/2017, Bing Li
public class BroadValuesResponse<Value extends CacheKey<String>> extends MulticastResponse
{
	private static final long serialVersionUID = 7847367261857669014L;
	
	private Map<String, Value> values;

	public BroadValuesResponse(String key, String collaboratorKey, Map<String, Value> values)
	{
//		super(CacheMessageType.BROAD_VALUES_RESPONSE, key, collaboratorKey);
		super(CacheMessageType.BROAD_VALUES_RESPONSE, collaboratorKey);
		this.values = values;
	}

	public Map<String, Value> getValues()
	{
		return this.values;
	}
}
