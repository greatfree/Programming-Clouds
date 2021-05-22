package org.greatfree.framework.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.streaming.message.ShutdownPubSubNotification;

// Created: 03/21/2020, Bing Li
class ShutdownPubSubNotificationThreadCreator implements NotificationQueueCreator<ShutdownPubSubNotification, ShutdownPubSubNotificationThread>
{

	@Override
	public ShutdownPubSubNotificationThread createInstance(int taskSize)
	{
		return new ShutdownPubSubNotificationThread(taskSize);
	}

}
