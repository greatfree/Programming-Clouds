package ca.streaming.news.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.streaming.message.SubscribeNotification;

// Created: 03/31/2020, Bing Li
class SubscribeNotificationThreadCreator implements NotificationThreadCreatable<SubscribeNotification, SubscribeNotificationThread>
{

	@Override
	public SubscribeNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new SubscribeNotificationThread(taskSize);
	}

}
