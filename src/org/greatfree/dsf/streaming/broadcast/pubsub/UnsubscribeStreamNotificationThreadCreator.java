package org.greatfree.dsf.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.streaming.message.UnsubscribeStreamNotification;

// Created: 03/19/2020, Bing Li
class UnsubscribeStreamNotificationThreadCreator implements NotificationThreadCreatable<UnsubscribeStreamNotification, UnsubscribeStreamNotificationThread>
{

	@Override
	public UnsubscribeStreamNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new UnsubscribeStreamNotificationThread(taskSize);
	}

}
