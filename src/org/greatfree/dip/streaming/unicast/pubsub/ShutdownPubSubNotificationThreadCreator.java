package org.greatfree.dip.streaming.unicast.pubsub;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.streaming.message.ShutdownPubSubNotification;

// Created: 03/23/2020, Bing Li
class ShutdownPubSubNotificationThreadCreator implements NotificationThreadCreatable<ShutdownPubSubNotification, ShutdownPubSubNotificationThread>
{

	@Override
	public ShutdownPubSubNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownPubSubNotificationThread(taskSize);
	}

}
