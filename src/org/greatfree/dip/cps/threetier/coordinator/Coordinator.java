package org.greatfree.dip.cps.threetier.coordinator;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.threetier.message.CoordinatorNotification;
import org.greatfree.dip.cps.threetier.message.CoordinatorRequest;
import org.greatfree.dip.cps.threetier.message.CoordinatorResponse;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.server.Peer;
import org.greatfree.util.TerminateSignal;

// Created: 07/06/2018, Bing Li
class Coordinator
{
	private Peer<CoordinatorDispatcher> peer;
	private CSServer<ManCoordinatorDispatcher> manServer;
	
	public Coordinator()
	{
	}
	
	private static Coordinator instance = new Coordinator();
	
	public static Coordinator CPS()
	{
		if (instance == null)
		{
			instance = new Coordinator();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();

		this.peer.stop(timeout);
		this.manServer.stop(timeout);
	}
	
	public void start(String username) throws IOException, ClassNotFoundException, RemoteReadException
	{
		// Initialize the peer. 06/05/2017, Bing Li
		this.peer = new Peer.PeerBuilder<CoordinatorDispatcher>()
				.peerPort(ServerConfig.COORDINATOR_PORT)
				.peerName(username)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(false)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.dispatcher(new CoordinatorDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(new CoordinatorDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
		
		// Start the peer. 04/30/2017, Bing Li
		this.peer.start();

		// For the three-node CPS infrastructure, it is unnecessary to set up a registry server. So the below line can be removed. 07/06/2018, Bing Li
		// Get the port for the management server. It is required when multiple peers run on the same node. 05/12/2017, Bing Li
//		PortResponse response = (PortResponse)this.peer.read(ChatConfig.CHAT_REGISTRY_ADDRESS, UtilConfig.PEER_REGISTRY_PORT, new PortRequest(ChatMaintainer.PEER().getLocalUserKey(), ChatConfig.PEER_ADMIN_PORT_KEY, this.peer.getPeerIP(), ChatConfig.CHAT_ADMIN_PORT));

		// Initialize the chat management server. 04/30/2017, Bing Li
		this.manServer = new CSServer.CSServerBuilder<ManCoordinatorDispatcher>()
				.port(ServerConfig.COORDINATOR_ADMIN_PORT)
				.listenerCount(ServerConfig.SINGLE_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.dispatcher(new ManCoordinatorDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(new ManCoordinatorDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();

		// Start the management server. 04/30/2017, Bing Li
		this.manServer.start();
	}

	public void notify(String notification) throws IOException, InterruptedException
	{
//		System.out.println("Coordinator notify(): " + notification + " is being sent to the terminal ...");
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new CoordinatorNotification(notification));
//		System.out.println("Coordinator notify(): " + notification + " is being sent to the terminal DONE ...");
	}
	
	public CoordinatorResponse query(String query) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (CoordinatorResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new CoordinatorRequest(query));
	}
}
