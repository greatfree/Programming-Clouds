package org.greatfree.dip.cps.cache.message.replicate;

import org.greatfree.dip.cps.cache.data.MyStoreData;
import org.greatfree.dip.cps.cache.message.TestCacheMessageType;
import org.greatfree.message.ServerMessage;

// Created: 08/25/2018, Bing Li
public class ReplicateMyStoreDataMapStoreNotification extends ServerMessage
{
	private static final long serialVersionUID = -3730226549118375518L;
	
	private MyStoreData data;

	public ReplicateMyStoreDataMapStoreNotification(MyStoreData data)
	{
		super(TestCacheMessageType.REPLICATE_MY_STORE_DATA_MAP_STORE_NOTIFICATION);
		this.data = data;
	}

	public MyStoreData getData()
	{
		return this.data;
	}
}
