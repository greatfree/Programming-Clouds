package org.greatfree.cache.message;

import java.util.HashMap;
import java.util.Set;

import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.util.IPAddress;

/*
 * The request is broadcast to the nearest ones of the distributed nodes in a cluster and then the nodes that receive the request need to respond to the root individually. The responses are merged together as the final response. 05/20/2017, Bing Li
 */

// Created: 07/10/2017, Bing Li
//public class BroadGetRequest<Key> extends ClusterBroadcastRequest
public class BroadGetRequest extends OldMulticastRequest
{
	private static final long serialVersionUID = -8848568637095828869L;
	
	private Set<String> requestedKeys;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public BroadGetRequest(Set<Key> requestedKeys, String key, String collaboratorKey)
//	public BroadGetRequest(String key, String collaboratorKey, Set<String> requestedKeys)
//	public BroadGetRequest(String collaboratorKey, Set<String> requestedKeys)
	public BroadGetRequest(Set<String> requestedKeys)
	{
		super(CacheMessageType.BROAD_GET_REQUEST);
		this.requestedKeys = requestedKeys;
	}

	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public BroadGetRequest(Set<Key> requestedKeys, String key, String collaboratorKey, HashMap<String, IPAddress> childrenServerMap)
//	public BroadGetRequest(String key, String collaboratorKey, HashMap<String, IPAddress> childrenServerMap, Set<String> requestedKeys)
//	public BroadGetRequest(String collaboratorKey, HashMap<String, IPAddress> childrenServerMap, Set<String> requestedKeys)
	public BroadGetRequest(HashMap<String, IPAddress> childrenServerMap, Set<String> requestedKeys)
	{
		super(CacheMessageType.BROAD_GET_REQUEST, childrenServerMap);
		this.requestedKeys = requestedKeys;
	}
	
	/*
	 * Expose the requested keys. 07/10/2017, Bing Li
	 */
//	public Set<Key> getRequestedKeys()
	public Set<String> getRequestedKeys()
	{
		return this.requestedKeys;
	}
}
