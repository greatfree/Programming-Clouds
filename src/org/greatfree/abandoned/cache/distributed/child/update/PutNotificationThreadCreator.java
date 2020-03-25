package org.greatfree.abandoned.cache.distributed.child.update;

import org.greatfree.cache.message.update.PutNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 07/17/2017, Bing Li
public class PutNotificationThreadCreator implements NotificationThreadCreatable<PutNotification, PutNotificationThread>
{

	@Override
	public PutNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new PutNotificationThread(taskSize);
	}

}
