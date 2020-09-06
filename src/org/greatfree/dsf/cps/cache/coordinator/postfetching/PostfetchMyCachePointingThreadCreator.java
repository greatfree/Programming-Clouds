package org.greatfree.dsf.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dsf.cps.cache.message.FetchMyCachePointingNotification;

// Created: 07/22/2018, Bing Li
public class PostfetchMyCachePointingThreadCreator implements NotificationObjectThreadCreatable<FetchMyCachePointingNotification, PostfetchMyCachePointingThread>
{

	@Override
	public PostfetchMyCachePointingThread createNotificationThreadInstance(int taskSize)
	{
		return new PostfetchMyCachePointingThread(taskSize);
	}

}
