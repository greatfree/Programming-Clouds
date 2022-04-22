package org.greatfree.framework.old.multicast.child;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastNotification;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastRequest;
import org.greatfree.framework.multicast.message.OldRootIPAddressBroadcastNotification;
import org.greatfree.framework.multicast.message.OldShutdownChildrenBroadcastNotification;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.p2p.message.ChatRegistryRequest;
import org.greatfree.message.ServerMessage;
import org.greatfree.multicast.child.abandoned.ClusterChildBroadcastNotifier;
import org.greatfree.multicast.child.abandoned.ClusterChildBroadcastReader;
import org.greatfree.server.Peer;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

/*
 * The distributed node within one particular cluster. 05/11/2017, Bing Li
 */

// Created: 05/11/2017, Bing Li
class ClusterChildSingleton
{
	// The child peer in the cluster. 05/12/2017, Bing Li
	private Peer<InstanceOfChildDispatcher> childPeer;
	// The management server for the current peer. 05/12/2017, Bing Li
//	private CSServer<ClusterManDispatcher> manServer;

	// The IP address of the cluster root. 06/15/2017, Bing Li
	private IPAddress rootAddress;

	// The root IP address should be broadcast to each node in the cluster before broading requesting/responding can be performed. 05/20/2017, Bing Li
	private ClusterChildBroadcastNotifier<OldRootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationCreator> rootIPBroadcastNotifier;

	// The child broadcastor for the notification of HelloWorldBroadcastNotification. 05/12/2017, Bing Li
//	private ChildBroadcastNotifier<HelloWorldNotification, HelloWorldNotificationCreator> descendantNotifier;
	private ClusterChildBroadcastNotifier<OldHelloWorldBroadcastNotification, HelloWorldBroadcastNotificationCreator> helloWorldBroadcastNotifier;
	
	// The child broadcastor for the request of HelloWorldBroadcastRequest. 05/20/2017, Bing Li
	private ClusterChildBroadcastReader<OldHelloWorldBroadcastRequest, HelloWorldBroadcastRequestCreator> helloWorldBroadcastReader;
	
	// The child broadcastor for the notification of ShutdownChildrenBroadcastNotification. 05/19/2017, Bing Li
	private ClusterChildBroadcastNotifier<OldShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationCreator> shutdownBroadcastNotifier;

	private ClusterChildSingleton()
	{
	}
	
	private static ClusterChildSingleton instance = new ClusterChildSingleton();
	
	public static ClusterChildSingleton CLUSTER()
	{
		if (instance == null)
		{
			instance = new ClusterChildSingleton();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Stop the child root. 05/12/2017, Bing Li
	 */
	public void stop(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();

		// Stop the child peer. 04/30/2017, Bing Li
		this.childPeer.stop(timeout);
		// Dispose the root IP notifier. 05/20/2017, Bing Li
		this.rootIPBroadcastNotifier.dispose();
		// Stop the management server. 04/30/2017, Bing Li
//		this.manServer.stop();
		// Dispose the hello world notifier. 05/15/2017, Bing Li
		this.helloWorldBroadcastNotifier.dispose();
		// Dispose the hello world reader. 05/20/2017, Bing Li
		this.helloWorldBroadcastReader.dispose();
		// Dispose the shutdown notifier. 05/19/2017, Bing Li
		this.shutdownBroadcastNotifier.dispose();
	}

	/*
	 * Start the child peer. 05/12/2017, Bing Li
	 */
	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		// Initialize the child peer. 05/12/2017, Bing Li
		this.childPeer = new Peer.PeerBuilder<InstanceOfChildDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(Tools.generateUniqueKey())
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
				.dispatcher(new InstanceOfChildDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
//				.dispatcher(new InstanceOfChildDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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

		// Start the child peer. 05/12/2017, Bing Li
		this.childPeer.start();
		
		// Updated in XTU. 05/17/2017, Bing Li
		// Get the port for the management server. It is required when multiple peers run on the same node. 05/12/2017, Bing Li
		/*
		PortResponse response = (PortResponse)this.dp.read(ChatConfig.CHAT_REGISTRY_ADDRESS, UtilConfig.PEER_REGISTRY_PORT, new PortRequest(this.dp.getID(), ChatConfig.PEER_ADMIN_PORT_KEY, this.dp.getPeerIP(), ChatConfig.CHAT_ADMIN_PORT));

		// Initialize the chat management server. 04/30/2017, Bing Li
		this.manServer = new CSServer.CSServerBuilder<ClusterManDispatcher>()
				.port(response.getPort())
				.listenerCount(ServerConfig.SINGLE_THREAD_COUNT)
				.listenerThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
				.listenerThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
				.dispatcher(new ClusterManDispatcher(ChatConfig.DISPATCHER_POOL_SIZE, ChatConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ChatConfig.SCHEDULER_POOL_SIZE, ChatConfig.SCHEDULER_KEEP_ALIVE_TIME))
				.build();

		// Start the management server. 04/30/2017, Bing Li
		this.manServer.start();
		*/

		// Initialize the root IP broadcast notifier to forward the root IP address to its children. 06/15/2017, Bing Li
		this.rootIPBroadcastNotifier = new ClusterChildBroadcastNotifier<OldRootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationCreator>(this.childPeer.getLocalIPKey(), this.childPeer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new RootIPAddressBroadcastNotificationCreator());
		
		// Initialize the hello world broadcast notifier. 05/12/2017, Bing Li
//		this.descendantNotifier = new SubBroadcastNotifier<HelloWorldNotification, HelloWorldNotificationCreator>(this.dp.getClientPool(), );
//		this.descendantNotifier = new ClusterChildBroadcastNotifier<HelloWorldNotification, ChildHelloWorldNotificationCreator>(this.dp.this.dp.getClientPool(), ClusterConfig.MULTICASTOR_POOL_SIZE, ClusterConfig.RESOURCE_WAIT_TIME, new ChildHelloWorldNotificationCreator());
		this.helloWorldBroadcastNotifier = new ClusterChildBroadcastNotifier<OldHelloWorldBroadcastNotification, HelloWorldBroadcastNotificationCreator>(this.childPeer.getLocalIPKey(), this.childPeer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new HelloWorldBroadcastNotificationCreator());
		
		// Initialize the hello world broadcast reader. 05/20/2017, Bing Li
		this.helloWorldBroadcastReader = new ClusterChildBroadcastReader<OldHelloWorldBroadcastRequest, HelloWorldBroadcastRequestCreator>(this.childPeer.getLocalIPKey(), this.childPeer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new HelloWorldBroadcastRequestCreator());

		// Initialize the shutdown broadcast notifier. 05/19/2017, Bing Li
		this.shutdownBroadcastNotifier = new ClusterChildBroadcastNotifier<OldShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationCreator>(this.childPeer.getLocalIPKey(), this.childPeer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new ShutdownChildrenBroadcastNotificationCreator());

		// Register the peer to the chatting registry. 06/15/2017, Bing Li
		// Updated in XTU. 05/17/2017, Bing Li
//		this.childPeer.read(ChatConfig.CHAT_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(this.childPeer.getPeerID(), UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING));
		this.childPeer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(this.childPeer.getPeerID()));
	}
	
	/*
	// For testing. 05/19/2017, Bing Li
	public String getChildID()
	{
		return this.dp.getID();
	}
	
	// For testing. 05/19/2017, Bing Li
	public int getPeerPort()
	{
		return this.dp.getPort();
	}
	*/
	
	/*
	 * Keep the root IP address. 05/20/2017, Bing Li
	 */
	public void setRootIP(IPAddress rootAddress)
	{
		this.rootAddress = rootAddress;
//		ClusterConfig.CLUSTER_ROOT_ADDRESS = rootAddress;
	}

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
		return this.childPeer.getPeerIP();
	}

	/*
	 * Expose the child's port. 06/17/2017, Bing Li
	 */
	public int getChildPort()
	{
		return this.childPeer.getPort();
	}

	/*
	 * Respond the response to the root of the cluster. 05/20/2017, Bing Li
	 */
//	public void respondToRoot(HelloWorldBroadcastResponse response) throws IOException
	public void respondToRoot(ServerMessage response) throws IOException, InterruptedException
	{
		this.childPeer.syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), response);
//		this.childPeer.syncNotify(ClusterConfig.CLUSTER_ROOT_ADDRESS.getIP(), ClusterConfig.CLUSTER_ROOT_ADDRESS.getPort(), response);
	}
	
	/*
	 * Broadcast the root IP address notification. 05/19/2017, Bing Li
	 */
	public void broadcastRootIP(OldRootIPAddressBroadcastNotification notification, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.rootIPBroadcastNotifier.notifiy(notification, subBranchCount);
	}

	/*
	 * Broadcast the hello world notification. 05/19/2017, Bing Li
	 */
	public void broadcastNotify(OldHelloWorldBroadcastNotification notification, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.helloWorldBroadcastNotifier.notifiy(notification, subBranchCount);
	}

	/*
	 * Broadcast the shutdown request. 05/20/2017, Bing Li
	 */
	public void broadcastRead(OldHelloWorldBroadcastRequest request, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.helloWorldBroadcastReader.read(request, subBranchCount);
	}

	/*
	 * Broadcast the shutdown notification. 05/19/2017, Bing Li
	 */
	public void broadcastShutdownNotify(OldShutdownChildrenBroadcastNotification notification, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.shutdownBroadcastNotifier.notifiy(notification, subBranchCount);
	}
}
