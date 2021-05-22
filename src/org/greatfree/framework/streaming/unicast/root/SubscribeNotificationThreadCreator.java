package org.greatfree.framework.streaming.unicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.streaming.message.SubscribeNotification;

// Created: 03/23/2020, Bing Li
class SubscribeNotificationThreadCreator implements NotificationQueueCreator<SubscribeNotification, SubscribeNotificationThread>
{

	@Override
	public SubscribeNotificationThread createInstance(int taskSize)
	{
		return new SubscribeNotificationThread(taskSize);
	}

}
