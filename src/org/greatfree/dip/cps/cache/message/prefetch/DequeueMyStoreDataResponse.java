package org.greatfree.dip.cps.cache.message.prefetch;

import java.util.List;

import org.greatfree.dip.cps.cache.data.MyStoreData;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/13/2018, Bing Li
public class DequeueMyStoreDataResponse extends ServerMessage
{
	private static final long serialVersionUID = -5048403676262651147L;

	private List<MyStoreData> data;

	public DequeueMyStoreDataResponse(List<MyStoreData> data)
	{
		super(TestCacheMessageType.DEQUEUE_MY_STORE_DATA_RESPONSE);
		this.data = data;
	}

	public List<MyStoreData> getData()
	{
		return this.data;
	}
}
