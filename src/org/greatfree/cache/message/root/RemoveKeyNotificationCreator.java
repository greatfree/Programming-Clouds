package org.greatfree.cache.message.root;

import java.util.HashMap;

import org.greatfree.cache.message.RemoveKeyNotification;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator that generates the key removal notification that is sent to one specified distributed node unicastly in a cluster. 07/12/2017, Bing Li
 */

// Created: 07/12/2017, Bing Li
public class RemoveKeyNotificationCreator implements RootBroadcastNotificationCreatable<RemoveKeyNotification, String>
{

	/*
	 * The constructor is used to create one key removal notification when the scale of the cluster is even larger than the value of the root branch count. 07/12/2017, Bing Li
	 */
	@Override
	public RemoveKeyNotification createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, String message)
	{
		return new RemoveKeyNotification(childrenMap, message);
	}

	/*
	 * The method is used to create one key removal notification when the scale of the cluster is even smaller than the value of the root branch count. 07/12/2017, Bing Li
	 */
	@Override
	public RemoveKeyNotification createInstanceWithoutChildren(String message)
	{
		return new RemoveKeyNotification(message);
	}

}
