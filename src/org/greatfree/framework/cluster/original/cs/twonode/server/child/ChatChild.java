package org.greatfree.framework.cluster.original.cs.twonode.server.child;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.ClusterChild;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cs.twonode.server.AccountRegistry;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

// Created: 10/23/2018, Bing Li
class ChatChild
{
	private ClusterChild child;
	
	private ChatChild()
	{
	}
	
	private static ChatChild instance = new ChatChild();
	
	public static ChatChild CSCLUSTER()
	{
		if (instance == null)
		{
			instance = new ChatChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();

		// Dispose the account registry. 04/30/2017, Bing Li
		AccountRegistry.CS().dispose();

		this.child.stop(timeout);
	}

	public void start(String rootKey, ChildTask chatTask) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException
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
				.dispatcherThreadPoolSize(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE)
				.dispatcherThreadKeepAliveTime(RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME)
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
//				.clientThreadPoolSize(RegistryConfig.CLIENT_THREAD_POOL_SIZE)
//				.clientThreadKeepAliveTime(RegistryConfig.CLIENT_THREAD_KEEP_ALIVE_TIME)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.treeBranchCount(MulticastConfig.SUB_BRANCH_COUNT)
				.build();
		
		this.child.start(rootKey, chatTask);
	}
}



