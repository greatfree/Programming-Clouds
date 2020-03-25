package org.greatfree.cache.message.root;

import java.util.HashMap;

import org.greatfree.cache.message.BroadValuesRequest;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.util.IPAddress;
import org.greatfree.util.NullObject;

/*
 * The creator that generates the broadcast values request that is sent to distributed nodes broadcastly in a cluster. 07/12/2017, Bing Li
 */

// Created: 07/12/2017, Bing Li
public class BroadValuesRequestCreator implements RootBroadcastRequestCreatable<BroadValuesRequest, NullObject>
{

	/*
	 * The constructor is used to create requests when the scale of the cluster is even larger than the value of the root branch count. 07/03/2017, Bing Li
	 */
	@Override
//	public BroadValuesRequest createInstanceWithChildren(String collaboratorKey, HashMap<String, IPAddress> childrenMap, NullObject t)
	public BroadValuesRequest createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, NullObject t)
	{
		return new BroadValuesRequest(childrenMap);
	}

	/*
	 * The method is used to create request when the scale of the cluster is even smaller than the value of the root branch count. 07/03/2017, Bing Li
	 */
	@Override
//	public BroadValuesRequest createInstanceWithoutChildren(String collaboratorKey, NullObject t)
	public BroadValuesRequest createInstanceWithoutChildren(NullObject t)
	{
		return new BroadValuesRequest();
	}

}
