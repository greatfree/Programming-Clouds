package org.greatfree.dip.cps.cache.message.front;

import org.greatfree.dip.cps.cache.data.MyStoreData;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/13/2018, Bing Li
public class PeekSingleMyStoreDataQueueResponse extends ServerMessage
{
	private static final long serialVersionUID = -2736884691805640339L;
	
	private MyStoreData data;

	public PeekSingleMyStoreDataQueueResponse(MyStoreData data)
	{
		super(TestCacheMessageType.PEEK_SINGLE_MY_STORE_DATA_QUEUE_RESPONSE);
		this.data = data;
	}

	public MyStoreData getData()
	{
		return this.data;
	}
}
