package org.greatfree.cluster.child.container;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.root.ClusterProfile;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.MulticastConfig;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.server.container.PeerProfile;
import org.greatfree.server.container.ServerProfile;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

// Created: 01/13/2019, Bing Li
public class ClusterChildContainer
{
	private ClusterChild child;
	private ChildTask task;

	public ClusterChildContainer(String registryServerIP, int registryServerPort, ChildTask task) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException
	{
		this.child = new ClusterChild.ClusterChildBuilder()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(Tools.generateUniqueKey())
				.registryServerIP(registryServerIP)
				.registryServerPort(registryServerPort)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
				.clientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
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
				.rootBranchCount(MulticastConfig.ROOT_BRANCH_COUNT)
				.treeBranchCount(MulticastConfig.SUB_BRANCH_COUNT)
				.requestWaitTime(MulticastConfig.BROADCAST_REQUEST_WAIT_TIME)
				.build();

		this.task = task;
	}

	public ClusterChildContainer(ChildTask task) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException
	{
		this.child = new ClusterChild.ClusterChildBuilder()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(Tools.generateUniqueKey())
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.dispatcherThreadPoolSize(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE)
//				.dispatcherThreadKeepAliveTime(RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME)
				.clientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
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
				.rootBranchCount(MulticastConfig.ROOT_BRANCH_COUNT)
				.treeBranchCount(MulticastConfig.SUB_BRANCH_COUNT)
				.requestWaitTime(MulticastConfig.BROADCAST_REQUEST_WAIT_TIME)
				.build();

		this.task = task;
	}
	
	public ClusterChildContainer(ChildTask task, String configXML) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException
	{
		ClusterProfile.CLUSTER().init(configXML);
		
		this.child = new ClusterChild.ClusterChildBuilder()
				.peerPort(ServerProfile.CS().getPort())
				.peerName(PeerProfile.P2P().getPeerName())
				.registryServerIP(PeerProfile.P2P().getRegistryServerIP())
				.registryServerPort(PeerProfile.P2P().getRegistryServerPort())
				.isRegistryNeeded(PeerProfile.P2P().isRegistryNeeded())
				.listenerCount(ServerProfile.CS().getListeningThreadCount())
				.serverThreadPoolSize(ServerProfile.CS().getServerThreadPoolSize())
				.serverThreadKeepAliveTime(ServerProfile.CS().getServerThreadKeepAliveTime())
				.clientPoolSize(PeerProfile.P2P().getFreeClientPoolSize())
				.readerClientSize(PeerProfile.P2P().getReaderClientSize())
				.syncEventerIdleCheckDelay(PeerProfile.P2P().getSyncEventerIdleCheckDelay())
				.syncEventerIdleCheckPeriod(PeerProfile.P2P().getSyncEventerIdleCheckPeriod())
				.syncEventerMaxIdleTime(PeerProfile.P2P().getSyncEventerMaxIdleTime())
				.asyncEventQueueSize(PeerProfile.P2P().getAsyncEventQueueSize())
				.asyncEventerSize(PeerProfile.P2P().getAsyncEventerSize())
				.asyncEventingWaitTime(PeerProfile.P2P().getAsyncEventingWaitTime())
				.asyncEventerWaitTime(PeerProfile.P2P().getAsyncEventerWaitTime())
				.asyncEventerWaitRound(PeerProfile.P2P().getAsyncEventerWaitRound())
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
	 * The child is enabled to interact with the root through notification synchronously. 09/14/2020, Bing Li
	 */
	public void syncNotifyRoot(Notification notification) throws IOException, InterruptedException
	{
		this.child.syncNotifyRoot(notification);
	}
	
	/*
	 * The child is enabled to interact with the root through notification asynchronously. 09/14/2020, Bing Li
	 */
	public void asyncNotifyRoot(Notification notification)
	{
		this.child.asyncNotifyRoot(notification);
	}
	
	/*
	 * The child is enabled to interact with the root through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readRoot(ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.child.readRoot(request);
	}

	/*
	 * The child is enabled to interact with the collaborator through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readCollaborator(IPAddress ip, ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.child.readCollaborator(ip, request);
	}
	
	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		this.child.stop(timeout);
	}
	
	public void start(String rootKey) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		this.child.start(rootKey, this.task);
	}
	
	public void start() throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		/*
		if (!ServerProfile.CS().isDefault())
		{
			this.child.start(ClusterProfile.CLUSTER().getRootKey(), this.task);
		}
		else
		{
			this.child.start(MulticastConfig.CLUSTER_SERVER_ROOT_KEY, this.task);
		}
		*/
		this.child.start(ClusterProfile.CLUSTER().getRootKey(), this.task);
	}
}
