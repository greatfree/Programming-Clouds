package org.greatfree.dsf.cps.enterprise.server;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.Peer;
import org.greatfree.util.TerminateSignal;

// Created: 04/21/2020, Bing Li
class EnterpriseServer
{
	private Peer<EnterpriseDispatcher> peer;
	
	public EnterpriseServer()
	{
	}
	
	private static EnterpriseServer instance = new EnterpriseServer();
	
	public static EnterpriseServer CPS()
	{
		if (instance == null)
		{
			instance = new EnterpriseServer();
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
	}

	public void start(String username) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder<EnterpriseDispatcher>()
				.peerPort(ServerConfig.COORDINATOR_PORT)
				.peerName(username)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(false)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new EnterpriseDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.build();
		
		this.peer.start();
	}
	
	public void forward(ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, notification);
	}
	
	public ServerMessage query(ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, request);
	}
}
