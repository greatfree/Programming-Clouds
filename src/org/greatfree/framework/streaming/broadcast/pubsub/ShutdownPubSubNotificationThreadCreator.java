package org.greatfree.framework.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.streaming.message.ShutdownPubSubNotification;

// Created: 03/21/2020, Bing Li
class ShutdownPubSubNotificationThreadCreator implements NotificationThreadCreatable<ShutdownPubSubNotification, ShutdownPubSubNotificationThread>
{

	@Override
	public ShutdownPubSubNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownPubSubNotificationThread(taskSize);
	}

}
