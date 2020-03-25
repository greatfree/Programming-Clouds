package org.greatfree.dip.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.dip.multicast.message.MessageDisposer;
import org.greatfree.dip.multicast.message.OldRootIPAddressBroadcastNotification;

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
