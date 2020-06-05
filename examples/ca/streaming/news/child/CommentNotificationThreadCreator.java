package ca.streaming.news.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.streaming.news.message.CommentNotification;

// Created: 04/02/2020, Bing Li
class CommentNotificationThreadCreator implements NotificationThreadCreatable<CommentNotification, CommentNotificationThread>
{

	@Override
	public CommentNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new CommentNotificationThread(taskSize);
	}

}
