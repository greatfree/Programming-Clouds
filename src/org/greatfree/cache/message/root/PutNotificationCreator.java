package org.greatfree.cache.message.root;

import java.util.HashMap;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.cache.message.PutNotification;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator that generates the put notification that is sent to distributed nodes unicastly in a cluster. 07/03/2017, Bing Li
 */

// Created: 07/03/2017, Bing Li
//public class PutNotificationCreator<Key extends Serializable, Value extends SerializedKey<Key>> implements RootBroadcastNotificationCreatable<PutNotification<Key, Value>, Value>
//public class PutNotificationCreator<Value extends SerializedKey<String>> implements RootCacheBroadcastNotificationCreatable<PutNotification<Value>, Value>
//public class PutNotificationCreator<Value extends CacheKey<String>> implements CacheRootBroadcastNotificationCreatable<PutNotification<Value>, Value>
public class PutNotificationCreator<Value extends CacheKey<String>> implements RootBroadcastNotificationCreatable<PutNotification<Value>, Value>
{

	/*
	@Override
	public PutNotification<Value> createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, String cacheKey, Value message)
	{
		return new PutNotification<Value>(Tools.generateUniqueKey(), childrenMap, cacheKey, message);
	}

	@Override
	public PutNotification<Value> createInstanceWithoutChildren(String cacheKey, Value message)
	{
		return new PutNotification<Value>(Tools.generateUniqueKey(), cacheKey, message);
	}
	*/

	/*
	 * The constructor is used to create put notifications when the scale of the cluster is even larger than the value of the root branch count. 07/03/2017, Bing Li
	 */
	@Override
//	public PutNotification<Value> createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, Value message, String cacheKey)
	public PutNotification<Value> createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, Value message)
	{
		return new PutNotification<Value>(childrenMap, message);
	}

	/*
	 * The method is used to create put notifications when the scale of the cluster is even smaller than the value of the root branch count. 07/03/2017, Bing Li
	 */
	@Override
//	public PutNotification<Value> createInstanceWithoutChildren(Value message, String cacheKey)
	public PutNotification<Value> createInstanceWithoutChildren(Value message)
	{
		return new PutNotification<Value>(message);
	}
	
	/*
	 * The constructor is used to create put notifications when the scale of the cluster is even larger than the value of the root branch count. 07/03/2017, Bing Li
	 */
	/*
	@Override
	public PutNotification<Key, Value> createInstanceWithChildren(Value message, HashMap<String, IPAddress> childrenMap)
	{
		return new PutNotification<Key, Value>(Tools.generateUniqueKey(), childrenMap, message);
	}
	*/

	/*
	 * The method is used to create put notifications when the scale of the cluster is even smaller than the value of the root branch count. 07/03/2017, Bing Li
	 */
	/*
	@Override
	public PutNotification<Key, Value> createInstanceWithoutChildren(Value message)
	{
		return new PutNotification<Key, Value>(Tools.generateUniqueKey(), message);
	}
	*/

}
