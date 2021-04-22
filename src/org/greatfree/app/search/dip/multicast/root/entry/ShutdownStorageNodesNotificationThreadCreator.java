package org.greatfree.app.search.dip.multicast.root.entry;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;

// Created: 12/10/2018, Bing Li
class ShutdownStorageNodesNotificationThreadCreator implements NotificationThreadCreatable<ShutdownChildrenAdminNotification, ShutdownStorageNodesNotificationThread>
{

	@Override
	public ShutdownStorageNodesNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownStorageNodesNotificationThread(taskSize);
	}

}
