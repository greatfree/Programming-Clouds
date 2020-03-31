package ca.multicast.search.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.multicast.message.ShutdownChildrenAdminNotification;

// Created: 03/15/2020, Bing Li
class ShutdownChildrenNotificationThreadCreator implements NotificationThreadCreatable<ShutdownChildrenAdminNotification, ShutdownChildrenNotificationThread>
{

	@Override
	public ShutdownChildrenNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChildrenNotificationThread(taskSize);
	}

}
