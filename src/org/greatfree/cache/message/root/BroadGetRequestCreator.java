package org.greatfree.cache.message.root;

import java.util.HashMap;
import java.util.HashSet;

import org.greatfree.cache.message.BroadGetRequest;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator that generates the broadcast get request that is sent to distributed nodes broadcastly in a cluster. 07/03/2017, Bing Li
 */

// Created: 07/10/2017, Bing Li
public class BroadGetRequestCreator implements RootBroadcastRequestCreatable<BroadGetRequest, HashSet<String>>
{

	/*
	 * The constructor is used to create requests when the scale of the cluster is even larger than the value of the root branch count. 07/03/2017, Bing Li
	 */
	@Override
//	public BroadGetRequest createInstanceWithChildren(String collaboratorKey, HashMap<String, IPAddress> childrenMap, HashSet<String> t)
	public BroadGetRequest createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, HashSet<String> t)
	{
//		return new BroadGetRequest(Tools.generateUniqueKey(), collaboratorKey, childrenMap, t);
		return new BroadGetRequest(childrenMap, t);
	}

	/*
	 * The method is used to create request when the scale of the cluster is even smaller than the value of the root branch count. 07/03/2017, Bing Li
	 */
	@Override
//	public BroadGetRequest createInstanceWithoutChildren(String collaboratorKey, HashSet<String> t)
	public BroadGetRequest createInstanceWithoutChildren(HashSet<String> t)
	{
//		return new BroadGetRequest(Tools.generateUniqueKey(), collaboratorKey, t);
		return new BroadGetRequest(t);
	}
}
