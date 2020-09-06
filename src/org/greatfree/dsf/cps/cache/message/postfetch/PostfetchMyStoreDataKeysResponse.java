package org.greatfree.dsf.cps.cache.message.postfetch;

import java.util.Map;

import org.greatfree.dsf.cps.cache.data.MyStoreData;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/25/2018, Bing Li
public class PostfetchMyStoreDataKeysResponse extends ServerMessage
{
	private static final long serialVersionUID = 8205074581871613906L;
	
	private Map<String, MyStoreData> data;

	public PostfetchMyStoreDataKeysResponse(Map<String, MyStoreData> keys)
	{
		super(TestCacheMessageType.POSTFETCH_MY_STORE_DATA_KEYS_RESPONSE);
		this.data = keys;
	}

	public Map<String, MyStoreData> getData()
	{
		return this.data;
	}
}
