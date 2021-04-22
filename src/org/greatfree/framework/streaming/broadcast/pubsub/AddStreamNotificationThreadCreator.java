package org.greatfree.framework.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.streaming.message.AddStreamNotification;

// Created: 03/19/2020, Bing Li
public class AddStreamNotificationThreadCreator implements NotificationThreadCreatable<AddStreamNotification, AddStreamNotificationThread>
{

	@Override
	public AddStreamNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new AddStreamNotificationThread(taskSize);
	}

}
