package org.greatfree.multicast.child.abandoned;

import org.greatfree.message.ChildBroadcastNotificationCreatable;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the instance of ChildBroadcastNotifier. 11/27/2014, Bing Li
 */

// Created: 05/05/2017, Bing Li
class ChildBroadcastNotifierDisposer<Notification extends OldMulticastMessage, NotificationCreator extends ChildBroadcastNotificationCreatable<Notification>> implements HashDisposable<ChildBroadcastNotifier<Notification, NotificationCreator>>
{

	@Override
	public void dispose(ChildBroadcastNotifier<Notification, NotificationCreator> t)
	{
		t.dispose();
	}

}
