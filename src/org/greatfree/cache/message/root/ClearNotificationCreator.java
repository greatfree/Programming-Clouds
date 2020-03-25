package org.greatfree.cache.message.root;

import java.util.HashMap;

import org.greatfree.cache.message.ClearNotification;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;
import org.greatfree.util.NullObject;

/*
 * The creator that generates the clear notification that is sent to distributed nodes broadcastly in a cluster. 07/12/2017, Bing Li
 */

// Created: 07/12/2017, Bing Li
public class ClearNotificationCreator implements RootBroadcastNotificationCreatable<ClearNotification, NullObject>
{

	/*
	 * The constructor is used to create the clear notifications when the scale of the cluster is even larger than the value of the root branch count. 07/12/2017, Bing Li
	 */
	@Override
	public ClearNotification createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, NullObject message)
	{
		return new ClearNotification(childrenMap);
	}

	/*
	 * The method is used to create the clear notifications when the scale of the cluster is even smaller than the value of the root branch count. 07/12/2017, Bing Li
	 */
	@Override
	public ClearNotification createInstanceWithoutChildren(NullObject message)
	{
		return new ClearNotification();
	}

}
