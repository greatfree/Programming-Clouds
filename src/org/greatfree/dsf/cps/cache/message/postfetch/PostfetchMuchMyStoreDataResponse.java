package org.greatfree.dsf.cps.cache.message.postfetch;

import java.util.Map;

import org.greatfree.dsf.cps.cache.data.MyStoreData;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/25/2018, Bing Li
public class PostfetchMuchMyStoreDataResponse extends ServerMessage
{
	private static final long serialVersionUID = 7868221537665670521L;
	
	private Map<String, MyStoreData> data;

	public PostfetchMuchMyStoreDataResponse(Map<String, MyStoreData> data)
	{
		super(TestCacheMessageType.POSTFETCH_MUCH_MY_STORE_DATA_RESPONSE);
		this.data = data;
	}

	public Map<String, MyStoreData> getData()
	{
		return this.data;
	}
}
