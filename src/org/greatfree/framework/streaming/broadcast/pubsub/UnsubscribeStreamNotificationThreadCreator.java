package org.greatfree.framework.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.streaming.message.UnsubscribeStreamNotification;

// Created: 03/19/2020, Bing Li
class UnsubscribeStreamNotificationThreadCreator implements NotificationQueueCreator<UnsubscribeStreamNotification, UnsubscribeStreamNotificationThread>
{

	@Override
	public UnsubscribeStreamNotificationThread createInstance(int taskSize)
	{
		return new UnsubscribeStreamNotificationThread(taskSize);
	}

}
