package ca.streaming.news.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.streaming.news.message.JournalistPostNotification;

// Created: 04/02/2020, Bing Li
class JournalistPostNotificationThreadCreator implements NotificationThreadCreatable<JournalistPostNotification, JournalistPostNotificationThread>
{

	@Override
	public JournalistPostNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new JournalistPostNotificationThread(taskSize);
	}

}
