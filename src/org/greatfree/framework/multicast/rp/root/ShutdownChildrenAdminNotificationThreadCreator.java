package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;

// Created: 10/21/2018, Bing Li
public class ShutdownChildrenAdminNotificationThreadCreator implements NotificationQueueCreator<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread>
{

	@Override
	public ShutdownChildrenAdminNotificationThread createInstance(int taskSize)
	{
		return new ShutdownChildrenAdminNotificationThread(taskSize);
	}

}
