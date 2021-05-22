package org.greatfree.framework.streaming.unicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.streaming.message.SubscribeNotification;

// Created: 03/23/2020, Bing Li
public class SubscribeNotificationThreadCreator implements NotificationQueueCreator<SubscribeNotification, SubscribeNotificationThread>
{

	@Override
	public SubscribeNotificationThread createInstance(int taskSize)
	{
		return new SubscribeNotificationThread(taskSize);
	}

}
