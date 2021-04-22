package org.greatfree.framework.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataNotification;

// Created: 08/24/2018, Bing Li
public class PostfetchMyDistributedStoreDatarThreadCreator implements NotificationObjectThreadCreatable<PostfetchMyStoreDataNotification, PostfetchMyDistributedStoreDataThread>
{

	@Override
	public PostfetchMyDistributedStoreDataThread createNotificationThreadInstance(int taskSize)
	{
		return new PostfetchMyDistributedStoreDataThread(taskSize);
	}

}
