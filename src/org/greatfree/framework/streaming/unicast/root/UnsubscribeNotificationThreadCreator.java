package org.greatfree.framework.streaming.unicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.streaming.message.UnsubscribeNotification;

// Created: 03/23/2020, Bing Li
class UnsubscribeNotificationThreadCreator implements NotificationThreadCreatable<UnsubscribeNotification, UnsubscribeNotificationThread>
{

	@Override
	public UnsubscribeNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new UnsubscribeNotificationThread(taskSize);
	}

}
