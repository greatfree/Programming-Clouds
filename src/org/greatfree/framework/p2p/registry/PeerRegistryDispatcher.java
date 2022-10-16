package org.greatfree.framework.p2p.registry;

import java.util.Calendar;

import org.greatfree.cluster.message.ClusterMessageType;
import org.greatfree.cluster.message.IsRootOnlineRequest;
import org.greatfree.cluster.message.IsRootOnlineResponse;
import org.greatfree.cluster.message.IsRootOnlineStream;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.PeerAddressRequest;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.PeerAddressStream;
import org.greatfree.message.PortRequest;
import org.greatfree.message.PortResponse;
import org.greatfree.message.PortStream;
import org.greatfree.message.RegisterPeerRequest;
import org.greatfree.message.RegisterPeerResponse;
import org.greatfree.message.RegisterPeerStream;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.message.UnregisterPeerRequest;
import org.greatfree.message.UnregisterPeerResponse;
import org.greatfree.message.UnregisterPeerStream;
import org.greatfree.message.multicast.ClusterIPRequest;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.message.multicast.ClusterIPStream;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerDispatcher;

/*
 * 1) Since a peer probably conflicts with others' ports, the registry server needs to assign idle ports to those peers.
 * 
 * 2) Before each peer interacts with each other, they needs to get their IP address from the registry server.
 * 
 * 05/01/2017, Bing Li
 * 
 */

// Created: 05/01/2017, Bing Li
class PeerRegistryDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a request dispatcher to respond peers' registering requests concurrently. 05/01/2017, Bing Li
	private RequestDispatcher<RegisterPeerRequest, RegisterPeerStream, RegisterPeerResponse, RegisterPeerThread, RegisterPeerThreadCreator> registerPeerRequestDispatcher;

	// Declare a request dispatcher to respond a peer's idle port request concurrently. 05/02/2017, Bing Li
	private RequestDispatcher<PortRequest, PortStream, PortResponse, PortRequestThread, PortRequestThreadCreator> portRequestDispatcher;

	// Declare a request dispatcher to respond IP addresses of the cluster requests concurrently. 04/17/2017, Bing Li
	private RequestDispatcher<ClusterIPRequest, ClusterIPStream, ClusterIPResponse, ClusterIPRequestThread, ClusterIPRequestThreadCreator> clusterIPRequestDispatcher;

	private RequestDispatcher<PeerAddressRequest, PeerAddressStream, PeerAddressResponse, PeerAddressRequestThread, PeerAddressRequestThreadCreator> peerIPRequestDispatcher;

	private RequestDispatcher<IsRootOnlineRequest, IsRootOnlineStream, IsRootOnlineResponse, IsRootOnlineRequestThread, IsRootOnlineRequestThreadCreator> isRootOnlineRequestDispatcher;

	// Declare a request dispatcher to respond peers' unregistering requests concurrently. 05/01/2017, Bing Li
	private RequestDispatcher<UnregisterPeerRequest, UnregisterPeerStream, UnregisterPeerResponse, UnregisterPeerThread, UnregisterPeerThreadCreator> unregisterPeerRequestDispatcher;

	/*
	 * The constructor of PeerRegistryDispatcher. 05/01/2017, Bing Li
	 */
//	public PeerRegistryDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public PeerRegistryDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		this.registerPeerRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<RegisterPeerRequest, RegisterPeerStream, RegisterPeerResponse, RegisterPeerThread, RegisterPeerThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new RegisterPeerThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.portRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PortRequest, PortStream, PortResponse, PortRequestThread, PortRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new PortRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.clusterIPRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ClusterIPRequest, ClusterIPStream, ClusterIPResponse, ClusterIPRequestThread, ClusterIPRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ClusterIPRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.peerIPRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PeerAddressRequest, PeerAddressStream, PeerAddressResponse, PeerAddressRequestThread, PeerAddressRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PeerAddressRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.isRootOnlineRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<IsRootOnlineRequest, IsRootOnlineStream, IsRootOnlineResponse, IsRootOnlineRequestThread, IsRootOnlineRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new IsRootOnlineRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.unregisterPeerRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<UnregisterPeerRequest, UnregisterPeerStream, UnregisterPeerResponse, UnregisterPeerThread, UnregisterPeerThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new UnregisterPeerThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
	}

	/*
	 * Shut down the server message dispatcher. 04/15/2017, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.registerPeerRequestDispatcher.dispose();
		this.portRequestDispatcher.dispose();
		this.clusterIPRequestDispatcher.dispose();
		this.peerIPRequestDispatcher.dispose();
		this.isRootOnlineRequestDispatcher.dispose();
		this.unregisterPeerRequestDispatcher.dispose();
	}

	/*
	 * Process the available messages in a concurrent way. 04/17/2017, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 04/17/2017, Bing Li
		switch (message.getMessage().getType())
		{
			case SystemMessageType.REGISTER_PEER_REQUEST:
				System.out.println("REGISTER_PEER_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.registerPeerRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.registerPeerRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.registerPeerRequestDispatcher.enqueue(new RegisterPeerStream(message.getOutStream(), message.getLock(), (RegisterPeerRequest) message.getMessage()));
				break;

			case SystemMessageType.PORT_REQUEST:
				System.out.println("PORT_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the port request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.portRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.portRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.portRequestDispatcher.enqueue(new PortStream(message.getOutStream(), message.getLock(), (PortRequest) message.getMessage()));
				break;

			case SystemMessageType.CLUSTER_IP_REQUEST:
				System.out.println("CLUSTER_IP_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the port request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.clusterIPRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.clusterIPRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.clusterIPRequestDispatcher.enqueue(new ClusterIPStream(message.getOutStream(), message.getLock(), (ClusterIPRequest) message.getMessage()));
				break;
				
			case SystemMessageType.PEER_ADDRESS_REQUEST:
				System.out.println("PEER_ADDRESS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the port request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.peerIPRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.peerIPRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.peerIPRequestDispatcher.enqueue(new PeerAddressStream(message.getOutStream(), message.getLock(), (PeerAddressRequest) message.getMessage()));
				break;
				
			case ClusterMessageType.IS_ROOT_ONLINE_REQUEST:
				System.out.println("IS_ROOT_ONLINE_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the port request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.isRootOnlineRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.isRootOnlineRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.isRootOnlineRequestDispatcher.enqueue(new IsRootOnlineStream(message.getOutStream(), message.getLock(), (IsRootOnlineRequest) message.getMessage()));
				break;
				
			case SystemMessageType.UNREGISTER_PEER_REQUEST:
				System.out.println("UNREGISTER_PEER_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the port request dispatcher is ready. 04/17/2017, Bing Li
				if (!this.unregisterPeerRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.unregisterPeerRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.unregisterPeerRequestDispatcher.enqueue(new UnregisterPeerStream(message.getOutStream(), message.getLock(), (UnregisterPeerRequest) message.getMessage()));
				break;
		}
	}
}
