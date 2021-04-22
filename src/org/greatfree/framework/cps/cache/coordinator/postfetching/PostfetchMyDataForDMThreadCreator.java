package org.greatfree.framework.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataNotification;

// Created: 07/09/2018, Bing Li
public class PostfetchMyDataForDMThreadCreator implements NotificationObjectThreadCreatable<PostfetchMyDataNotification, PostfetchMyDataForDMThread>
{

	@Override
	public PostfetchMyDataForDMThread createNotificationThreadInstance(int taskSize)
	{
		return new PostfetchMyDataForDMThread(taskSize);
	}

}
