package org.greatfree.framework.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.streaming.message.UnsubscribeStreamNotification;

// Created: 03/19/2020, Bing Li
class UnsubscribeStreamNotificationThreadCreator implements NotificationThreadCreatable<UnsubscribeStreamNotification, UnsubscribeStreamNotificationThread>
{

	@Override
	public UnsubscribeStreamNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new UnsubscribeStreamNotificationThread(taskSize);
	}

}
