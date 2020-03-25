package org.greatfree.dip.cps.cache.message.front;

import java.util.List;

import org.greatfree.dip.cps.cache.data.MyStoreData;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/13/2018, Bing Li
public class PeekMyStoreDataQueueResponse extends ServerMessage
{
	private static final long serialVersionUID = -3223053389010115744L;

	private List<MyStoreData> data;
	private MyStoreData value;

	public PeekMyStoreDataQueueResponse(List<MyStoreData> data)
	{
		super(TestCacheMessageType.PEEK_MY_STORE_DATA_QUEUE_RESPONSE);
		this.data = data;
	}

	public PeekMyStoreDataQueueResponse(MyStoreData data)
	{
		super(TestCacheMessageType.PEEK_MY_STORE_DATA_QUEUE_RESPONSE);
		this.value = data;
	}

	public List<MyStoreData> getData()
	{
		return this.data;
	}
	
	public MyStoreData getValue()
	{
		return this.value;
	}
}
