package org.greatfree.abandoned.cache.distributed.child;

import java.io.IOException;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;
import org.greatfree.cache.message.BroadGetRequest;
import org.greatfree.cache.message.BroadKeysRequest;
import org.greatfree.cache.message.BroadSizeRequest;
import org.greatfree.cache.message.BroadValuesRequest;
import org.greatfree.cache.message.ClearNotification;
import org.greatfree.cache.message.CloseNotification;
import org.greatfree.cache.message.RemoveKeysNotification;
import org.greatfree.cache.message.UniGetResponse;
import org.greatfree.cache.message.child.BroadGetRequestCreator;
import org.greatfree.cache.message.child.BroadKeysRequestCreator;
import org.greatfree.cache.message.child.BroadSizeRequestCreator;
import org.greatfree.cache.message.child.BroadValuesRequestCreator;
import org.greatfree.cache.message.child.ClearNotificationCreator;
import org.greatfree.cache.message.child.CloseNotificationCreator;
import org.greatfree.cache.message.child.RemoveKeysNotificationCreator;
import org.greatfree.dsf.multicast.MulticastConfig;
import org.greatfree.dsf.old.multicast.child.ClusterChild;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.multicast.child.abandoned.ClusterChildBroadcastNotifier;
import org.greatfree.multicast.child.abandoned.ClusterChildBroadcastReader;
import org.greatfree.server.Peer;

/*
 * The previous comments are out of date. I need to update them later. 07/08/2017, Bing Li
 */

/*
 * This class represents a cluster that plays the role of substrate for all of those distributed cache. It should be a singleton such that it needs to differentiate all of the caches with their unique keys. The differentiation is important when receiving responses from the cluster since the dispatcher needs to dispatch the received messages to corresponding caches. 07/03/2017, Bing Li
 */

// Created: 07/08/2017, Bing Li
class MapClusterChild<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> extends ClusterChild<DistributedCacheChildDispatcher<Key, Value, Factory, DB>>
{
	// The child map key. 07/20/2017, Bing Li
//	private final String childMapKey;
	// The root map key. 07/20/2017, Bing Li
//	private final String rootMapKey;
	// The request broadcastor to retrieve values according to the given keys from the local cache and its children. 07/14/2017, Bing Li
	private ClusterChildBroadcastReader<BroadGetRequest, BroadGetRequestCreator> getValuesBroadcastReader;
	// The request broadcastor to retrieve the size of the local cache and its children. 07/14/2017, Bing Li
	private ClusterChildBroadcastReader<BroadSizeRequest, BroadSizeRequestCreator> getSizeBroadcastReader;
	// The request broadcastor to retrieve keys of the local cache and its children. 07/14/2017, Bing Li
	private ClusterChildBroadcastReader<BroadKeysRequest, BroadKeysRequestCreator> getKeysBroadcastReader;
	// The request broadcastor to retrieve all the values from the local cache and its children. 07/14/2017, Bing Li
	private ClusterChildBroadcastReader<BroadValuesRequest, BroadValuesRequestCreator> getAllValuesBroadcastReader;
	// The notification broadcastor to notify its children to remove the values according to the given keys. 07/14/2017, Bing Li
	private ClusterChildBroadcastNotifier<RemoveKeysNotification, RemoveKeysNotificationCreator> removeKeysBroadcastNotifier;
	// The notification broadcastor to notify its children to clear their caches. 07/14/2017, Bing Li
	private ClusterChildBroadcastNotifier<ClearNotification, ClearNotificationCreator> clearBroadcastNotifier;
	// The notification broadcastor to notify its children to close their caches. 07/14/2017, Bing Li
	private ClusterChildBroadcastNotifier<CloseNotification, CloseNotificationCreator> closeBroadcastNotifier;
	
	/*
	 * The cache cluster child needs to share the peer with others. 07/05/2017, Bing Li
	 */
//	public MapClusterChild(String childMapKey, String rootMapKey, Peer<DistributedCacheChildDispatcher<Value, Factory, DB>> peer)
	public MapClusterChild(Peer<DistributedCacheChildDispatcher<Key, Value, Factory, DB>> peer)
	{
		super(peer);
//		this.childMapKey = childMapKey;
//		this.rootMapKey = rootMapKey;
	}

	/*
	 * Initialize the cache cluster child. 07/05/2017, Bing Li
	 */
	@Override
	public void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException
	{
		// Start the cluster child on the system level. 07/05/2017, Bing Li
		super.start();

		// Join one particular map. 07/20/2017, Bing Li
//		super.syncNotify(ChatConfig.CHAT_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new JoinMapNotification(this.childMapKey, this.rootMapKey));

		// Initialize the broadcasting reader to retrieve the values according to given keys. 07/14/2017, Bing Li
		this.getValuesBroadcastReader = new ClusterChildBroadcastReader<BroadGetRequest, BroadGetRequestCreator>(super.getLocalIPKey(), super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new BroadGetRequestCreator());

		// Initialize the broadcasting reader to get the size of the entire distributed cache. 07/14/2017, Bing Li
		this.getSizeBroadcastReader = new ClusterChildBroadcastReader<BroadSizeRequest, BroadSizeRequestCreator>(super.getLocalIPKey(), super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new BroadSizeRequestCreator());

		// Initialize the broadcasting reader to retrieve all of the keys of the entire distributed cache. 07/14/2017, Bing Li
		this.getKeysBroadcastReader = new ClusterChildBroadcastReader<BroadKeysRequest, BroadKeysRequestCreator>(super.getLocalIPKey(), super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new BroadKeysRequestCreator());

		// Initialize the broadcasting reader to retrieve all of the values of the entire distributed cache. 07/14/2017, Bing Li
		this.getAllValuesBroadcastReader = new ClusterChildBroadcastReader<BroadValuesRequest, BroadValuesRequestCreator>(super.getLocalIPKey(), super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new BroadValuesRequestCreator());
		
		// Initialize the broadcasting notifier to remove the values according to the given keys from the local cache and its children. 07/14/2017, Bing Li
		this.removeKeysBroadcastNotifier = new ClusterChildBroadcastNotifier<RemoveKeysNotification, RemoveKeysNotificationCreator>(super.getLocalIPKey(), super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new RemoveKeysNotificationCreator());

		// Initialize the broadcasting notifier to clear all of the values from the local cache and its children. 07/14/2017, Bing Li
		this.clearBroadcastNotifier = new ClusterChildBroadcastNotifier<ClearNotification, ClearNotificationCreator>(super.getLocalIPKey(), super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new ClearNotificationCreator());

		// Initialize the broadcasting notifier to close the local cache and the ones at its children. 07/14/2017, Bing Li
		this.closeBroadcastNotifier = new ClusterChildBroadcastNotifier<CloseNotification, CloseNotificationCreator>(super.getLocalIPKey(), super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new CloseNotificationCreator());
	}

	/*
	 * Terminate the cache cluster child. 07/05/2017, Bing Li
	 */
	@Override
	public void terminate(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		// Stop the cluster child on the system level. 07/05/2017, Bing Li
		super.stop(timeout);
		
		// Dispose the values broadcasting reader for the given keys. 07/14/2017, Bing Li
		this.getValuesBroadcastReader.dispose();
		
		// Dispose the size broadcasting reader. 07/14/2017, Bing Li
		this.getSizeBroadcastReader.dispose();

		// Dispose the keys broadcasting reader. 07/14/2017, Bing Li
		this.getKeysBroadcastReader.dispose();
		
		// Dispose the values broadcasting reader. 07/14/2017, Bing Li
		this.getAllValuesBroadcastReader.dispose();
		
		// Dispose the keys removal broadcasting notifier. 07/14/2017, Bing Li
		this.removeKeysBroadcastNotifier.dispose();
		
		// Dispose the clear broadcasting notifier. 07/14/2017, Bing Li
		this.clearBroadcastNotifier.dispose();
		
		// Dispose the close broadcasting notifier. 07/14/2017, Bing Li
		this.closeBroadcastNotifier.dispose();
	}

	/*
	 * Send the retrieved value to the root. 07/09/2017, Bing Li
	 */
//	public void respondToRoot(UniGetResponse<String, Value> response) throws IOException
	public void respondToRoot(UniGetResponse<Value> response) throws IOException, InterruptedException
	{
		super.respondToRoot(response);
	}
	
	/*
	 * Broadcast the keys within the cluster to retrieve values by given keys. 07/14/2017, Bing Li
	 */
	public void broadcastGivenKeysRequest(BroadGetRequest request, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.getValuesBroadcastReader.read(request, subBranchCount);
	}

	/*
	 * Broadcast the request to get the size of the distributed map to the local node's children. 07/14/2017, Bing Li
	 */
	public void broadcastGetSizeRequest(BroadSizeRequest request, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.getSizeBroadcastReader.read(request, subBranchCount);
	}
	
	/*
	 * Broadcast the request to get all of the keys of the distributed map to the local node's children. 07/14/2017, Bing Li
	 */
	public void broadcastGetAllKeysRequest(BroadKeysRequest request, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.getKeysBroadcastReader.read(request, subBranchCount);
	}

	/*
	 * Broadcast the request to get all of the values of the distributed map to the local node's children. 07/14/2017, Bing Li
	 */
	public void broadcastGetAllValuesRequest(BroadValuesRequest request, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.getAllValuesBroadcastReader.read(request, subBranchCount);
	}
	
	/*
	 * Broadcast the notification to remove keys to the local node's children. 07/14/2017, Bing Li
	 */
	public void broadcastRemovalKeysNotification(RemoveKeysNotification notification, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.removeKeysBroadcastNotifier.notifiy(notification, subBranchCount);
	}
	
	/*
	 * Broadcast the notification to clear the distributed map to the local node's children. 07/14/2017, Bing Li
	 */
	public void broadcastClearNotification(ClearNotification notification, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.clearBroadcastNotifier.notifiy(notification, subBranchCount);
	}
	
	/*
	 * Broadcast the notification to close the distributed map to the local node's children. 07/14/2017, Bing Li
	 */
	public void broadcastCloseNotification(CloseNotification notification, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.closeBroadcastNotifier.notifiy(notification, subBranchCount);
	}
}
