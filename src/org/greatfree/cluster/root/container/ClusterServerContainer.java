package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.ClusterProfile;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.server.container.PeerProfile;
import org.greatfree.server.container.ServerProfile;
import org.greatfree.util.TerminateSignal;

// Created: 01/13/2019, Bing Li
public final class ClusterServerContainer
{
	private ClusterServer server;
	private RootTask task;

	public ClusterServerContainer(int port, String rootName, String registryServerIP, int registryServerPort, int listenerCount, int maxIOCount, RootTask task, int replicas) throws IOException
	{
		ClusterProfile.CLUSTER().init(registryServerIP, registryServerPort, false);

		this.server = new ClusterServer.ServerOnClusterBuilder()
				.peerPort(port)
				.peerName(rootName)
				.registryServerIP(registryServerIP)
				.registryServerPort(registryServerPort)
				.isRegistryNeeded(true)
				.listenerCount(listenerCount)
				.maxIOCount(maxIOCount)
				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
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
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.rootBranchCount(MulticastConfig.ROOT_BRANCH_COUNT)
				.treeBranchCount(MulticastConfig.SUB_BRANCH_COUNT)
				.requestWaitTime(MulticastConfig.BROADCAST_REQUEST_WAIT_TIME)
				.replicas(replicas)
				.build();

		this.task = task;
	}

	public ClusterServerContainer(int port, String rootName, String registryServerIP, int registryServerPort, RootTask task) throws IOException
	{
		ClusterProfile.CLUSTER().init(registryServerIP, registryServerPort, false);

		this.server = new ClusterServer.ServerOnClusterBuilder()
				.peerPort(port)
				.peerName(rootName)
				.registryServerIP(registryServerIP)
				.registryServerPort(registryServerPort)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
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
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.rootBranchCount(MulticastConfig.ROOT_BRANCH_COUNT)
				.treeBranchCount(MulticastConfig.SUB_BRANCH_COUNT)
				.requestWaitTime(MulticastConfig.BROADCAST_REQUEST_WAIT_TIME)
				.build();

		this.task = task;
	}

	public ClusterServerContainer(int port, String rootName, RootTask task) throws IOException
	{
		this.server = new ClusterServer.ServerOnClusterBuilder()
				.peerPort(port)
				.peerName(rootName)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
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
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.rootBranchCount(MulticastConfig.ROOT_BRANCH_COUNT)
				.treeBranchCount(MulticastConfig.SUB_BRANCH_COUNT)
				.requestWaitTime(MulticastConfig.BROADCAST_REQUEST_WAIT_TIME)
				.build();

		this.task = task;
	}

	public ClusterServerContainer(RootTask task, String configXML) throws IOException
	{
		ClusterProfile.CLUSTER().init(configXML);

		this.server = new ClusterServer.ServerOnClusterBuilder()
				.peerPort(ServerProfile.CS().getPort())
				.peerName(PeerProfile.P2P().getPeerName())
				.registryServerIP(PeerProfile.P2P().getRegistryServerIP())
				.registryServerPort(PeerProfile.P2P().getRegistryServerPort())
				.isRegistryNeeded(PeerProfile.P2P().isRegistryNeeded())
				.listenerCount(ServerProfile.CS().getListeningThreadCount())
				.maxIOCount(ServerProfile.CS().getMaxIOCount())
				.serverThreadPoolSize(ServerProfile.CS().getServerThreadPoolSize())
				.serverThreadKeepAliveTime(ServerProfile.CS().getServerThreadKeepAliveTime())
				.freeClientPoolSize(PeerProfile.P2P().getFreeClientPoolSize())
				.readerClientSize(PeerProfile.P2P().getReaderClientSize())
				.syncEventerIdleCheckDelay(PeerProfile.P2P().getSyncEventerIdleCheckDelay())
				.syncEventerIdleCheckPeriod(PeerProfile.P2P().getSyncEventerIdleCheckPeriod())
				.syncEventerMaxIdleTime(PeerProfile.P2P().getSyncEventerMaxIdleTime())
				.asyncEventQueueSize(PeerProfile.P2P().getAsyncEventQueueSize())
				.asyncEventerSize(PeerProfile.P2P().getAsyncEventerSize())
				.asyncEventingWaitTime(PeerProfile.P2P().getAsyncEventingWaitTime())
				.asyncEventQueueWaitTime(PeerProfile.P2P().getAsyncEventQueueWaitTime())
//				.asyncEventerWaitRound(PeerProfile.P2P().getAsyncEventerWaitRound())
				.asyncEventIdleCheckDelay(PeerProfile.P2P().getAsyncEventIdleCheckDelay())
				.asyncEventIdleCheckPeriod(PeerProfile.P2P().getAsyncEventIdleCheckPeriod())
				.schedulerPoolSize(ClusterProfile.CLUSTER().getSchedulerPoolSize())
				.schedulerKeepAliveTime(ClusterProfile.CLUSTER().getBroadcastRequestWaitTime())
				.rootBranchCount(ClusterProfile.CLUSTER().getRootBranchCount())
				.treeBranchCount(ClusterProfile.CLUSTER().getSubBranchCount())
				.requestWaitTime(ClusterProfile.CLUSTER().getBroadcastRequestWaitTime())
				.build();

		this.task = task;
	}

	/*
	 * The constructor implements the feature of partition-based replications. 09/08/2020, Bing Li
	 */
	public ClusterServerContainer(int port, String rootName, String registryIP, int registryPort, RootTask task, int replicas) throws IOException
	{
		this.server = new ClusterServer.ServerOnClusterBuilder()
				.peerPort(port)
				.peerName(rootName)
				.registryServerIP(registryIP)
				.registryServerPort(registryPort)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
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
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.rootBranchCount(MulticastConfig.ROOT_BRANCH_COUNT)
				.treeBranchCount(MulticastConfig.SUB_BRANCH_COUNT)
				.requestWaitTime(MulticastConfig.BROADCAST_REQUEST_WAIT_TIME)
				.replicas(replicas)
				.build();

		this.task = task;
	}

	/*
	public boolean isChildrenEmpty()
	{
		return this.server.isChildrenEmpty();
	}
	*/
	
	public void stopCluster() throws IOException, DistributedNodeFailedException
	{
		this.server.stopCluster();
//		this.server.stop(timeout);
	}
	
	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		if (!this.server.isChildrenEmpty())
		{
			TerminateSignal.SIGNAL().waitTermination(timeout);
		}
		
		// With the updating of notify/wait, the line is not needed. But to keep compatible, just leave it here for some time. 08/25/2021, Bing Li
		// Set the terminating signal. 11/25/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		
		// I attempt to change the SP pattern to replace the while-true loop with the notify/wait structure. 08/25/2021, Bing Li
//		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
	}

	public void start() throws ClassNotFoundException, IOException, RemoteReadException, DistributedNodeFailedException
	{
		this.server.start(this.task);
	}
}
