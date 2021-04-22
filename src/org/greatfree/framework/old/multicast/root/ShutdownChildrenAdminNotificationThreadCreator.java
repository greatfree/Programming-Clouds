package org.greatfree.framework.old.multicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;

// Created: 05/20/2017, Bing Li
class ShutdownChildrenAdminNotificationThreadCreator implements NotificationThreadCreatable<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread>
{

	@Override
	public ShutdownChildrenAdminNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChildrenAdminNotificationThread(taskSize);
	}

}
