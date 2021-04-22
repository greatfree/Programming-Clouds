package org.greatfree.cluster.child.container;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.RootIPAddressBroadcastNotification;

// Created: 01/13/2019, Bing Li
class RootIPAddressBroadcastNotificationThreadCreator implements NotificationThreadCreatable<RootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationThread>
{

	@Override
	public RootIPAddressBroadcastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new RootIPAddressBroadcastNotificationThread(taskSize);
	}

}
