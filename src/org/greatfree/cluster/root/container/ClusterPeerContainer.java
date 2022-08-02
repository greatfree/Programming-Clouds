package org.greatfree.cluster.root.container;

import java.io.IOException;
import java.util.List;

import org.greatfree.client.CSClient;
import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.ClusterProfile;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastNotification;
import org.greatfree.message.multicast.MulticastRequest;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.server.container.PeerProfile;
import org.greatfree.server.container.ServerProfile;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.TerminateSignal;

/*
 * Finally, I decide it is not necessary to design a cluster client. The goal to design cluster-based distributed models aims to form a large-scale or infinite-scale system. Now the goal is accomplished with the ClusterPeerContainer. 01/17/2019, Bing Li
 * 
 * This is a cluster as a peer, which has the features of a client as well as a server. I am considering whether it is necessary to design a client which forbids others' accessing. 01/17/2019, Bing Li
 */

// Created: 01/15/2019, Bing Li
public class ClusterPeerContainer
{
	private CSClient client;
	private ClusterServer server;
	private RootTask task;
	
	public ClusterPeerContainer(String rootName, RootTask task) throws IOException
	{
		this.server = new ClusterServer.ServerOnClusterBuilder()
				.peerPort(ServerConfig.COORDINATOR_PORT)
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

		this.client = new CSClient.CSClientBuilder()
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.clientIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.clientIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.clientMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
//				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.asyncSchedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.pool(this.server.getThreadPool())
				.build();

		this.task = task;
	}
	
	public ClusterPeerContainer(String rootName, String configXML, RootTask task) throws IOException
	{
		ClusterProfile.CLUSTER().init(configXML);
		
		String rn = PeerProfile.P2P().getPeerName();
		if (!PeerProfile.P2P().getPeerName().equals(rootName))
		{
			rn = rootName;
		}

		this.client = new CSClient.CSClientBuilder()
				.freeClientPoolSize(PeerProfile.P2P().getFreeClientPoolSize())
				.clientIdleCheckDelay(PeerProfile.P2P().getSyncEventerIdleCheckDelay())
				.clientIdleCheckPeriod(PeerProfile.P2P().getSyncEventerIdleCheckPeriod())
				.clientMaxIdleTime(PeerProfile.P2P().getSyncEventerMaxIdleTime())
				.asyncEventQueueSize(PeerProfile.P2P().getAsyncEventQueueSize())
				.asyncEventerSize(PeerProfile.P2P().getAsyncEventerSize())
				.asyncEventingWaitTime(PeerProfile.P2P().getAsyncEventingWaitTime())
				.asyncEventQueueWaitTime(PeerProfile.P2P().getAsyncEventQueueWaitTime())
//				.asyncEventerWaitRound(PeerProfile.P2P().getAsyncEventerWaitRound())
				.asyncEventIdleCheckDelay(PeerProfile.P2P().getAsyncEventIdleCheckDelay())
				.asyncEventIdleCheckPeriod(PeerProfile.P2P().getAsyncEventIdleCheckPeriod())
				.schedulerPoolSize(ClusterProfile.CLUSTER().getSchedulerPoolSize())
				.schedulerKeepAliveTime(ClusterProfile.CLUSTER().getBroadcastRequestWaitTime())
				.asyncSchedulerShutdownTimeout(ClusterProfile.CLUSTER().getSchedulerShutdownTimeout())
				.readerClientSize(PeerProfile.P2P().getReaderClientSize())
				.build();

		this.server = new ClusterServer.ServerOnClusterBuilder()
				.peerPort(ServerProfile.CS().getPort())
				.peerName(rn)
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
	 * For testing only. 01/16/2019, Bing Li
	 */
	/*
	public CSClient getPeerClient()
	{
		return this.server.getPeerClient();
	}
	*/
	
	/*
	public void stopCluster() throws IOException, DistributedNodeFailedException
	{
		this.server.stopCluster();
	}
	*/
	
	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, DistributedNodeFailedException
	{
		ServerStatus.FREE().setShutdown();
		this.server.stopCluster();
		if (!this.server.isChildrenEmpty())
		{
			TerminateSignal.SIGNAL().waitTermination(timeout);
		}
		this.server.stop(timeout);
		this.client.dispose();
	}

	public void start() throws ClassNotFoundException, IOException, RemoteReadException, DistributedNodeFailedException
	{
//		this.client.init(this.server.getThreadPool());
		this.server.start(this.task);
	}
	
	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		return ClusterRoot.CONTAINER().read(ip, port, request);
		return this.client.read(ip, port, request);
	}

	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
//		ClusterRoot.CONTAINER().syncNotify(ip, port, notification);
		this.client.syncNotify(ip, port, notification);
	}
	
	public void asyncNotify(String ip, int port, ServerMessage notification)
	{
//		ClusterRoot.CONTAINER().asyncNotify(ip, port, notification);
		this.client.asyncNotify(ip, port, notification);
	}
	
	public void broadcastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		ClusterRoot.CONTAINER().broadcastNotify(notification);
	}
	
	public void asyncBroadcastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		ClusterRoot.CONTAINER().asyncBroadcastNotify(notification);
	}
	
	public void anycastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		ClusterRoot.CONTAINER().anycastNotify(notification);
	}
	
	public void asyncAnycastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		ClusterRoot.CONTAINER().asyncAnycastNotify(notification);
	}
	
	public void unicastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		ClusterRoot.CONTAINER().unicastNotify(notification);
	}
	
	public void asyncUnicastNotify(MulticastNotification notification) throws IOException, DistributedNodeFailedException
	{
		ClusterRoot.CONTAINER().asyncUnicastNotify(notification);
	}

	public List<MulticastResponse> broadcastRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return ClusterRoot.CONTAINER().broadcastRead(request);
	}

	public List<MulticastResponse> asyncBroadcastRead(MulticastRequest request) throws DistributedNodeFailedException, IOException
	{
		return ClusterRoot.CONTAINER().asyncBroadcastRead(request);
	}

	public List<MulticastResponse> anycastRead(MulticastRequest request, int n) throws IOException, DistributedNodeFailedException
	{
		return ClusterRoot.CONTAINER().anycastRead(request, n);
	}

	public List<MulticastResponse> asyncAnycastRead(MulticastRequest request, int n) throws IOException, DistributedNodeFailedException
	{
		return ClusterRoot.CONTAINER().asyncAnycastRead(request, n);
	}
	
	public List<MulticastResponse> unicastRead(MulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		return ClusterRoot.CONTAINER().unicastRead(request);
	}
	
	public List<MulticastResponse> asyncUnicastRead(MulticastRequest request) throws IOException, DistributedNodeFailedException
	{
		return ClusterRoot.CONTAINER().asyncUnicastRead(request);
	}
}
