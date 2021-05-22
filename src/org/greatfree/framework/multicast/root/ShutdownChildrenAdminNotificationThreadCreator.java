package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;

// Created: 08/26/2018, Bing Li
class ShutdownChildrenAdminNotificationThreadCreator implements NotificationQueueCreator<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread>
{

	@Override
	public ShutdownChildrenAdminNotificationThread createInstance(int taskSize)
	{
		return new ShutdownChildrenAdminNotificationThread(taskSize);
	}

}
