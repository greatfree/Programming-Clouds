package org.greatfree.dip.old.multicast.root;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.client.FreeClientPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.MulticastConfig;
import org.greatfree.dip.multicast.message.OldRootIPAddressBroadcastNotification;
import org.greatfree.dip.old.multicast.message.root.RootIPAddressBroadcastNotificationCreator;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.ClusterIPRequest;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.multicast.root.abandoned.ClusterRootBroadcastNotifier;
import org.greatfree.server.Peer;
import org.greatfree.server.ServerDispatcher;
import org.greatfree.util.IPAddress;

/*
 * As a cluster root, it needs to append new notifiers and readers. So the abstract class of the cluster root is designed for developers to extend. Only the system level properties are retained in the root. 07/05/2017, Bing Li
 */

// Created: 07/05/2017, Bing Li
public abstract class ClusterRoot<Dispatcher extends ServerDispatcher<ServerMessage>>
{
	// The main node of the cluster. 05/10/2017, Bing Li
	private Peer<Dispatcher> peer;
	// Declare a CS server for management. 05/08/2017, Bing Li
//	private CSServer<RootManDispatcher> manServer;
	
	// Broadcast the root IP address to each child in the cluster. It is required to perform broadcast request/response. So this is a system level broadcast notifier. 05/10/2017, Bing Li
	private ClusterRootBroadcastNotifier<IPAddress, OldRootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationCreator> rootIPBroadcastNotifier;

	/*
	 * As an instance of ClusterRoot, when it is required to share the peer, the constructor is called. 07/05/2017, Bing Li
	 */
	public ClusterRoot(Peer<Dispatcher> peer)
	{
		this.peer = peer;
	}

	/*
	 * As an instance of ClusterRoot, when it is required to has its own peer, the constructor is called. 07/05/2017, Bing Li
	 */
	public ClusterRoot(Dispatcher dispatcher) throws IOException
	{
		// Initialize the peer. 06/13/2017, Bing Li
		this.peer = new Peer.PeerBuilder<Dispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(MulticastConfig.CLUSTER_SERVER_ROOT_NAME)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.listenerThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.listenerThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
				.dispatcher(dispatcher)
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.syncEventerIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.syncEventerIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.syncEventerMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
//				.clientThreadPoolSize(RegistryConfig.CLIENT_THREAD_POOL_SIZE)
//				.clientThreadKeepAliveTime(RegistryConfig.CLIENT_THREAD_KEEP_ALIVE_TIME)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.build();
	}

	/*
	 * Stop the cluster root. 07/05/2017, Bing Li
	 */
	public synchronized void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		// Stop the peer. 07/05/2017, Bing Li
		if (this.peer.isStarted())
		{
			this.peer.stop(timeout);
		}

		// Dispose the root IP address broadcast notifier. 07/05/2017, Bing Li
		this.rootIPBroadcastNotifier.dispose();
	}

	/*
	 * Start the cluster root. 07/05/2017, Bing Li
	 */
	public synchronized void start() throws IOException, ClassNotFoundException, RemoteReadException, InstantiationException, IllegalAccessException, InterruptedException
	{
		// Start up the peer. 06/14/2017, Bing Li
		if (!this.peer.isStarted())
		{
			this.peer.start();
		}

		// Retrieve all of the registered IP addresses of the distributed nodes in the cluster from the registry server. 07/05/2017, Bing Li
		ClusterIPResponse ipResponse = (ClusterIPResponse)this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new ClusterIPRequest());
		if (ipResponse.getIPs() != null)
		{
			// Add the IP addresses to the client pool. 07/05/2017, Bing Li
			for (IPAddress ip : ipResponse.getIPs().values())
			{
				this.peer.getClientPool().addIP(ip.getIP(), ip.getPort());
			}

			// Initialize the cluster root IP address broadcast notifier. 07/05/2017, Bing Li
			this.rootIPBroadcastNotifier = new ClusterRootBroadcastNotifier<IPAddress, OldRootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationCreator>(this.peer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new RootIPAddressBroadcastNotificationCreator());
			// Broadcast the root IP address to each child in the cluster. 07/05/2017, Bing Li
			this.rootIPBroadcastNotifier.notifiy(new IPAddress(this.peer.getPeerID(), this.peer.getPeerIP(), this.peer.getPort()), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT);
		}
		else
		{
			// Initialize the cluster root IP address broadcast notifier. 07/05/2017, Bing Li
			this.rootIPBroadcastNotifier = new ClusterRootBroadcastNotifier<IPAddress, OldRootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationCreator>(this.peer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new RootIPAddressBroadcastNotificationCreator());
		}
	}
	
	/*
	 * Expose the TCP client pool. 07/05/2017, Bing Li
	 */
	public FreeClientPool getClientPool()
	{
		return this.peer.getClientPool();
	}

	/*
	 * The interface is invoked after the method, start(), is invoked. 07/05/2017, Bing Li
	 */
	public abstract void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException;
	
	/*
	 * The interface is invoked after the method, stop(), is invoked. 07/05/2017, Bing Li
	 */
	public abstract void terminate(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException;
}
