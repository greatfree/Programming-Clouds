package org.greatfree.framework.cluster.original.cs.twonode.server;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.ClusterServer;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.TerminateSignal;

// Created: 10/23/2018, Bing Li
class ChatServer
{
	private ClusterServer server;

	private ChatServer()
	{
	}
	
	private static ChatServer instance = new ChatServer();
	
	public static ChatServer CSCLUSTER()
	{
		if (instance == null)
		{
			instance = new ChatServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void stopCluster() throws IOException, DistributedNodeFailedException
	{
		this.server.stopCluster();
//		this.stopServer(timeout);
	}

	public void stopServer(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, RemoteIPNotExistedException
	{
		if (!this.server.isChildrenEmpty())
		{
//			TerminateSignal.SIGNAL().waitTermination(timeout);
			TerminateSignal.SIGNAL().notifyAllTermination();
		}
		// Set the terminating signal. 11/25/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();

		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
	}
	
	public void start(String serverName, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.server = new ClusterServer.ServerOnClusterBuilder()
			.peerPort(ServerConfig.COORDINATOR_PORT)
			.peerName(serverName)
			.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
			.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
			.isRegistryNeeded(true)
			.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
			.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
			.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//			.dispatcherThreadPoolSize(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE)
//			.dispatcherThreadKeepAliveTime(RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME)
			.clientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
			.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
			.syncEventerIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
			.syncEventerIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
			.syncEventerMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
			.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
			.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
			.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
			.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
//			.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
			.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
			.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
//			.clientThreadPoolSize(RegistryConfig.CLIENT_THREAD_POOL_SIZE)
//			.clientThreadKeepAliveTime(RegistryConfig.CLIENT_THREAD_KEEP_ALIVE_TIME)
			.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
			.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
			.rootBranchCount(MulticastConfig.ROOT_BRANCH_COUNT)
			.treeBranchCount(MulticastConfig.SUB_BRANCH_COUNT)
			.requestWaitTime(MulticastConfig.BROADCAST_REQUEST_WAIT_TIME)
			.build();
		
//		this.server.start(new ChatServerTask());
		this.server.start(task);
	}
}

