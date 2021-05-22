package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.RootIPAddressBroadcastNotification;

// Created: 09/10/2018, Bing Li
class RootIPAddressBroadcastNotificationThreadCreator implements NotificationQueueCreator<RootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationThread>
{

	@Override
	public RootIPAddressBroadcastNotificationThread createInstance(int taskSize)
	{
		return new RootIPAddressBroadcastNotificationThread(taskSize);
	}

}
