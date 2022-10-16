package org.greatfree.framework.cluster.original.cs.twonode.client;

import java.io.IOException;

import org.greatfree.client.CSClient;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.PeerAddressRequest;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;

// Created: 10/23/2018, Bing Li
class ChatClient
{
	private CSClient client;
	
	private IPAddress rootAddress;

	private ChatClient()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static ChatClient instance = new ChatClient();
	
	public static ChatClient CLUSTER_FRONT()
	{
		if (instance == null)
		{
			instance = new ChatClient();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose() throws IOException, InterruptedException, ClassNotFoundException
	{
		SharedThreadPool.SHARED().dispose(ServerConfig.SHARED_THREAD_POOL_SHUTDOWN_TIMEOUT);
		this.client.dispose();
	}
	
	public void init() throws ClassNotFoundException, RemoteReadException, IOException
	{
		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);
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
				.pool(SharedThreadPool.SHARED().getPool())
				.build();
		
//		this.client.init(SharedThreadPool.SHARED().getPool());
		
		PeerAddressResponse response = (PeerAddressResponse)this.client.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(MulticastConfig.CLUSTER_SERVER_ROOT_KEY));
		this.rootAddress = response.getPeerAddress();
	}

	public void syncNotify(ServerMessage notification) throws IOException, InterruptedException
	{
		this.client.syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public void asyncNotify(ServerMessage notification)
	{
		this.client.asyncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public ServerMessage read(ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.client.read(this.rootAddress.getIP(), this.rootAddress.getPort(), request);
	}
}
