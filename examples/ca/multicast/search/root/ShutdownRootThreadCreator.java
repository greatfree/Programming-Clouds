package ca.multicast.search.root;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 03/15/2020, Bing Li
class ShutdownRootThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownRootThread>
{

	@Override
	public ShutdownRootThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownRootThread(taskSize);
	}

}
