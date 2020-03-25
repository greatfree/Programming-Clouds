package org.greatfree.cache.distributed.container;

import org.greatfree.cache.StoreElement;
import org.greatfree.cache.distributed.MapReplicateNotification;
import org.greatfree.concurrency.reactive.NotificationTask;

// Created: 01/22/2019, Bing Li
class MapReplicateTask<Value extends StoreElement> implements NotificationTask<MapReplicateNotification<Value>>
{

	@Override
	public void processNotification(MapReplicateNotification<Value> notification)
	{
		
	}

}
