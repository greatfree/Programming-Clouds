package org.greatfree.cache.message;

import java.util.HashMap;

import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.util.IPAddress;

/*
 * This is a broadcast notification to notify each node in the cluster to clear their data. 07/12/2017, Bing Li
 */

// Created: 07/12/2017, Bing Li
public class ClearNotification extends OldMulticastMessage
{
	private static final long serialVersionUID = -405430518237456310L;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 07/01/2017, Bing Li
	 */
//	public ClearNotification(String key)
	public ClearNotification()
	{
		super(CacheMessageType.CLEAR_NOTIFICATION);
	}

	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public ClearNotification(String key, HashMap<String, IPAddress> childrenServers)
	public ClearNotification(HashMap<String, IPAddress> childrenServers)
	{
		super(CacheMessageType.CLEAR_NOTIFICATION, childrenServers);
	}
}
