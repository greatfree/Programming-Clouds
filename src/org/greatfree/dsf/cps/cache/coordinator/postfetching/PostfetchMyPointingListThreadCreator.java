package org.greatfree.dsf.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dsf.cps.cache.message.FetchMyPointingListNotification;

// Created: 07/11/2018, Bing Li
public class PostfetchMyPointingListThreadCreator implements NotificationObjectThreadCreatable<FetchMyPointingListNotification, PostfetchMyPointingListThread>
{

	@Override
	public PostfetchMyPointingListThread createNotificationThreadInstance(int taskSize)
	{
		return new PostfetchMyPointingListThread(taskSize);
	}

}
