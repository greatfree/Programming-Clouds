package org.greatfree.cache.message;

import java.util.HashMap;

import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.util.IPAddress;

/*
 * The request is broadcast to each of the distributed node in a cluster and then each of the node needs to respond all of the values of the local cache to the root individually. The responses are merged together as the final response. 07/11/2017, Bing Li
 */

// Created: 07/11/2017, Bing Li
public class BroadValuesRequest extends OldMulticastRequest
{
	private static final long serialVersionUID = -3511227010675831216L;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 07/11/2017, Bing Li
	 */
//	public BroadValuesRequest(String key, String collaboratorKey)
//	public BroadValuesRequest(String collaboratorKey)
	public BroadValuesRequest()
	{
		super(CacheMessageType.BROAD_VALUES_REQUEST);
	}

	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public BroadValuesRequest(String key, String collaboratorKey, HashMap<String, IPAddress> childrenServerMap)
//	public BroadValuesRequest(String collaboratorKey, HashMap<String, IPAddress> childrenServerMap)
	public BroadValuesRequest(HashMap<String, IPAddress> childrenServerMap)
	{
		super(CacheMessageType.BROAD_VALUES_REQUEST, childrenServerMap);
	}
}
