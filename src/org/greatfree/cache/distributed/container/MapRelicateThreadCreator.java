package org.greatfree.cache.distributed.container;

import org.greatfree.cache.StoreElement;
import org.greatfree.cache.distributed.MapReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationTask;
import org.greatfree.concurrency.reactive.NotificationTaskThreadCreator;

// Created: 01/22/2019, Bing Li
class MapRelicateThreadCreator<Value extends StoreElement> implements NotificationTaskThreadCreator<MapReplicateNotification<Value>, MapRelicateThread<Value>>
{

	@Override
	public MapRelicateThread<Value> createThreadInstance(int taskSize, NotificationTask<MapReplicateNotification<Value>> task)
	{
		return new MapRelicateThread<Value>(taskSize, task);
	}

}
