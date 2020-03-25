package org.greatfree.cache.message;

import java.util.HashMap;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.util.IPAddress;

/*
 * The request is unicast to the nearest one of the distributed nodes in a cluster and then the node that receives the request needs to respond to the root individually. The responses are merged together as the final response. 05/20/2017, Bing Li
 */

// Created: 07/03/2017, Bing Li
//public class UniGetRequest<Key> extends ClusterBroadcastRequest
//public class UniGetRequest extends ClusterBroadcastRequest
public class UniGetRequest<Key extends CacheKey<String>> extends OldMulticastRequest
{
	private static final long serialVersionUID = -466925340032850568L;
	
//	private String requestedKey;
	private Key requestedKey;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public UniGetRequest(Key requestedKey, String key, String collaboratorKey)
//	public UniGetRequest(String key, String collaboratorKey, String requestedKey)
//	public UniGetRequest(String key, String collaboratorKey, Key requestedKey)
//	public UniGetRequest(String collaboratorKey, Key requestedKey)
	public UniGetRequest(Key requestedKey)
	{
		super(CacheMessageType.UNI_GET_REQUEST);
		this.requestedKey = requestedKey;
	}

	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public UniGetRequest(Key requestedKey, String key, String collaboratorKey, HashMap<String, IPAddress> childrenServerMap)
//	public UniGetRequest(String key, String collaboratorKey, HashMap<String, IPAddress> childrenServerMap, String requestedKey)
//	public UniGetRequest(String key, String collaboratorKey, HashMap<String, IPAddress> childrenServerMap, Key requestedKey)
//	public UniGetRequest(String collaboratorKey, HashMap<String, IPAddress> childrenServerMap, Key requestedKey)
	public UniGetRequest(HashMap<String, IPAddress> childrenServerMap, Key requestedKey)
	{
		super(CacheMessageType.UNI_GET_REQUEST, childrenServerMap);
		this.requestedKey = requestedKey;
	}

	/*
	 * Expose the requested key. 07/10/2017, Bing Li
	 */
//	public String getRequestedKey()
	public Key getRequestedKey()
	{
		return this.requestedKey;
	}
}
