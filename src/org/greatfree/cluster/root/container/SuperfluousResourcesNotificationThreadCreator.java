package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.SuperfluousResourcesNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 09/06/2020, Bing Li
class SuperfluousResourcesNotificationThreadCreator implements NotificationQueueCreator<SuperfluousResourcesNotification, SuperfluousResourcesNotificationThread>
{

	@Override
	public SuperfluousResourcesNotificationThread createInstance(int taskSize)
	{
		return new SuperfluousResourcesNotificationThread(taskSize);
	}

}
