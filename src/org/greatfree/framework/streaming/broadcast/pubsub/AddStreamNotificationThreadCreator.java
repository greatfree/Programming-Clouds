package org.greatfree.framework.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.streaming.message.AddStreamNotification;

// Created: 03/19/2020, Bing Li
public class AddStreamNotificationThreadCreator implements NotificationQueueCreator<AddStreamNotification, AddStreamNotificationThread>
{

	@Override
	public AddStreamNotificationThread createInstance(int taskSize)
	{
		return new AddStreamNotificationThread(taskSize);
	}

}
