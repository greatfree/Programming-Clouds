package org.greatfree.dip.old.multicast.message.root;

import java.util.HashMap;

import org.greatfree.dip.multicast.message.OldRootIPAddressBroadcastNotification;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator that generates the notification that is sent to distributed nodes broadcastly in a cluster. 05/08/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
public class RootIPAddressBroadcastNotificationCreator implements RootBroadcastNotificationCreatable<OldRootIPAddressBroadcastNotification, IPAddress>
{
	/*
	 * The constructor is used to create notifications when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
	public OldRootIPAddressBroadcastNotification createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, IPAddress message)
	{
//		return new RootIPAddressBroadcastNotification(Tools.generateUniqueKey(), childrenMap, message);
		return new OldRootIPAddressBroadcastNotification(childrenMap, message);
	}

	/*
	 * The method is used to create notifications when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
	public OldRootIPAddressBroadcastNotification createInstanceWithoutChildren(IPAddress message)
	{
//		return new RootIPAddressBroadcastNotification(Tools.generateUniqueKey(), message);
		return new OldRootIPAddressBroadcastNotification(message);
	}

}
