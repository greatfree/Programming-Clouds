package org.greatfree.framework.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.streaming.message.RemoveStreamNotification;

// Created: 03/19/2020, bing Li
public class RemoveStreamNotificationThreadCreator implements NotificationQueueCreator<RemoveStreamNotification, RemoveStreamNotificationThread>
{

	@Override
	public RemoveStreamNotificationThread createInstance(int taskSize)
	{
		return new RemoveStreamNotificationThread(taskSize);
	}

}
