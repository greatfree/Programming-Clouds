package org.greatfree.framework.cps.cache.coordinator.evicting;

import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationObjectQueue;
import org.greatfree.framework.cps.cache.data.MyStoreData;

// Created: 08/24/2018, Bing Li
public class EvictMyDistributedStoreDataThread extends NotificationObjectQueue<EvictedNotification<MyStoreData>>
{

	public EvictMyDistributedStoreDataThread(int taskSize)
	{
		super(taskSize);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}

}
