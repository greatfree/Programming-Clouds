package org.greatfree.framework.streaming.broadcast.client;

import java.io.IOException;
import java.util.List;

import org.greatfree.client.CSClient;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.message.PeerAddressRequest;
import org.greatfree.framework.multicast.message.PeerAddressResponse;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.streaming.StreamConfig;
import org.greatfree.framework.streaming.message.SearchRequest;
import org.greatfree.framework.streaming.message.SearchResponse;
import org.greatfree.framework.streaming.message.SubscribersRequest;
import org.greatfree.framework.streaming.message.SubscribersResponse;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

// Created: 03/21/2020, Bing Li
class ServiceAccessor
{
	private CSClient client;
	
	private IPAddress pubSubAddress;
	private IPAddress subscriberAddress;
	
	private ServiceAccessor()
	{
	}

	private static ServiceAccessor instance = new ServiceAccessor();
	
	public static ServiceAccessor BROAD()
	{
		if (instance == null)
		{
			instance = new ServiceAccessor();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws IOException, InterruptedException
	{
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
		
		PeerAddressResponse par = (PeerAddressResponse)this.client.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(Tools.getHash(StreamConfig.PUBSUB_SERVER_NAME)));
		this.pubSubAddress = par.getPeerAddress();
	}

	public void setSubscriberAddress(String publisher, String topic) throws ClassNotFoundException, RemoteReadException, IOException
	{
		SubscribersResponse sr = (SubscribersResponse)this.client.read(this.pubSubAddress.getIP(), this.pubSubAddress.getPort(), new SubscribersRequest(publisher, topic, false));
		PeerAddressResponse par = (PeerAddressResponse)this.client.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(sr.getSelectedSubscriber()));
		this.subscriberAddress = par.getPeerAddress();
	}
	
	public List<String> search(String keyword) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return ((SearchResponse)this.client.read(this.subscriberAddress.getIP(), this.subscriberAddress.getPort(), new SearchRequest(keyword))).getResults();
	}
}
