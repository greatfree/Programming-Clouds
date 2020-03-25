package org.greatfree.cache.message.root;

import java.util.HashMap;

import org.greatfree.cache.message.CloseNotification;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;
import org.greatfree.util.NullObject;

/*
 * The creator that generates the close notification that is sent to distributed nodes broadcastly in a cluster. 07/12/2017, Bing Li
 */

// Created: 07/12/2017, Bing Li
public class CloseNotificationCreator implements RootBroadcastNotificationCreatable<CloseNotification, NullObject>
{

	/*
	 * The constructor is used to create the close notifications when the scale of the cluster is even larger than the value of the root branch count. 07/12/2017, Bing Li
	 */
	@Override
	public CloseNotification createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, NullObject message)
	{
		return new CloseNotification(childrenMap);
	}

	/*
	 * The method is used to create the close notifications when the scale of the cluster is even smaller than the value of the root branch count. 07/12/2017, Bing Li
	 */
	@Override
	public CloseNotification createInstanceWithoutChildren(NullObject message)
	{
		return new CloseNotification();
	}

}
