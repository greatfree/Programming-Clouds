package org.greatfree.framework.streaming.unicast.client;

import java.io.IOException;
import java.util.List;

import org.greatfree.client.CSClient;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.streaming.StreamConfig;
import org.greatfree.framework.streaming.message.SearchRequest;
import org.greatfree.framework.streaming.message.SearchResponse;
import org.greatfree.framework.streaming.message.SubscriberRequest;
import org.greatfree.framework.streaming.message.SubscriberResponse;
import org.greatfree.message.PeerAddressRequest;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

// Created: 03/23/2020, Bing Li
class ServiceAccessor
{
	private CSClient client;
	
	private IPAddress pubSubAddress;
	private IPAddress subscriberAddress;
	
	private ServiceAccessor()
	{
	}

	private static ServiceAccessor instance = new ServiceAccessor();
	
	public static ServiceAccessor UNI()
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
				.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
//				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
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
		SubscriberResponse sr = (SubscriberResponse)this.client.read(this.pubSubAddress.getIP(), this.pubSubAddress.getPort(), new SubscriberRequest(publisher, topic));
		this.subscriberAddress = sr.getIP();
	}
	
	public List<String> search(String keyword) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return ((SearchResponse)this.client.read(this.subscriberAddress.getIP(), this.subscriberAddress.getPort(), new SearchRequest(keyword))).getResults();
	}
}
