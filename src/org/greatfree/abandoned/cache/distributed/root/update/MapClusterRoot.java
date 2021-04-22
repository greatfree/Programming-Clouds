package org.greatfree.abandoned.cache.distributed.root.update;

import java.io.IOException;

import org.greatfree.abandoned.cache.distributed.CacheValue;
import org.greatfree.abandoned.cache.distributed.root.DistributedCacheRootDispatcher;
import org.greatfree.cache.message.root.update.PutNotificationCreator;
import org.greatfree.cache.message.update.PutNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.old.multicast.root.ClusterRoot;
import org.greatfree.multicast.root.abandoned.ClusterRootUnicastNotifier;
import org.greatfree.server.Peer;

// Created: 07/16/2017, Bing Li
class MapClusterRoot extends ClusterRoot<DistributedCacheRootDispatcher>
{
	private ClusterRootUnicastNotifier<CacheValue, PutNotification, PutNotificationCreator> putNotifier;

	/*
	 * The cache cluster root needs to share the peer with others. 07/05/2017, Bing Li
	 */
	public MapClusterRoot(Peer<DistributedCacheRootDispatcher> peer)
	{
		super(peer);
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
		this.putNotifier = new ClusterRootUnicastNotifier<CacheValue, PutNotification, PutNotificationCreator>(super.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new PutNotificationCreator());

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
	}

	/*
	 * Unicast the notification to one nearest node from the distributed nodes in the cluster. 05/15/2017, Bing Li
	 */
	public void unicastNotifyNearestly(CacheValue value, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException
	{
		this.putNotifier.notifyNearestly(value, value.getDataKey(), rootBranchCount, subBranchCount);
	}
}
