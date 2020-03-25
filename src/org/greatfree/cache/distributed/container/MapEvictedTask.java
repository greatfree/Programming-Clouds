package org.greatfree.cache.distributed.container;

import org.greatfree.cache.StoreElement;
import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationTask;

// Created: 01/22/2019, Bing Li
class MapEvictedTask<Value extends StoreElement> implements NotificationTask<EvictedNotification<Value>>
{

	@Override
	public void processNotification(EvictedNotification<Value> notification)
	{
		// TODO Auto-generated method stub
		
	}

}
