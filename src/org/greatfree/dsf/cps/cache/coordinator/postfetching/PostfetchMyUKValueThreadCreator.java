package org.greatfree.dsf.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dsf.cps.cache.message.postfetch.FetchMyUKValueNotification;

// Created: 02/25/2019, Bing Li
public class PostfetchMyUKValueThreadCreator implements NotificationObjectThreadCreatable<FetchMyUKValueNotification, PostfetchMyUKValueThread>
{

	@Override
	public PostfetchMyUKValueThread createNotificationThreadInstance(int taskSize)
	{
		return new PostfetchMyUKValueThread(taskSize);
	}

}
