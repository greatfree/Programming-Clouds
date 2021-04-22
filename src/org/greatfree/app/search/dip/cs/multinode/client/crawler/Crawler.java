package org.greatfree.app.search.dip.cs.multinode.client.crawler;

import java.io.IOException;

import org.greatfree.client.CSClient;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.multicast.message.PeerAddressRequest;
import org.greatfree.framework.multicast.message.PeerAddressResponse;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

// Created: 10/08/2018, Bing Li
class Crawler
{
	private CSClient client;
	
	private IPAddress rootAddress;
	
	private Crawler()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static Crawler instance = new Crawler();
	
	public static Crawler FRONT()
	{
		if (instance == null)
		{
			instance = new Crawler();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws IOException, InterruptedException
	{
		SharedThreadPool.SHARED().dispose(ServerConfig.SHARED_THREAD_POOL_SHUTDOWN_TIMEOUT);
		this.client.dispose();
	}
	
	public void init() throws ClassNotFoundException, RemoteReadException, IOException
	{
		this.client = new CSClient.CSClientBuilder()
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.clientIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.clientIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.clientMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.asyncSchedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.build();
		
		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);
		
		this.client.init(SharedThreadPool.SHARED().getPool());
		
		PeerAddressResponse response = (PeerAddressResponse)this.client.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(Tools.getHash(MulticastConfig.CLUSTER_SERVER_ROOT_NAME)));
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
