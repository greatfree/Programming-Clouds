package org.greatfree.cache.distributed.container;

import org.greatfree.cache.StoreElement;
import org.greatfree.cache.distributed.EvictedNotification;
import org.greatfree.concurrency.reactive.NotificationTask;
import org.greatfree.concurrency.reactive.NotificationTaskThreadCreator;

// Created: 01/22/2019, Bing Li
class MapEvictThreadCreator<Value extends StoreElement> implements NotificationTaskThreadCreator<EvictedNotification<Value>, MapEvictThread<Value>>
{

	@Override
	public MapEvictThread<Value> createThreadInstance(int taskSize, NotificationTask<EvictedNotification<Value>> task)
	{
		return new MapEvictThread<Value>(taskSize, task);
	}

}
