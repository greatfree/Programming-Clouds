package org.greatfree.framework.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingMapNotification;

// Created: 07/19/2018, Bing Li
public class PostfetchMyPointingMapThreadCreator implements NotificationObjectThreadCreatable<PostfetchMyPointingMapNotification, PostfetchMyPointingMapThread>
{

	@Override
	public PostfetchMyPointingMapThread createNotificationThreadInstance(int taskSize)
	{
		return new PostfetchMyPointingMapThread(taskSize);
	}

}
