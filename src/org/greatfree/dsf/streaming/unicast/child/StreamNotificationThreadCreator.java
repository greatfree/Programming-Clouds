package org.greatfree.dsf.streaming.unicast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.streaming.message.StreamNotification;

// Created: 03/23/2020, Bing Li
class StreamNotificationThreadCreator implements NotificationThreadCreatable<StreamNotification, StreamNotificationThread>
{

	@Override
	public StreamNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new StreamNotificationThread(taskSize);
	}

}
