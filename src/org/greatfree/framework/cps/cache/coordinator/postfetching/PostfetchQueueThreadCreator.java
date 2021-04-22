package org.greatfree.framework.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.message.FetchQueueNotification;

// Created: 08/13/2018, Bing Li
public class PostfetchQueueThreadCreator implements  NotificationObjectThreadCreatable<FetchQueueNotification, PostfetchQueueThread>
{

	@Override
	public PostfetchQueueThread createNotificationThreadInstance(int taskSize)
	{
		return new PostfetchQueueThread(taskSize);
	}

}
