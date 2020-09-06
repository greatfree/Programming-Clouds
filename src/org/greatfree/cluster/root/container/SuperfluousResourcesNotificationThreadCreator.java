package org.greatfree.cluster.root.container;

import org.greatfree.cluster.message.SuperfluousResourcesNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 09/06/2020, Bing Li
class SuperfluousResourcesNotificationThreadCreator implements NotificationThreadCreatable<SuperfluousResourcesNotification, SuperfluousResourcesNotificationThread>
{

	@Override
	public SuperfluousResourcesNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new SuperfluousResourcesNotificationThread(taskSize);
	}

}
