package org.greatfree.dip.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dip.cps.cache.message.postfetch.PostfetchMyDataNotification;

// Created: 07/21/2018, Bing Li
public class PostfetchMyDataForPDMThreadCreator implements NotificationObjectThreadCreatable<PostfetchMyDataNotification, PostfetchMyDataForPDMThread>
{

	@Override
	public PostfetchMyDataForPDMThread createNotificationThreadInstance(int taskSize)
	{
		return new PostfetchMyDataForPDMThread(taskSize);
	}

}
