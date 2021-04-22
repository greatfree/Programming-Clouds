package org.greatfree.framework.cps.cache.message.postfetch;

import org.greatfree.framework.cps.cache.data.MyStoreData;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/25/2018, Bing Li
public class PostfetchMyStoreDataResponse extends ServerMessage
{
	private static final long serialVersionUID = -3297927639372597447L;
	
	private MyStoreData data;

	public PostfetchMyStoreDataResponse(MyStoreData data)
	{
		super(TestCacheMessageType.POSTFETCH_MY_STORE_DATA_RESPONSE);
		this.data = data;
	}

	public MyStoreData getData()
	{
		return this.data;
	}
}
