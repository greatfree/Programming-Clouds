package org.greatfree.dip.old.multicast.child;

import java.util.HashMap;

import org.greatfree.dip.multicast.message.OldShutdownChildrenBroadcastNotification;
import org.greatfree.message.ChildBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator generates the notifications of ShutdownChildrenBroadcastNotification that should be broadcast to the current distributed node's children. 05/12/2017, Bing Li
 */

// Created: 05/19/2017, Bing Li
class ShutdownChildrenBroadcastNotificationCreator implements ChildBroadcastNotificationCreatable<OldShutdownChildrenBroadcastNotification>
{

	@Override
	public OldShutdownChildrenBroadcastNotification createInstanceWithChildren(HashMap<String, IPAddress> children, OldShutdownChildrenBroadcastNotification msg)
	{
//		return new ShutdownChildrenBroadcastNotification(Tools.generateUniqueKey(), children);
		return new OldShutdownChildrenBroadcastNotification(children);
	}

}
