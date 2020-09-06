package org.greatfree.dsf.cps.cache.message.replicate;

import org.greatfree.dsf.cps.cache.data.MyStoreData;
import org.greatfree.dsf.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 07/24/2018, Bing Li
public class PushMyStoreDataNotification extends ServerMessage
{
	private static final long serialVersionUID = 1147954574350949963L;

	private MyStoreData data;

	public PushMyStoreDataNotification(MyStoreData data)
	{
		super(TestCacheMessageType.PUSH_MY_STORE_DATA_NOTIFICATION);
		this.data = data;
	}

	public MyStoreData getData()
	{
		return this.data;
	}
}
