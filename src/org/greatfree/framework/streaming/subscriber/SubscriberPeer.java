package org.greatfree.framework.streaming.subscriber;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.multicast.child.ChildMulticastor;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.p2p.message.ChatRegistryRequest;
import org.greatfree.framework.streaming.StreamConfig;
import org.greatfree.framework.streaming.message.StreamRequest;
import org.greatfree.framework.streaming.message.StreamResponse;
import org.greatfree.framework.streaming.message.SubscribeStreamRequest;
import org.greatfree.framework.streaming.message.SubscribeStreamResponse;
import org.greatfree.framework.streaming.message.UnsubscribeStreamNotification;
import org.greatfree.message.PeerAddressRequest;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.server.Peer;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

// Created: 03/19/2020, Bing Li
class SubscriberPeer
{
	private Peer<SubscriberDispatcher> peer;

	// The address is the one for the selected child in the multicasting framework. 03/20/2020, Bing Li
	private IPAddress pubSubAddress;

	private SubscriberPeer()
	{
	}
	
	private static SubscriberPeer instance = new SubscriberPeer();
	
	public static SubscriberPeer CHILD()
	{
		if (instance == null)
		{
			instance = new SubscriberPeer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		this.peer.stop(timeout);
		ChildMulticastor.CHILD().stop();

		TerminateSignal.SIGNAL().setTerminated();
	}

	public void start(String subscriber) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder<SubscriberDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(Tools.getHash(subscriber))
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new SubscriberDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
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
				.build();

		this.peer.start();

		ChildMulticastor.CHILD().start(this.peer.getLocalIPKey(), this.peer.getClientPool(), MulticastConfig.SUB_BRANCH_COUNT, this.peer.getPool());

		System.out.println("Subscriber-start(): peer ID = " + this.peer.getPeerID());

		this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(this.peer.getPeerID()));

		PeerAddressResponse response = (PeerAddressResponse)this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(Tools.getHash(StreamConfig.PUBSUB_SERVER_NAME)));
		this.pubSubAddress = response.getPeerAddress();
		
		System.out.println("Subscriber-start(): PubSub Address = " + this.pubSubAddress);
	}
	
	public StreamResponse getRegisteredStreams() throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (StreamResponse)this.peer.read(this.pubSubAddress.getIP(), this.pubSubAddress.getPort(), new StreamRequest());
	}

	public SubscribeStreamResponse subscribe(String publisher, String topic) throws ClassNotFoundException, RemoteReadException, IOException
	{
		System.out.println("Subscriber-subscribe(): peer ID = " + this.peer.getPeerID());
		return (SubscribeStreamResponse)this.peer.read(this.pubSubAddress.getIP(), this.pubSubAddress.getPort(), new SubscribeStreamRequest(this.peer.getPeerID(), publisher, topic));
	}
	
	public void unsubscribe(String publisher, String topic) throws IOException, InterruptedException
	{
		this.peer.syncNotify(this.pubSubAddress.getIP(), this.pubSubAddress.getPort(), new UnsubscribeStreamNotification(this.peer.getPeerID(), publisher, topic));
	}
}
