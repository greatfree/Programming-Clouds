package org.greatfree.framework.old.multicast.root;

import java.io.IOException;
import java.util.Map;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.multicast.message.HelloWorldAnycastResponse;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.framework.multicast.message.HelloWorldUnicastResponse;
import org.greatfree.framework.multicast.message.OldHelloWorldAnycastNotification;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastNotification;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastRequest;
import org.greatfree.framework.multicast.message.OldHelloWorldUnicastNotification;
import org.greatfree.framework.multicast.message.OldHelloWorldUnicastRequest;
import org.greatfree.framework.multicast.message.OldRootIPAddressBroadcastNotification;
import org.greatfree.framework.multicast.message.OldShutdownChildrenBroadcastNotification;
import org.greatfree.framework.old.multicast.message.root.HelloWorldAnycastNotificationCreator;
import org.greatfree.framework.old.multicast.message.root.HelloWorldAnycastRequestCreator;
import org.greatfree.framework.old.multicast.message.root.HelloWorldBroadcastNotificationCreator;
import org.greatfree.framework.old.multicast.message.root.HelloWorldBroadcastRequestCreator;
import org.greatfree.framework.old.multicast.message.root.HelloWorldUnicastNotificationCreator;
import org.greatfree.framework.old.multicast.message.root.HelloWorldUnicastRequestCreator;
import org.greatfree.framework.old.multicast.message.root.OldHelloWorldAnycastRequest;
import org.greatfree.framework.old.multicast.message.root.RootIPAddressBroadcastNotificationCreator;
import org.greatfree.framework.old.multicast.message.root.ShutdownChildrenBroadcastNotificationCreator;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.multicast.ClusterIPRequest;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.multicast.root.abandoned.ClusterRootAnycastNotifier;
import org.greatfree.multicast.root.abandoned.ClusterRootAnycastReader;
import org.greatfree.multicast.root.abandoned.ClusterRootBroadcastNotifier;
import org.greatfree.multicast.root.abandoned.ClusterRootBroadcastReader;
import org.greatfree.multicast.root.abandoned.ClusterRootUnicastNotifier;
import org.greatfree.multicast.root.abandoned.ClusterRootUnicastReader;
import org.greatfree.server.Peer;
import org.greatfree.util.IPAddress;

/*
 * The class, CSCluster, is defined as a system enclosing multiple or infinite number of distributed instead of just one, like the CSServer or the Peer. All of the composed distributed node forms a single node from a developer's point of view. 05/04/2017, Bing Li
 */

// Created: 05/04/2017, Bing Li
class ClusterRootSingleton
{
	// The main node of the cluster. 05/10/2017, Bing Li
	private Peer<RootDispatcher> peer;
	// Declare a CS server for management. 05/08/2017, Bing Li
//	private CSServer<RootManDispatcher> manServer;
	
	// Broadcast the root IP address to each child in the cluster. It is required to perform broadcast request/response. So this is a system level broadcast notifier. 05/10/2017, Bing Li
	private ClusterRootBroadcastNotifier<IPAddress, OldRootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationCreator> rootIPBroadcastNotifier;
	// One broadcast notifier. 05/10/2017, Bing Li
	private ClusterRootBroadcastNotifier<HelloWorld, OldHelloWorldBroadcastNotification, HelloWorldBroadcastNotificationCreator> helloWorldBroadcastNotifier;
	// One anycast notifier. 05/19/2017, Bing Li
	private ClusterRootAnycastNotifier<HelloWorld, OldHelloWorldAnycastNotification, HelloWorldAnycastNotificationCreator> helloWorldAnycastNotifier;
	// One unicast notifier. 05/19/2017, Bing Li
	private ClusterRootUnicastNotifier<HelloWorld, OldHelloWorldUnicastNotification, HelloWorldUnicastNotificationCreator> helloWorldUnicastNotifier;
	// One broadcast reader. 05/20/2017, Bing Li
	private ClusterRootBroadcastReader<HelloWorld, OldHelloWorldBroadcastRequest, HelloWorldBroadcastResponse, HelloWorldBroadcastRequestCreator> helloWorldBroadcastReader;
	// One anycast reader. 05/21/2017, Bing Li
	private ClusterRootAnycastReader<HelloWorld, OldHelloWorldAnycastRequest, HelloWorldAnycastResponse, HelloWorldAnycastRequestCreator> helloWorldAnycastReader;
	// One unicast reader. 05/21/2017, Bing Li
	private ClusterRootUnicastReader<HelloWorld, OldHelloWorldUnicastRequest, HelloWorldUnicastResponse, HelloWorldUnicastRequestCreator> helloWorldUnicastReader;
	// One broadcast notifier to send shutdown children broadcast notification. 05/19/2017, Bing Li
	private ClusterRootBroadcastNotifier<HelloWorld, OldShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationCreator> shutdownChildrenBroadcastNotifier;
	
	private ClusterRootSingleton()
	{
	}
	
	private static ClusterRootSingleton instance = new ClusterRootSingleton();
	
	public static ClusterRootSingleton CLUSTER()
	{
		if (instance == null)
		{
			instance = new ClusterRootSingleton();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	// The main eventer on the root of the cluster. 05/08/2017, Bing Li
//	private MainEventer eventer;
	
	/*
	 * Initialize the CSCluster. 05/08/2017, Bing Li
	 */
//	public CSCluster(String peerName, String sysRegistryIP, int sysRegistryPort, int listenerCount, ThreadPool listenerThreadPool, Dispatcher dispatcher, int clientPoolSize, int readerClientSize, long syncEventerIdleCheckDelay, long syncEventerIdleCheckPeriod, long syncEventerMaxIdleTime, int asyncEventQueueSize, int asyncEventerSize, long asyncEventingWaitTime, long asyncEventerWaitTime, int asyncEventerWaitRound, long asyncEventIdleCheckDelay, long asyncEventIdleCheckPeriod, int clientThreadPoolSize, long clientThreadKeepAliveTime, int schedulerPoolSize, long scheduleKeepAliveTime, MainEventer eventer) throws IOException
	/*
	public Cluster(String peerName, String sysRegistryIP, int sysRegistryPort, int listenerCount, ThreadPool listenerThreadPool, Dispatcher dispatcher, int clientPoolSize, int readerClientSize, long syncEventerIdleCheckDelay, long syncEventerIdleCheckPeriod, long syncEventerMaxIdleTime, int asyncEventQueueSize, int asyncEventerSize, long asyncEventingWaitTime, long asyncEventerWaitTime, int asyncEventerWaitRound, long asyncEventIdleCheckDelay, long asyncEventIdleCheckPeriod, int clientThreadPoolSize, long clientThreadKeepAliveTime, int schedulerPoolSize, long scheduleKeepAliveTime) throws IOException
	{
		super(peerName, sysRegistryIP, sysRegistryPort, listenerCount, listenerThreadPool, dispatcher, clientPoolSize, readerClientSize, syncEventerIdleCheckDelay, syncEventerIdleCheckPeriod, syncEventerMaxIdleTime, asyncEventQueueSize, asyncEventerSize, asyncEventingWaitTime, asyncEventerWaitTime, asyncEventerWaitRound, asyncEventIdleCheckDelay, asyncEventIdleCheckPeriod, clientThreadPoolSize, clientThreadKeepAliveTime, schedulerPoolSize, scheduleKeepAliveTime);
//		this.eventer = eventer;
	}
	*/
	
	/*
	public ClusterRoot() throws IOException
	{
//		super(builder.getPeerName(), builder.getSysRegistryServerIP(), builder.getSysRegistryServerPort(), builder.getListenerCount(), builder.getListenerThreadPool(), builder.getDispatcher(), builder.getClientPoolSize(), builder.getReaderClientSize(), builder.getSyncEventerIdleCheckDelay(), builder.getSyncEventerIdleCheckPeriod(), builder.getSyncEventerMaxIdleTime(), builder.getAsyncEventQueueSize(), builder.getAsyncEventerSize(), builder.getAsyncEventerWaitTime(), builder.getAsyncEventerWaitTime(), builder.getAsyncEventerWaitRound(), builder.getAsyncEventIdleCheckDelay(), builder.getAsyncEventIdleCheckPeriod(), builder.getClientPoolSize(), builder.getClientThreadKeepAliveTime(), builder.getSchedulerPoolSize(), builder.getSchedulerKeepAliveTime());
		this.peer = new Peer.PeerBuilder<MulticastDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(Tools.generateUniqueKey())
				.sysRegistryServerIP(ChatConfig.CHAT_REGISTRY_ADDRESS)
				.sysRegistryServerPort(UtilConfig.PEER_REGISTRY_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.listenerThreadPool(SharedThreadPool.SHARED().getPool())
				.dispatcher(new MulticastDispatcher(ChatConfig.DISPATCHER_POOL_SIZE, ChatConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME))
				.clientPoolSize(ChatConfig.CLIENT_POOL_SIZE)
				.readerClientSize(ChatConfig.READER_CLIENT_SIZE)
				.syncEventerIdleCheckDelay(ChatConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.syncEventerIdleCheckPeriod(ChatConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.syncEventerMaxIdleTime(ChatConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(ChatConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(ChatConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(ChatConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventerWaitTime(ChatConfig.ASYNC_EVENTER_WAIT_TIME)
				.asyncEventerWaitRound(ChatConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(ChatConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(ChatConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.clientThreadPoolSize(ChatConfig.CLIENT_THREAD_POOL_SIZE)
				.clientThreadKeepAliveTime(ChatConfig.CLIENT_THREAD_KEEP_ALIVE_TIME)
				.schedulerPoolSize(ChatConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ChatConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.build();
	}
	*/

	/*
	 * Stop the CSCluster. 05/08/2017, Bing Li
	 */
	public void stop(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		// Stop the peer. 04/30/2017, Bing Li
//		super.stop();
		this.peer.stop(timeout);
		// Stop the server. 04/30/2017, Bing Li
//		this.manServer.stop();
		
		// Dispose the root IP address broadcast notifier. 05/10/2017, Bing Li
		this.rootIPBroadcastNotifier.dispose();
		
		// Dispose the broadcast notifier. 05/10/2017, Bing Li
		this.helloWorldBroadcastNotifier.dispose();
		
		// Dispose the anycast notifier. 05/10/2017, Bing Li
		this.helloWorldAnycastNotifier.dispose();
		
		// Dispose the unicast notifier. 05/10/2017, Bing Li
		this.helloWorldUnicastNotifier.dispose();
		
		// Dispose the broadcast reader. 05/20/2017, Bing Li
		this.helloWorldBroadcastReader.dispose();
		
		// Dispose the anycast reader. 05/21/2017, Bing Li
		this.helloWorldAnycastReader.dispose();

		// Dispose the unicast reader. 05/21/2017, Bing Li
		this.helloWorldUnicastReader.dispose();

		// Dispose the children shutdown broadcast notifier. 05/19/2017, Bing Li
		this.shutdownChildrenBroadcastNotifier.dispose();

		// Dispose the main cluster eventer. 05/08/2017, Bing Li
//		this.eventer.stop();
		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();
		// Shutdown the SharedThreadPool. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().dispose();
	}

	/*
	 * Start the CSCluster. 05/08/2017, Bing Li
	 */
	public void start() throws ClassNotFoundException, RemoteReadException, IOException, InstantiationException, IllegalAccessException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		// Initialize the shared thread pool for server listeners. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);
		// Initialize the scheduler to do something periodically. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
//		super.start();

		// Initialize the peer. 06/13/2017, Bing Li
		this.peer = new Peer.PeerBuilder<RootDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
//				.peerName(Tools.generateUniqueKey())
				.peerName(MulticastConfig.CLUSTER_SERVER_ROOT_NAME)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
//				.listenerThreadPool(SharedThreadPool.SHARED().getPool())
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.schedulerPoolSize(ChatConfig.SCHEDULER_POOL_SIZE)
//				.schedulerKeepAliveTime(ChatConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.dispatcher(new RootDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
//				.dispatcher(new RootDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.syncEventerIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.syncEventerIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.syncEventerMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
//				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
//				.clientThreadPoolSize(RegistryConfig.CLIENT_THREAD_POOL_SIZE)
//				.clientThreadKeepAliveTime(RegistryConfig.CLIENT_THREAD_KEEP_ALIVE_TIME)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.build();

		// Start up the peer. 06/14/2017, Bing Li
		this.peer.start();

		// Get the proper port for the chatting peer. It takes effect only in the case multiple peers running on the same node. 05/08/2017, Bing Li
//		PortResponse response = (PortResponse)this.peer.read(ChatConfig.CHAT_REGISTRY_ADDRESS, UtilConfig.PEER_REGISTRY_PORT, new PortRequest(this.peer.getID(), ChatConfig.PEER_ADMIN_PORT_KEY, this.peer.getPeerIP(), ChatConfig.CHAT_ADMIN_PORT));

		// Initialize the chat management server. 04/30/2017, Bing Li
		/*
		this.manServer = new CSServer.CSServerBuilder<RootManDispatcher>()
				.port(response.getPort())
				.listenerCount(ServerConfig.SINGLE_THREAD_COUNT)
//				.listenerThreadPool(SharedThreadPool.SHARED().getPool())
				.listenerThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
				.listenerThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.schedulerPoolSize(ChatConfig.SCHEDULER_POOL_SIZE)
//				.schedulerKeepAliveTime(ChatConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.dispatcher(new RootManDispatcher(ChatConfig.DISPATCHER_POOL_SIZE, ChatConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ChatConfig.SCHEDULER_POOL_SIZE, ChatConfig.SCHEDULER_KEEP_ALIVE_TIME))
				.build();

		// Start the management server. 04/30/2017, Bing Li
		this.manServer.start();
		*/
		
		// Retrieve all of the registered IP addresses of the distributed nodes in the cluster from the registry server. 05/08/2017, Bing Li
		ClusterIPResponse ipResponse = (ClusterIPResponse)this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new ClusterIPRequest());
		
		if (ipResponse.getIPs() != null)
		{
			System.out.println("ClusterRootSingleton-ipResponse: ip size = " + ipResponse.getIPs().size());
			
			// Add the IP addresses to the client pool. 05/08/2017, Bing Li
			for (IPAddress ip : ipResponse.getIPs().values())
			{
				System.out.println("Cluster IPs = " + ip.getIP() + ", " + ip.getPort());
//				this.peer.getClientPool().addIP(ip.getPeerKey(), ip.getIP(), ip.getPort());
				this.peer.getClientPool().addIP(ip.getIP(), ip.getPort());
			}
		}

		// Initialize the main cluster eventer. 05/08/2017, Bing Li
//		this.eventer.start(super.getClientPool());
		
//		MainClusterEventer.ROOT().init(this.peer.getClientPool());
		
		// Initialize the cluster root IP address broadcast notifier. 05/08/2017, Bing Li
		this.rootIPBroadcastNotifier = new ClusterRootBroadcastNotifier<IPAddress, OldRootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationCreator>(this.peer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new RootIPAddressBroadcastNotificationCreator());
		// Broadcast the root IP address to each child in the cluster. 05/20/2017, Bing Li
		this.rootIPBroadcastNotifier.notifiy(new IPAddress(this.peer.getPeerID(), this.peer.getPeerName(), this.peer.getPeerIP(), this.peer.getPort()), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT);

		// Initialize the cluster root broadcast notifier. 05/08/2017, Bing Li
		this.helloWorldBroadcastNotifier = new ClusterRootBroadcastNotifier<HelloWorld, OldHelloWorldBroadcastNotification, HelloWorldBroadcastNotificationCreator>(this.peer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new HelloWorldBroadcastNotificationCreator());
		
		// Initialize the cluster root anycast notifier. 05/19/2017, Bing Li
		this.helloWorldAnycastNotifier = new ClusterRootAnycastNotifier<HelloWorld, OldHelloWorldAnycastNotification, HelloWorldAnycastNotificationCreator>(this.peer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new HelloWorldAnycastNotificationCreator());
		
		// Initialize the cluster root unicast notifier. 05/19/2017, Bing Li
		this.helloWorldUnicastNotifier = new ClusterRootUnicastNotifier<HelloWorld, OldHelloWorldUnicastNotification, HelloWorldUnicastNotificationCreator>(this.peer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new HelloWorldUnicastNotificationCreator());
		
		// Initialize the cluster root broadcast reader. 05/20/2017, Bing Li
		this.helloWorldBroadcastReader = new ClusterRootBroadcastReader<HelloWorld, OldHelloWorldBroadcastRequest, HelloWorldBroadcastResponse, HelloWorldBroadcastRequestCreator>(this.peer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, new HelloWorldBroadcastRequestCreator());
		
		// Initialize the cluster root anycast reader. 05/20/2017, Bing Li
		this.helloWorldAnycastReader = new ClusterRootAnycastReader<HelloWorld, OldHelloWorldAnycastRequest, HelloWorldAnycastResponse, HelloWorldAnycastRequestCreator>(this.peer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, new HelloWorldAnycastRequestCreator());
		
		// Initialize the cluster root unicast reader. 05/20/2017, Bing Li
		this.helloWorldUnicastReader = new ClusterRootUnicastReader<HelloWorld, OldHelloWorldUnicastRequest, HelloWorldUnicastResponse, HelloWorldUnicastRequestCreator>(this.peer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, new HelloWorldUnicastRequestCreator());
		
		// Initialize the cluster root shutdown children notifier. 05/19/2017, Bing Li
		this.shutdownChildrenBroadcastNotifier = new ClusterRootBroadcastNotifier<HelloWorld, OldShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationCreator>(this.peer.getClientPool(), MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new ShutdownChildrenBroadcastNotificationCreator());
	}
	
	/*
	 * Broadcast the notification to the distributed nodes in the cluster. 05/15/2017, Bing Li
	 */
	public void broadcastNotify(HelloWorld hl, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.helloWorldBroadcastNotifier.notifiy(hl, rootBranchCount, subBranchCount);
	}

	/*
	 * Anycast the notification to the distributed nodes in the cluster. 05/15/2017, Bing Li
	 */
	public void anycastNotify(HelloWorld hl, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.helloWorldAnycastNotifier.notifiy(hl, rootBranchCount, subBranchCount);
	}

	/*
	 * Unicast the notification to the distributed nodes in the cluster. 05/15/2017, Bing Li
	 */
	public void unicastNotify(HelloWorld hl, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.helloWorldUnicastNotifier.notifiy(hl, rootBranchCount, subBranchCount);
	}
	
	/*
	 * Broadcast the shutdown notification to the distributed nodes in the cluster. 05/15/2017, Bing Li
	 */
	public void broadcastShutdownNotify(int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.shutdownChildrenBroadcastNotifier.notifiy(null, rootBranchCount, subBranchCount);
	}
	
	/*
	 * Broadcast the request to the distributed nodes in the cluster. 05/20/2017, Bing Li
	 */
	public Map<String, HelloWorldBroadcastResponse> broadcastRead(HelloWorld hw, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
//		return this.helloWorldBroadcastReader.read(this.peer.getAddress(), hw, rootBranchCount, subBranchCount);
		return this.helloWorldBroadcastReader.read(hw, rootBranchCount, subBranchCount);
	}

	/*
	 * Collect responses from all of the children in the cluster until timeout. 05/21/2017, Bing Li
	 */
	public void broadcastResponseReceived(HelloWorldBroadcastResponse response)
	{
		this.helloWorldBroadcastReader.notifyResponseReceived(response);
	}

	/*
	 * Anycast the shutdown notification to the distributed nodes in the cluster. 05/15/2017, Bing Li
	 */
	public Map<String, HelloWorldAnycastResponse> anycastRead(HelloWorld hw, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.helloWorldAnycastReader.read(hw, rootBranchCount, subBranchCount);
	}
	
	/*
	 * Collect responses from any of the children in the cluster until time. 05/21/2017, Bing Li
	 */
	public void anycastResponseReceived(HelloWorldAnycastResponse response)
	{
		this.helloWorldAnycastReader.notifyResponseReceived(response);
	}

	/*
	 * Unicast the shutdown notification to the distributed nodes in the cluster. 05/15/2017, Bing Li
	 */
	public Map<String, HelloWorldUnicastResponse> unicastRead(HelloWorld hw, int rootBranchCount, int subBranchCount) throws InstantiationException, IllegalAccessException, InterruptedException, IOException
	{
		return this.helloWorldUnicastReader.read(hw, rootBranchCount, subBranchCount);
	}
	
	/*
	 * Collect responses from the particular one of the children in the cluster until time. 05/21/2017, Bing Li
	 */
	public void unicastResponseReceived(HelloWorldUnicastResponse response)
	{
		this.helloWorldUnicastReader.notifyResponseReceived(response);
	}

	/*
	 * Expose the eventer. 05/08/2017, Bing Li
	 */
	/*
	public MainEventer getEventer()
	{
		return this.eventer;
	}
	*/
	
	/*
	 * Expose the TCP client pool for multicasting eventer and reader. 05/08/2017, Bing Li
	 */
	/*
	public FreeClientPool getClientPool()
	{
		return this.peer.getClientPool();
	}
	*/
}
