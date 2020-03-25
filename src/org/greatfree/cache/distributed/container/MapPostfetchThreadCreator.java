package org.greatfree.cache.distributed.container;

import org.greatfree.concurrency.reactive.NotificationTask;
import org.greatfree.concurrency.reactive.NotificationTaskThreadCreator;

// Created: 01/22/2019, Bing Li
class MapPostfetchThreadCreator implements NotificationTaskThreadCreator<ContainerMapPostfetchNotification, MapPostfetchThread>
{

	@Override
	public MapPostfetchThread createThreadInstance(int taskSize, NotificationTask<ContainerMapPostfetchNotification> task)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
