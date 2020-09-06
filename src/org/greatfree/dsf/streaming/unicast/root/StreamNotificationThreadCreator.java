package org.greatfree.dsf.streaming.unicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.streaming.message.StreamNotification;

// Created: 03/22/2020, Bing Li
class StreamNotificationThreadCreator implements NotificationThreadCreatable<StreamNotification, StreamNotificationThread>
{

	@Override
	public StreamNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new StreamNotificationThread(taskSize);
	}

}
