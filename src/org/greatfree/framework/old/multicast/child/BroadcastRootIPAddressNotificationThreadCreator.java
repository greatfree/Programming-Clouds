package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.framework.multicast.message.OldRootIPAddressBroadcastNotification;
import org.greatfree.message.MessageDisposer;

// Created: 05/20/2017, Bing Li
class BroadcastRootIPAddressNotificationThreadCreator implements BoundNotificationThreadCreatable<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>, BroadcastRootIPAddressNotificationThread>
{

	@Override
	public BroadcastRootIPAddressNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldRootIPAddressBroadcastNotification> binder)
	{
		// TODO Auto-generated method stub
		return new BroadcastRootIPAddressNotificationThread(taskSize, dispatcherKey, binder);
	}

}
