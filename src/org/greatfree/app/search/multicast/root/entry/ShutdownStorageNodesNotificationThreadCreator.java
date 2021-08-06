package org.greatfree.app.search.multicast.root.entry;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;

// Created: 12/10/2018, Bing Li
class ShutdownStorageNodesNotificationThreadCreator implements NotificationQueueCreator<ShutdownChildrenAdminNotification, ShutdownStorageNodesNotificationThread>
{

	@Override
	public ShutdownStorageNodesNotificationThread createInstance(int taskSize)
	{
		return new ShutdownStorageNodesNotificationThread(taskSize);
	}

}
