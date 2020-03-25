package org.greatfree.dip.old.multicast.child;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.client.FreeClientPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.MulticastConfig;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.dip.p2p.message.ChatRegistryRequest;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.Peer;
import org.greatfree.server.ServerDispatcher;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.UtilConfig;

// Created: 07/08/2017, Bing Li
public abstract class ClusterChild<Dispatcher extends ServerDispatcher<ServerMessage>>
{
	// The child peer in the cluster. 05/12/2017, Bing Li
	private Peer<Dispatcher> peer;

	// The IP address of the cluster root. 06/15/2017, Bing Li
//	private IPAddress rootAddress;

	// The root IP address should be broadcast to each node in the cluster before broading requesting/responding can be performed. 05/20/2017, Bing Li
//	private ClusterChildBroadcastNotifier<RootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationCreator> rootIPBroadcastNotifier;

	/*
	 * As an instance of ClusterRoot, when it is required to share the peer, the constructor is called. 07/05/2017, Bing Li
	 */
	public ClusterChild(Peer<Dispatcher> peer)
	{
		this.peer = peer;
	}
	
	/*
	 * As an instance of ClusterRoot, when it is required to has its own peer, the constructor is called. 07/05/2017, Bing Li
	 */
	public ClusterChild(Dispatcher dispatcher) throws IOException
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
	 * Stop the child root. 05/12/2017, Bing Li
	 */
	public synchronized void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();
		
		ClusterChildSubstrate.CLUSTER().dispose();
		
		// Stop the child peer. 04/30/2017, Bing Li
		this.peer.stop(timeout);
		// Dispose the root IP notifier. 05/20/2017, Bing Li
//		this.rootIPBroadcastNotifier.dispose();
	}

	/*
	 * Start the cluster child. 07/08/2017, Bing Li
	 */
	public synchronized void start() throws ClassNotFoundException, RemoteReadException, IOException
	{
		// Start up the peer. 06/14/2017, Bing Li
		if (!this.peer.isStarted())
		{
			this.peer.start();
		}

		ClusterChildSubstrate.CLUSTER().init(this.peer.getLocalIPKey(), this.peer.getClientPool());
		
		// Initialize the root IP broadcast notifier to forward the root IP address to its children. 06/15/2017, Bing Li
//		this.rootIPBroadcastNotifier = new ClusterChildBroadcastNotifier<RootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationCreator>(this.peer.getLocalIPKey(), this.peer.getClientPool(), ClusterConfig.MULTICASTOR_POOL_SIZE, ClusterConfig.RESOURCE_WAIT_TIME, new RootIPAddressBroadcastNotificationCreator());

		// Register the peer to the chatting registry. 06/15/2017, Bing Li
		this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(this.peer.getPeerID(), UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING));
	}
	
	/*
	 * Keep the root IP address. 05/20/2017, Bing Li
	 */
	/*
	public void setRootIP(IPAddress rootAddress)
	{
		this.rootAddress = rootAddress;
	}
	*/

	/*
	 * Expose the root IP. 05/20/2017, Bing Li
	 */
	/*
	public String getRootIP()
	{
		return this.rootAddress.getIP();
	}
	*/

	/*
	 * Expose the root port. 05/20/2017, Bing Li
	 */
	/*
	public int getRootPort()
	{
		return this.rootAddress.getPort();
	}
	*/

	/*
	 * Expose the child's IP. 06/17/2017, Bing Li
	 */
	public String getChildIP()
	{
		return this.peer.getPeerIP();
	}

	/*
	 * Expose the child's port. 06/17/2017, Bing Li
	 */
	public int getChildPort()
	{
		return this.peer.getPort();
	}
	
	/*
	 * Expose the local IP key of the peer. 07/14/2017, Bing Li
	 */
	public String getLocalIPKey()
	{
		return this.peer.getLocalIPKey();
	}

	/*
	 * Expose the TCP client pool. 07/05/2017, Bing Li
	 */
	public FreeClientPool getClientPool()
	{
		return this.peer.getClientPool();
	}

	/*
	 * Respond the response to the root of the cluster. 05/20/2017, Bing Li
	 */
	public void respondToRoot(ServerMessage response) throws IOException, InterruptedException
	{
//		this.peer.syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), response);
		this.peer.syncNotify(ClusterChildSubstrate.CLUSTER().getRootIP(), ClusterChildSubstrate.CLUSTER().getRootPort(), response);
	}

	/*
	 * Send notifications to somewhere. 07/20/2017, Bing Li
	 */
	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip, port, notification);
	}
	
	/*
	 * Broadcast the root IP address notification. 05/19/2017, Bing Li
	 */
	/*
	public void broadcastRootIP(RootIPAddressBroadcastNotification notification, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.rootIPBroadcastNotifier.notifiy(notification, subBranchCount);
	}
	*/

	/*
	 * The interface is invoked after the method, start(), is invoked. 07/05/2017, Bing Li
	 */
	public abstract void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, RemoteReadException, InterruptedException;
	
	/*
	 * The interface is invoked after the method, stop(), is invoked. 07/05/2017, Bing Li
	 */
	public abstract void terminate(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException;
}
