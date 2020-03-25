package org.greatfree.dip.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dip.cps.cache.message.FetchStackNotification;

// Created: 08/07/2018, Bing Li
public class PostfetchStackThreadCreator implements  NotificationObjectThreadCreatable<FetchStackNotification, PostfetchStackThread>
{

	@Override
	public PostfetchStackThread createNotificationThreadInstance(int taskSize)
	{
		return new PostfetchStackThread(taskSize);
	}

}
