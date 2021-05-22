package org.greatfree.framework.old.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;

// Created: 05/20/2017, Bing Li
class ShutdownChildrenAdminNotificationThreadCreator implements NotificationQueueCreator<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread>
{

	@Override
	public ShutdownChildrenAdminNotificationThread createInstance(int taskSize)
	{
		return new ShutdownChildrenAdminNotificationThread(taskSize);
	}

}
