package org.greatfree.dsf.multicast.message;

import org.greatfree.message.multicast.MulticastNotification;

/*
 * After receiving the administration message of shutting down from the administrator, the root sends a broadcast notification to its children. 05/19/2017, Bing Li
 */

// Created: 05/19/2017, Bing Li
public class ShutdownChildrenBroadcastNotification extends MulticastNotification
{
	private static final long serialVersionUID = -8645482803978813301L;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public ShutdownChildrenBroadcastNotification(String key)
	public ShutdownChildrenBroadcastNotification()
	{
		super(MulticastDIPMessageType.SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION);
	}

	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public ShutdownChildrenBroadcastNotification(String key, HashMap<String, IPAddress> childrenServers)
	/*
	public ShutdownChildrenBroadcastNotification(HashMap<String, IPAddress> childrenServers)
	{
		super(MulticastMessageType.SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION, childrenServers);
	}
	*/
}
