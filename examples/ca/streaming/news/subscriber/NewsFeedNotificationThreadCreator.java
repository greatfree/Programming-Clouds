package ca.streaming.news.subscriber;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.streaming.news.message.NewsFeedNotification;

// Created: 04/03/2020, Bing Li
class NewsFeedNotificationThreadCreator implements NotificationThreadCreatable<NewsFeedNotification, NewsFeedNotificationThread>
{

	@Override
	public NewsFeedNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new NewsFeedNotificationThread(taskSize);
	}

}
