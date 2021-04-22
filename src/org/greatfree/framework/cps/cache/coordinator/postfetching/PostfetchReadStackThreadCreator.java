package org.greatfree.framework.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.message.FetchStackNotification;

// Created: 08/23/2018, Bing Li
public class PostfetchReadStackThreadCreator implements  NotificationObjectThreadCreatable<FetchStackNotification, PostfetchReadStackThread>
{

	@Override
	public PostfetchReadStackThread createNotificationThreadInstance(int taskSize)
	{
		return new PostfetchReadStackThread(taskSize);
	}

}
