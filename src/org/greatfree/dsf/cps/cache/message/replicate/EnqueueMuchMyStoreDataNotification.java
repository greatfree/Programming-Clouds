package org.greatfree.dsf.cps.cache.message.replicate;

import java.util.List;

import org.greatfree.dsf.cps.cache.data.MyStoreData;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/13/2018, Bing Li
public class EnqueueMuchMyStoreDataNotification extends ServerMessage
{
	private static final long serialVersionUID = -8557156538239608224L;

	private String queueKey;
	private List<MyStoreData> data;

	public EnqueueMuchMyStoreDataNotification(String queueKey, List<MyStoreData> data)
	{
		super(TestCacheMessageType.ENQUEUE_MUCH_MY_STORE_DATA_NOTIFICATION);
		this.queueKey = queueKey;
		this.data = data;
	}

	public String getQueueKey()
	{
		return this.queueKey;
	}
	
	public List<MyStoreData> getData()
	{
		return this.data;
	}
}
