package org.greatfree.dsf.cps.cache.message.front;

import org.greatfree.dsf.cps.cache.data.MyStoreData;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/13/2018, Bing Li
public class DequeueSingleMyStoreDataResponse extends ServerMessage
{
	private static final long serialVersionUID = -3928126697031037944L;

	private MyStoreData data;

	public DequeueSingleMyStoreDataResponse(MyStoreData data)
	{
		super(TestCacheMessageType.DEQUEUE_SINGLE_MY_STORE_DATA_RESPONSE);
		this.data = data;
	}

	public MyStoreData getData()
	{
		return this.data;
	}
}
