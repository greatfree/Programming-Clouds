package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.framework.multicast.message.MessageDisposer;
import org.greatfree.framework.multicast.message.OldRootIPAddressBroadcastNotification;

// Created: 07/19/2017, Bing Li
class SubstrateBroadcastRootIPAddressNotificationThreadCreator implements BoundNotificationThreadCreatable<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>, SubstrateBroadcastRootIPAddressNotificationThread>
{

	@Override
	public SubstrateBroadcastRootIPAddressNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldRootIPAddressBroadcastNotification> binder)
	{
		return new SubstrateBroadcastRootIPAddressNotificationThread(taskSize, dispatcherKey, binder);
	}

}
