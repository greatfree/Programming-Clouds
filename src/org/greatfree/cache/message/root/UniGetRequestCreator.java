package org.greatfree.cache.message.root;

import java.util.HashMap;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.cache.message.UniGetRequest;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator that generates the unicast get request that is sent to distributed nodes unicastly in a cluster. 07/03/2017, Bing Li
 */

// Created: 07/03/2017, Bing Li
//public class UniGetRequestCreator<Key extends Serializable> implements RootBroadcastRequestCreatable<UniGetRequest<Key>, Key>
//public class UniGetRequestCreator implements RootBroadcastRequestCreatable<UniGetRequest, String>
public class UniGetRequestCreator<Key extends CacheKey<String>> implements RootBroadcastRequestCreatable<UniGetRequest<Key>, Key>
{
	/*
	 * The constructor is used to create requests when the scale of the cluster is even larger than the value of the root branch count. 07/03/2017, Bing Li
	 */
	@Override
//	public UniGetRequest createInstanceWithChildren(String collaboratorKey, HashMap<String, IPAddress> childrenMap, String t)
	public UniGetRequest<Key> createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, Key t)
	{
		return new UniGetRequest<Key>(childrenMap, t);
	}

	/*
	 * The method is used to create request when the scale of the cluster is even smaller than the value of the root branch count. 07/03/2017, Bing Li
	 */
	@Override
//	public UniGetRequest createInstanceWithoutChildren(String collaboratorKey, String t)
	public UniGetRequest<Key> createInstanceWithoutChildren(Key t)
	{
		return new UniGetRequest<Key>(t);
	}
	
	/*
	 * The constructor is used to create requests when the scale of the cluster is even larger than the value of the root branch count. 07/03/2017, Bing Li
	 */
	/*
	@Override
	public UniGetRequest<Key> createInstanceWithChildren(Key t, String collaboratorKey, HashMap<String, IPAddress> childrenMap)
	{
		return new UniGetRequest<Key>(t, Tools.generateUniqueKey(), collaboratorKey, childrenMap);
	}
	*/

	/*
	 * The method is used to create request when the scale of the cluster is even smaller than the value of the root branch count. 07/03/2017, Bing Li
	 */
	/*
	@Override
	public UniGetRequest<Key> createInstanceWithoutChildren(Key t, String collaboratorKey)
	{
		return new UniGetRequest<Key>(t, Tools.generateUniqueKey(), collaboratorKey);
	}
	*/

}
