package org.greatfree.framework.streaming.unicast.pubsub;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.streaming.message.ShutdownPubSubNotification;

// Created: 03/23/2020, Bing Li
class ShutdownPubSubNotificationThreadCreator implements NotificationQueueCreator<ShutdownPubSubNotification, ShutdownPubSubNotificationThread>
{

	@Override
	public ShutdownPubSubNotificationThread createInstance(int taskSize)
	{
		return new ShutdownPubSubNotificationThread(taskSize);
	}

}
