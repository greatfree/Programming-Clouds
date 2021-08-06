package org.greatfree.app.search.multicast.child.storage;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;

// Created: 12/10/2018, Bing Li
class ShutdownStorageBroadcastNotificationThreadCreator implements NotificationQueueCreator<ShutdownChildrenBroadcastNotification, ShutdownStorageBroadcastNotificationThread>
{

	@Override
	public ShutdownStorageBroadcastNotificationThread createInstance(int taskSize)
	{
		return new ShutdownStorageBroadcastNotificationThread(taskSize);
	}

}
