package edu.greatfree.threetier.coordinator;

import java.io.IOException;

import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.server.Peer;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.threetier.admin.AdminConfig;
import edu.greatfree.threetier.message.CoordinatorNotification;
import edu.greatfree.threetier.message.CoordinatorRequest;
import edu.greatfree.threetier.message.CoordinatorResponse;

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
				.peerPort(AdminConfig.COORDINATOR_PORT)
				.peerName(username)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(false)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new CoordinatorDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME))
				.freeClientPoolSize(ClientConfig.CLIENT_POOL_SIZE)
				.readerClientSize(ClientConfig.CLIENT_READER_POOL_SIZE)
				.syncEventerIdleCheckDelay(ClientConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.syncEventerIdleCheckPeriod(ClientConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.syncEventerMaxIdleTime(ClientConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventerWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.asyncEventerWaitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.build();
		
		// Start the peer. 04/30/2017, Bing Li
		this.peer.start();

		// Initialize the chat management server. 04/30/2017, Bing Li
		this.manServer = new CSServer.CSServerBuilder<ManCoordinatorDispatcher>()
				.port(AdminConfig.COORDINATOR_ADMIN_PORT)
				.listenerCount(ServerConfig.SINGLE_THREAD_COUNT)
				.dispatcher(new ManCoordinatorDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME))
				.build();

		// Start the management server. 04/30/2017, Bing Li
		this.manServer.start();
	}

	public void notify(String notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(AdminConfig.TERMINAL_ADDRESS, AdminConfig.TERMINAL_PORT, new CoordinatorNotification(notification));
	}
	
	public CoordinatorResponse query(String query) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (CoordinatorResponse)this.peer.read(AdminConfig.TERMINAL_ADDRESS, AdminConfig.TERMINAL_PORT, new CoordinatorRequest(query));
	}
}
