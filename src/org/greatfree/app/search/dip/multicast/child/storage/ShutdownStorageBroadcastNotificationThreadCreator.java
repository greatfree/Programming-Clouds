package org.greatfree.app.search.dip.multicast.child.storage;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;

// Created: 12/10/2018, Bing Li
class ShutdownStorageBroadcastNotificationThreadCreator implements NotificationThreadCreatable<ShutdownChildrenBroadcastNotification, ShutdownStorageBroadcastNotificationThread>
{

	@Override
	public ShutdownStorageBroadcastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownStorageBroadcastNotificationThread(taskSize);
	}

}
