package org.greatfree.abandoned.cache.distributed.root;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.cache.message.BroadGetRequest;
import org.greatfree.cache.message.BroadGetResponse;
import org.greatfree.cache.message.BroadKeysRequest;
import org.greatfree.cache.message.BroadKeysResponse;
import org.greatfree.cache.message.BroadSizeRequest;
import org.greatfree.cache.message.BroadSizeResponse;
import org.greatfree.cache.message.BroadValuesRequest;
import org.greatfree.cache.message.BroadValuesResponse;
import org.greatfree.cache.message.ClearNotification;
import org.greatfree.cache.message.CloseNotification;
import org.greatfree.cache.message.PutNotification;
import org.greatfree.cache.message.RemoveKeyNotification;
import org.greatfree.cache.message.RemoveKeysNotification;
import org.greatfree.cache.message.UniGetRequest;
import org.greatfree.cache.message.UniGetResponse;
import org.greatfree.cache.message.root.BroadGetRequestCreator;
import org.greatfree.cache.message.root.BroadKeysRequestCreator;
import org.greatfree.cache.message.root.BroadSizeRequestCreator;
import org.greatfree.cache.message.root.BroadValuesRequestCreator;
import org.greatfree.cache.message.root.ClearNotificationCreator;
import org.greatfree.cache.message.root.CloseNotificationCreator;
import org.greatfree.cache.message.root.PutNotificationCreator;
import org.greatfree.cache.message.root.RemoveKeyNotificationCreator;
import org.greatfree.cache.message.root.RemoveKeysNotificationCreator;
import org.greatfree.cache.message.root.UniGetRequestCreator;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.old.multicast.root.ClusterRoot;
import org.greatfree.multicast.root.abandoned.ClusterRootBroadcastNotifier;
import org.greatfree.multicast.root.abandoned.ClusterRootBroadcastReader;
import org.greatfree.multicast.root.abandoned.ClusterRootUnicastNotifier;
import org.greatfree.multicast.root.abandoned.ClusterRootUnicastReader;
import org.greatfree.server.Peer;
import org.greatfree.util.NullObject;

/*
 * The previous comments are out of date. I need to update them later. 07/08/2017, Bing Li
 */

/*
 * This class represents a cluster that plays the role of substrate for all of those distributed cache. It should be a singleton such that it needs to differentiate all of the caches with their unique keys. The differentiation is important when receiving responses from the cluster since the dispatcher needs to dispatch the received messages to corresponding caches. 07/03/2017, Bing Li
 */

// Created: 07/03/2017, Bing Li
//class MapClusterRoot<Key extends Serializable, Value extends SerializedKey<Key>> extends ClusterRoot<DistributedCacheRootDispatcher>
//class MapClusterRoot<Value extends CacheKey<String>> extends ClusterRoot<DistributedCacheRootDispatcher>
class MapClusterRoot<Key extends CacheKey<String>, Value extends CacheKey<String>> extends ClusterRoot<DistributedCacheRootDispatcher>
{
	// The cache key the map cluster root belongs to. 07/22/2017, Bing Li
//	private final String cacheKey;

	// Here is one unicast notifier is defined. It sends the evicted value to one specified node in the cluster. 07/03/2017, Bing Li
//	private ClusterRootUnicastNotifier<Value, PutNotification<String, Value>, PutNotificationCreator<String, Value>> putNotifier;
	private ClusterRootUnicastNotifier<Value, PutNotification<Value>, PutNotificationCreator<Value>> putNotifier;

	// The unicast get reader sends a unicast request to one particular node in the cluster to retrieve the value according to the key in the request. 07/03/2017, Bing Li
//	private ClusterRootUnicastReader<String, UniGetRequest<String>, UniGetResponse<String, Value>, UniGetRequestCreator<String>> uniGetReader;
//	private ClusterRootUnicastReader<String, UniGetRequest, UniGetResponse<Value>, UniGetRequestCreator> uniGetReader;
	private ClusterRootUnicastReader<Key, UniGetRequest<Key>, UniGetResponse<Value>, UniGetRequestCreator<Key>> uniGetReader;
	
	// The broadcast get reader sends a broadcast request to each node in the cluster to retrieve the value according to the key in the request. 07/03/2017, Bing Li
	private ClusterRootBroadcastReader<HashSet<String>, BroadGetRequest, BroadGetResponse<Value>, BroadGetRequestCreator> broadGetReader;
	
	// The broadcast size reader sends a broadcast request to each node in the cluster to retrieve the size of the cache on the distributed node. 07/11/2017, Bing Li
	private ClusterRootBroadcastReader<NullObject, BroadSizeRequest, BroadSizeResponse, BroadSizeRequestCreator> broadSizeReader;

	// The broadcast keys reader sends a broadcast request to each node in the cluster to retrieve the keys of the cache on the distributed node. 07/11/2017, Bing Li
	private ClusterRootBroadcastReader<NullObject, BroadKeysRequest, BroadKeysResponse, BroadKeysRequestCreator> broadKeysReader;

	// The broadcast values reader sends a broadcast request to each node in the cluster to retrieve the values of the cache on the distributed node. 07/11/2017, Bing Li
	private ClusterRootBroadcastReader<NullObject, BroadValuesRequest, BroadValuesResponse<Value>, BroadValuesRequestCreator> broadValuesReader;

	// Here is one broadcast notifier is defined. It sends the keys removal notification broadcastly in the cluster. 07/03/2017, Bing Li
	private ClusterRootBroadcastNotifier<HashSet<String>, RemoveKeysNotification, RemoveKeysNotificationCreator> removeKeysNotifier;

	// Here is one unicast notifier is defined. It sends the key removal notification to one specified node in the cluster. 07/03/2017, Bing Li
	private ClusterRootUnicastNotifier<String, RemoveKeyNotification, RemoveKeyNotificationCreator> removeKeyNotifier;

	// Here is one broadcast notifier is defined. It sends the clear notification broadcastly in the cluster. 07/03/2017, Bing Li
	private ClusterRootBroadcastNotifier<NullObject, ClearNotification, ClearNotificationCreator> clearNotifier;
	
	// Here is one broadcast notifier is defined. It sends the close notification broadcastly in the cluster. 07/03/2017, Bing Li
	private ClusterRootBroadcastNotifier<NullObject, CloseNotification, CloseNotificationCreator> closeNotifier;

	/*
	 * The cache cluster root needs to share the peer with others. 07/05/2017, Bing Li
	 */
//	public MapClusterRoot(String cacheKey, Peer<DistributedCacheRootDispatcher> peer)
	public MapClusterRoot(Peer<DistributedCacheRootDispatcher> peer)
	{
		super(peer);
//		this.cacheKey = cacheKey;
	}

	/*
	 * Initialize the cache cluster root. 07/05/2017, Bing Li
	 */
	@Override
	public void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException
	{
		// Start the cluster root on the system level. 07/05/2017, Bing Li
		super.start();
		
		// Initialize the cluster root unicast notifier for data retaining. 05/19/2017, Bing Li
//		this.putNotifier = new ClusterRootUnicastNotifier<Value, PutNotification<String, Value>, PutNotificationCreator<String, Value>>(super.getClientPool(), ClusterConfig.MULTICASTOR_POOL_SIZE, ClusterConfig.RESOURCE_WAIT_TIME, new PutNotificationCreator<String, Value>());
		this.putNotifier = new ClusterRootUnicastNotifier<Value, PutNotification<Value>, PutNotificationCreator<Value>>(super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new PutNotificationCreator<Value>());

		// Initialize the cluster root unicast reader. 05/20/2017, Bing Li
//		this.uniGetReader = new ClusterRootUnicastReader<String, UniGetRequest<String>, UniGetResponse<String, Value>, UniGetRequestCreator<String>>(super.getClientPool(), ClusterConfig.MULTICASTOR_POOL_SIZE, ClusterConfig.RESOURCE_WAIT_TIME, ClusterConfig.BROADCAST_REQUEST_WAIT_TIME, new UniGetRequestCreator<String>());
//		this.uniGetReader = new ClusterRootUnicastReader<String, UniGetRequest, UniGetResponse<Value>, UniGetRequestCreator>(super.getClientPool(), ClusterConfig.MULTICASTOR_POOL_SIZE, ClusterConfig.RESOURCE_WAIT_TIME, ClusterConfig.BROADCAST_REQUEST_WAIT_TIME, new UniGetRequestCreator());
		this.uniGetReader = new ClusterRootUnicastReader<Key, UniGetRequest<Key>, UniGetResponse<Value>, UniGetRequestCreator<Key>>(super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, new UniGetRequestCreator<Key>());
		
		// Initialize the cluster root broadcast reader for retrieving values. 05/20/2017, Bing Li
		this.broadGetReader = new ClusterRootBroadcastReader<HashSet<String>, BroadGetRequest, BroadGetResponse<Value>, BroadGetRequestCreator>(super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, new BroadGetRequestCreator());
		
		// Initialize the cluster root broadcast reader for retrieving the size. 05/20/2017, Bing Li
		this.broadSizeReader = new ClusterRootBroadcastReader<NullObject, BroadSizeRequest, BroadSizeResponse, BroadSizeRequestCreator>(super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, new BroadSizeRequestCreator());

		// Initialize the cluster root broadcast reader for retrieving the keys. 05/20/2017, Bing Li
		this.broadKeysReader = new ClusterRootBroadcastReader<NullObject, BroadKeysRequest, BroadKeysResponse, BroadKeysRequestCreator>(super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, new BroadKeysRequestCreator());

		// Initialize the cluster root broadcast reader for retrieving the values. 05/20/2017, Bing Li
		this.broadValuesReader = new ClusterRootBroadcastReader<NullObject, BroadValuesRequest, BroadValuesResponse<Value>, BroadValuesRequestCreator>(super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, new BroadValuesRequestCreator());

		// Initialize the cluster root broadcast notifier for keys removal. 05/19/2017, Bing Li
		this.removeKeysNotifier = new ClusterRootBroadcastNotifier<HashSet<String>, RemoveKeysNotification, RemoveKeysNotificationCreator>(super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new RemoveKeysNotificationCreator());

		// Initialize the cluster root unicast notifier for the key removal. 05/19/2017, Bing Li
		this.removeKeyNotifier = new ClusterRootUnicastNotifier<String, RemoveKeyNotification, RemoveKeyNotificationCreator>(super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new RemoveKeyNotificationCreator());

		// Initialize the cluster root broadcast notifier for clear. 05/19/2017, Bing Li
		this.clearNotifier = new ClusterRootBroadcastNotifier<NullObject, ClearNotification, ClearNotificationCreator>(super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new ClearNotificationCreator());

		// Initialize the cluster root broadcast notifier for closing. 05/19/2017, Bing Li
		this.closeNotifier = new ClusterRootBroadcastNotifier<NullObject, CloseNotification, CloseNotificationCreator>(super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new CloseNotificationCreator());
	}

	/*
	 * Terminate the cache cluster root. 07/05/2017, Bing Li
	 */
	@Override
	public void terminate(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		// Stop the cluster root on the system level. 07/05/2017, Bing Li
		super.stop(timeout);

		// Dispose the put notifier. 07/05/2017, Bing Li
		this.putNotifier.dispose();
		
		// Dispose the unicast get reader. 07/05/2017, Bing Li
		this.uniGetReader.dispose();
		
		// Dispose the broadcast get reader. 07/05/2017, Bing Li
		this.broadGetReader.dispose();
		
		// Dispose the broadcast size reader. 07/05/2017, Bing Li
		this.broadSizeReader.dispose();
		
		// Dispose the broadcast keys reader. 07/05/2017, Bing Li
		this.broadKeysReader.dispose();
		
		// Dispose the broadcast values reader. 07/05/2017, Bing Li
		this.broadValuesReader.dispose();
		
		// Dispose the keys removal notifier. 07/12/2017, Bing Li
		this.removeKeysNotifier.dispose();

		// Dispose the key removal notifier. 07/12/2017, Bing Li
		this.removeKeyNotifier.dispose();
		
		// Dispose the clear notifier. 07/12/2017, Bing Li
		this.clearNotifier.dispose();
		
		// Dispose the close notifier. 07/12/2017, Bing Li
		this.closeNotifier.dispose();
	}
	
	/*
	 * Unicast the notification to one randomly selected node from the distributed nodes in the cluster. 05/15/2017, Bing Li
	 */
	/*
	public void unicastNotifyRandomly(Value value, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.putNotifier.notifiy(value, rootBranchCount, subBranchCount);
	}
	*/
	
	/*
	 * Unicast the notification to one nearest node from the distributed nodes in the cluster. 05/15/2017, Bing Li
	 */
	public void unicastNotifyNearestly(Value value, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException
	{
		this.putNotifier.notifyNearestly(value, value.getDataKey(), rootBranchCount, subBranchCount);
	}

	/*
	 * Unicast the request to one nearest node from the distributed nodes in the cluster. 05/15/2017, Bing Li
	 */
//	public Map<String, UniGetResponse<String, Value>> unicastReadNearestly(String key, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
//	public Map<String, UniGetResponse<Value>> unicastReadNearestly(String key, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	public Map<String, UniGetResponse<Value>> unicastReadNearestly(Key key, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// The first parameter, key, is used to generate the nearest node. The second parameter, key, is the key of the value to be retrieved. They are the same in the case. In other cases, it might not be identical. So two parameters are retained. 07/10/2017, Bing Li
		return this.uniGetReader.readNearestly(key.getDataKey(), key, rootBranchCount, subBranchCount);
	}
	
	/*
	 * Broadcast the request to nearest nodes from the distributed nodes in the cluster. 05/15/2017, Bing Li
	 */
	public Map<String, BroadGetResponse<Value>> broadcastReadNearestly(HashSet<String> keys, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		// The first parameter, keys, is used to generate the nearest nodes. The second parameter, keys, is the keys of the values to be retrieved. They are the same in the case. In other cases, it might not be identical. So two parameters are retained. 07/10/2017, Bing Li
		return this.broadGetReader.readNearestly(keys, keys, rootBranchCount, subBranchCount);
	}
	
	/*
	 * Broadcast the size request to each node in the cluster. 07/11/2017, Bing Li
	 */
	public Map<String, BroadSizeResponse> broadcastReadSize(int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.broadSizeReader.read(null, rootBranchCount, subBranchCount);
	}
	
	/*
	 * Broadcast the keys request to each node in the cluster. 07/11/2017, Bing Li
	 */
	public Map<String, BroadKeysResponse> broadcastReadKeys(int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.broadKeysReader.read(null, rootBranchCount, subBranchCount);
	}

	/*
	 * Broadcast the values request to each node in the cluster. 07/11/2017, Bing Li
	 */
	public Map<String, BroadValuesResponse<Value>> broadcastReadValues(int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.broadValuesReader.read(null, rootBranchCount, subBranchCount);
	}

	/*
	 * Broadcast the keys removal notification to each node in the cluster. 07/12/2017, Bing Li
	 */
	public void broadcastRemovalKeys(HashSet<String> keys, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.removeKeysNotifier.notifiy(keys, rootBranchCount, subBranchCount);
	}

	/*
	 * Unicast the key removal notification to one nearest node from the distributed nodes in the cluster. 07/12/2017, Bing Li
	 */
	public void unicastNotifyNearestly(String key, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException
	{
		this.removeKeyNotifier.notifyNearestly(key, key, rootBranchCount, subBranchCount);
	}

	/*
	 * Broadcast the clear notification to each node in the cluster. 07/12/2017, Bing Li
	 */
	public void broadcastClear(int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.clearNotifier.notifiy(null, rootBranchCount, subBranchCount);
	}
	
	/*
	 * Broadcast the close notification to each node in the cluster. 07/12/2017, Bing Li
	 */
	public void broadcastClose(int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.closeNotifier.notifiy(null, rootBranchCount, subBranchCount);
	}
}
