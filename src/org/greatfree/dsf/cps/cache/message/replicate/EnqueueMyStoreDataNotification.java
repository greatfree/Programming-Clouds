package org.greatfree.dsf.cps.cache.message.replicate;

import org.greatfree.dsf.cps.cache.data.MyStoreData;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/13/2018, Bing Li
public class EnqueueMyStoreDataNotification extends ServerMessage
{
	private static final long serialVersionUID = 8156929517735880583L;

	private MyStoreData data;

	public EnqueueMyStoreDataNotification(MyStoreData data)
	{
		super(TestCacheMessageType.ENQUEUE_MY_STORE_DATA_NOTIFICATION);
		this.data = data;
	}

	public MyStoreData getData()
	{
		return this.data;
	}
}
