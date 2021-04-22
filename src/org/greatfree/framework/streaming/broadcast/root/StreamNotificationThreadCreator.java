package org.greatfree.framework.streaming.broadcast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.streaming.message.StreamNotification;

// Created: 03/19/2020, Bing Li
class StreamNotificationThreadCreator implements NotificationThreadCreatable<StreamNotification, StreamNotificationThread>
{

	@Override
	public StreamNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new StreamNotificationThread(taskSize);
	}

}
