package ca.streaming.news.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.streaming.news.message.MicroblogNotification;

// Created: 04/01/2020, Bing Li
class MicroblogNotificationThreadCreator implements NotificationThreadCreatable<MicroblogNotification, MicroblogNotificationThread>
{

	@Override
	public MicroblogNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new MicroblogNotificationThread(taskSize);
	}

}
