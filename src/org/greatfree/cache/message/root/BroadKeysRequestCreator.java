package org.greatfree.cache.message.root;

import java.util.HashMap;

import org.greatfree.cache.message.BroadKeysRequest;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.util.IPAddress;
import org.greatfree.util.NullObject;

/*
 * The creator that generates the broadcast keys request that is sent to distributed nodes broadcastly in a cluster. 07/11/2017, Bing Li
 */

// Created: 07/11/2017, Bing Li
public class BroadKeysRequestCreator implements RootBroadcastRequestCreatable<BroadKeysRequest, NullObject>
{

	/*
	 * The constructor is used to create requests when the scale of the cluster is even larger than the value of the root branch count. 07/03/2017, Bing Li
	 */
	@Override
//	public BroadKeysRequest createInstanceWithChildren(String collaboratorKey, HashMap<String, IPAddress> childrenMap, NullObject t)
	public BroadKeysRequest createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, NullObject t)
	{
//		return new BroadKeysRequest(Tools.generateUniqueKey(), collaboratorKey, childrenMap);
		return new BroadKeysRequest(childrenMap);
	}

	/*
	 * The method is used to create request when the scale of the cluster is even smaller than the value of the root branch count. 07/03/2017, Bing Li
	 */
	@Override
//	public BroadKeysRequest createInstanceWithoutChildren(String collaboratorKey, NullObject t)
	public BroadKeysRequest createInstanceWithoutChildren(NullObject t)
	{
//		return new BroadKeysRequest(Tools.generateUniqueKey(), collaboratorKey);
		return new BroadKeysRequest();
	}

}
