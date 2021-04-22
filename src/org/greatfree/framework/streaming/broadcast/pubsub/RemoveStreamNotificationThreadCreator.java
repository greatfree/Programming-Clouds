package org.greatfree.framework.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.streaming.message.RemoveStreamNotification;

// Created: 03/19/2020, bing Li
public class RemoveStreamNotificationThreadCreator implements NotificationThreadCreatable<RemoveStreamNotification, RemoveStreamNotificationThread>
{

	@Override
	public RemoveStreamNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new RemoveStreamNotificationThread(taskSize);
	}

}
