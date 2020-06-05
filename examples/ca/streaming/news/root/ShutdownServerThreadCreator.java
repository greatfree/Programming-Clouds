package ca.streaming.news.root;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 04/01/2020, Bing Li
class ShutdownServerThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownServerThread>
{

	@Override
	public ShutdownServerThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownServerThread(taskSize);
	}

}
