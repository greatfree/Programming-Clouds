package org.greatfree.dsf.streaming.broadcast.pubsub;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.streaming.message.AddStreamNotification;

// Created: 03/19/2020, Bing Li
public class AddStreamNotificationThreadCreator implements NotificationThreadCreatable<AddStreamNotification, AddStreamNotificationThread>
{

	@Override
	public AddStreamNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new AddStreamNotificationThread(taskSize);
	}

}
