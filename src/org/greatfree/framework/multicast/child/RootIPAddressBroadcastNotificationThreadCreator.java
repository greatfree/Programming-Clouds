package org.greatfree.framework.multicast.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.RootIPAddressBroadcastNotification;

// Created: 09/10/2018, Bing Li
class RootIPAddressBroadcastNotificationThreadCreator implements NotificationThreadCreatable<RootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationThread>
{

	@Override
	public RootIPAddressBroadcastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new RootIPAddressBroadcastNotificationThread(taskSize);
	}

}
