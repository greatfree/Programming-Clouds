package org.greatfree.framework.streaming.unicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.streaming.message.SubscribeNotification;

// Created: 03/23/2020, Bing Li
class SubscribeNotificationThreadCreator implements NotificationThreadCreatable<SubscribeNotification, SubscribeNotificationThread>
{

	@Override
	public SubscribeNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new SubscribeNotificationThread(taskSize);
	}

}
