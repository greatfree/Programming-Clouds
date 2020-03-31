package edu.greatfree.tncs.server;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import edu.greatfree.tncs.message.PostMerchandiseNotification;

// Created: 05/19/2019, Bing Li
class PostMerchandiseNotificationThreadCreator implements NotificationThreadCreatable<PostMerchandiseNotification, PostMerchandiseNotificationThread>
{
	@Override
	public PostMerchandiseNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new PostMerchandiseNotificationThread(taskSize);
	}
}
