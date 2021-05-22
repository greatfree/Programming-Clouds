package org.greatfree.framework.streaming.subscriber;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.streaming.message.StreamNotification;

// Created: 03/20/2020, Bing Li
class StreamNotificationThreadCreator implements NotificationQueueCreator<StreamNotification, StreamNotificationThread>
{

	@Override
	public StreamNotificationThread createInstance(int taskSize)
	{
		return new StreamNotificationThread(taskSize);
	}

}
