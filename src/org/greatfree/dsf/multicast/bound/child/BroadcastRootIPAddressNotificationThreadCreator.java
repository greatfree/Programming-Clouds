package org.greatfree.dsf.multicast.bound.child;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.dsf.multicast.message.MessageDisposer;
import org.greatfree.dsf.multicast.message.OldRootIPAddressBroadcastNotification;

// Created: 08/26/2018, Bing Li
public class BroadcastRootIPAddressNotificationThreadCreator implements BoundNotificationThreadCreatable<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>, BroadcastRootIPAddressNotificationThread>
{

	@Override
	public BroadcastRootIPAddressNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldRootIPAddressBroadcastNotification> binder)
	{
		return new BroadcastRootIPAddressNotificationThread(taskSize, dispatcherKey, binder);
	}

}
