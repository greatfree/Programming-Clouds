package org.greatfree.multicast.root.abandoned;

import java.io.Serializable;

import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the instance of RootAnycastNotifier. 05/04/2017, Bing Li
 */

// Created: 05/04/2017, Bing Li
class RootAnycastNotifierDisposer<Data extends Serializable, Notification extends OldMulticastMessage, NotificationCreator extends RootBroadcastNotificationCreatable<Notification, Data>> implements HashDisposable<RootAnycastNotifier<Data, Notification, NotificationCreator>>
{

	@Override
	public void dispose(RootAnycastNotifier<Data, Notification, NotificationCreator> t)
	{
		t.dispose();
	}

}
