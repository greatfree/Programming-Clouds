package org.greatfree.dsf.streaming.broadcast.child;

import java.io.IOException;
import java.util.Map;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.MulticastConfig;
import org.greatfree.dsf.multicast.child.ChildMulticastor;
import org.greatfree.dsf.multicast.message.PeerAddressRequest;
import org.greatfree.dsf.multicast.message.PeerAddressResponse;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.dsf.p2p.message.ChatRegistryRequest;
import org.greatfree.dsf.streaming.StreamConfig;
import org.greatfree.dsf.streaming.message.SubscribersRequest;
import org.greatfree.dsf.streaming.message.SubscribersResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.ClusterIPRequest;
import org.greatfree.message.multicast.ClusterIPResponse;
import org.greatfree.server.Peer;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

// Created: 03/19/2020, Bing Li
class ChildPeer
{
	private Peer<ChildDispatcher> peer;

	private IPAddress rootAddress;
	private IPAddress pubSubAddress;

	private ChildPeer()
	{
	}
	
	private static ChildPeer instance = new ChildPeer();
	
	public static ChildPeer BROADCAST()
	{
		if (instance == null)
		{
			instance = new ChildPeer();
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
		RootMulticastor.CHILD_STREAM().stop();

		TerminateSignal.SIGNAL().setTerminated();
	}

	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder<ChildDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(Tools.generateUniqueKey())
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new ChildDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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

		RootMulticastor.CHILD_STREAM().start(this.peer.getClientPool(), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, this.peer.getPool());

		this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(this.peer.getPeerID()));

		PeerAddressResponse response = (PeerAddressResponse)this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(Tools.getHash(StreamConfig.PUBSUB_SERVER_NAME)));
		this.pubSubAddress = response.getPeerAddress();
		
		System.out.println("Pub/Sub address = " + this.pubSubAddress);
	}
	
	public String getLocalKey()
	{
		return this.peer.getLocalIPKey();
	}

	public void setRootIP(IPAddress rootAddress)
	{
		this.rootAddress = rootAddress;
	}

	public void notifyRoot(ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public Map<String, IPAddress> getSubscriberIPs(String publisher, String topic) throws ClassNotFoundException, RemoteReadException, IOException
	{
		SubscribersResponse sr = (SubscribersResponse)this.peer.read(this.pubSubAddress.getIP(), this.pubSubAddress.getPort(), new SubscribersRequest(publisher, topic));
		if (sr.getSubscribers() != StreamConfig.NO_SUBSCRIBERS)
		{
			for (String entry : sr.getSubscribers())
			{
				System.out.println("ChildPeer-getSubscriberIPs(): subscriber = " + entry);
			}
			return ((ClusterIPResponse)this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new ClusterIPRequest(sr.getSubscribers()))).getIPs();
		}
		return StreamConfig.NO_IPS;
	}
}
