package org.greatfree.testing.server;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.container.Peer;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 03/05/2023
 *
 */
final class UnaryPeer
{
	private Peer<TNServerDispatcher> peer;
	
	private UnaryPeer()
	{
	}
	
	private static UnaryPeer instance = new UnaryPeer();
	
	public static UnaryPeer CS()
	{
		if (instance == null)
		{
			instance = new UnaryPeer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop() throws ClassNotFoundException, RemoteReadException, InterruptedException, RemoteIPNotExistedException, IOException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
	}
	
//	public void start(String peerName, boolean isServerDisabled, boolean isClientDisabled) throws ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	public void start(String peerName, boolean isServerDisabled) throws ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.peer = new Peer.PeerBuilder<TNServerDispatcher>()
				.peerPort(ServerConfig.COORDINATOR_PORT)
				.peerName(peerName)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
				.dispatcher(new TNServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.syncEventerIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.syncEventerIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.syncEventerMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.isServerDisabled(isServerDisabled)
//				.isClientDisabled(isClientDisabled)
				.build();
		this.peer.start();
	}
	
	public void notify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip, port, notification);
	}

	public ServerMessage read(String ip, int port, ServerMessage request, int timeout) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, IOException
	{
		return this.peer.read(ip, port, request, timeout);
	}
}
