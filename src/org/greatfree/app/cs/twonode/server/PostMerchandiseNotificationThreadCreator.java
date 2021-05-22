package org.greatfree.app.cs.twonode.server;

import org.greatfree.app.cs.twonode.message.PostMerchandiseNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 07/31/2018, Bing Li
class PostMerchandiseNotificationThreadCreator implements NotificationQueueCreator<PostMerchandiseNotification, PostMerchandiseNotificationThread>
{

	@Override
	public PostMerchandiseNotificationThread createInstance(int taskSize)
	{
		return new PostMerchandiseNotificationThread(taskSize);
	}

}
