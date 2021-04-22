package org.greatfree.framework.old.multicast.child;

import java.util.HashMap;

import org.greatfree.framework.multicast.message.OldRootIPAddressBroadcastNotification;
import org.greatfree.message.ChildBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator generates the notifications of RootIPAddressBroadcastNotification that should be broadcast to the current distributed node's children. 05/12/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
class RootIPAddressBroadcastNotificationCreator implements ChildBroadcastNotificationCreatable<OldRootIPAddressBroadcastNotification>
{

	@Override
	public OldRootIPAddressBroadcastNotification createInstanceWithChildren(HashMap<String, IPAddress> children, OldRootIPAddressBroadcastNotification msg)
	{
//		return new RootIPAddressBroadcastNotification(Tools.generateUniqueKey(), children, msg.getRootAddress());
		return new OldRootIPAddressBroadcastNotification(children, msg.getRootAddress());
	}

}
