package org.greatfree.app.cs.twonode.server;

import org.greatfree.app.cs.twonode.message.PostMerchandiseNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 07/31/2018, Bing Li
class PostMerchandiseNotificationThreadCreator implements NotificationThreadCreatable<PostMerchandiseNotification, PostMerchandiseNotificationThread>
{

	@Override
	public PostMerchandiseNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new PostMerchandiseNotificationThread(taskSize);
	}

}
