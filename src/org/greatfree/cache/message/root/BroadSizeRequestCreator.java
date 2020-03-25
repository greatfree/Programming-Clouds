package org.greatfree.cache.message.root;

import java.util.HashMap;

import org.greatfree.cache.message.BroadSizeRequest;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.util.IPAddress;
import org.greatfree.util.NullObject;

/*
 * The creator that generates the broadcast size request that is sent to distributed nodes broadcastly in a cluster. 07/11/2017, Bing Li
 */

// Created: 07/11/2017, Bing Li
public class BroadSizeRequestCreator implements RootBroadcastRequestCreatable<BroadSizeRequest, NullObject>
{

	/*
	 * The constructor is used to create requests when the scale of the cluster is even larger than the value of the root branch count. 07/03/2017, Bing Li
	 */
	@Override
//	public BroadSizeRequest createInstanceWithChildren(String collaboratorKey, HashMap<String, IPAddress> childrenMap, NullObject t)
	public BroadSizeRequest createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, NullObject t)
	{
		return new BroadSizeRequest(childrenMap);
	}

	/*
	 * The method is used to create request when the scale of the cluster is even smaller than the value of the root branch count. 07/03/2017, Bing Li
	 */
	@Override
//	public BroadSizeRequest createInstanceWithoutChildren(String collaboratorKey, NullObject t)
	public BroadSizeRequest createInstanceWithoutChildren(NullObject t)
	{
		return new BroadSizeRequest();
	}

}
