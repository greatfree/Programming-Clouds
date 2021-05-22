package org.greatfree.framework.streaming.unicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.streaming.message.StreamNotification;

// Created: 03/23/2020, Bing Li
class StreamNotificationThreadCreator implements NotificationQueueCreator<StreamNotification, StreamNotificationThread>
{

	@Override
	public StreamNotificationThread createInstance(int taskSize)
	{
		return new StreamNotificationThread(taskSize);
	}

}
