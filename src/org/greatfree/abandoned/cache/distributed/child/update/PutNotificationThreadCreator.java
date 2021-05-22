package org.greatfree.abandoned.cache.distributed.child.update;

import org.greatfree.cache.message.update.PutNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 07/17/2017, Bing Li
public class PutNotificationThreadCreator implements NotificationQueueCreator<PutNotification, PutNotificationThread>
{

	@Override
	public PutNotificationThread createInstance(int taskSize)
	{
		return new PutNotificationThread(taskSize);
	}

}
