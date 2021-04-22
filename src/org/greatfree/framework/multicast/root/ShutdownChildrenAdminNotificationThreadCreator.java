package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;

// Created: 08/26/2018, Bing Li
class ShutdownChildrenAdminNotificationThreadCreator implements NotificationThreadCreatable<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread>
{

	@Override
	public ShutdownChildrenAdminNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChildrenAdminNotificationThread(taskSize);
	}

}
