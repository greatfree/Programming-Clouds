package ca.streaming.news.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.multicast.message.ShutdownChildrenAdminNotification;

// Created: 04/01/2020, Bing Li
class ShutdownChildrenAdminNotificationThreadCreator implements NotificationThreadCreatable<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread>
{

	@Override
	public ShutdownChildrenAdminNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChildrenAdminNotificationThread(taskSize);
	}

}
