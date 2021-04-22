package org.greatfree.framework.streaming.unicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;

// Created: 03/22/2020, Bing Li
class ShutdownChildrenAdminNotificationThreadCreator implements NotificationThreadCreatable<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread>
{

	@Override
	public ShutdownChildrenAdminNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownChildrenAdminNotificationThread(taskSize);
	}

}
