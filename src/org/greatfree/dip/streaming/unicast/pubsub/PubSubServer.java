package org.greatfree.dip.streaming.unicast.pubsub;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.MulticastConfig;
import org.greatfree.dip.multicast.message.PeerAddressRequest;
import org.greatfree.dip.multicast.message.PeerAddressResponse;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.dip.streaming.StreamConfig;
import org.greatfree.dip.streaming.message.SubscribeNotification;
import org.greatfree.dip.streaming.message.UnsubscribeNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.Peer;
import org.greatfree.server.PeerRegistry;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

// Created: 03/22/2020, Bing Li
class PubSubServer
{
	private Peer<PubSubDispatcher> peer;
	
	private IPAddress rootAddress;

	private PubSubServer()
	{
	}
	
	private static PubSubServer instance = new PubSubServer();
	
	public static PubSubServer UNI_STREAM()
	{
		if (instance == null)
		{
			instance = new PubSubServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();
		PeerRegistry.SYSTEM().dispose();

		this.peer.stop(timeout);
	}

	public void start(String username) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder<PubSubDispatcher>()
				.peerPort(StreamConfig.PUBSUB_SERVER_PORT)
				.peerName(username)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new PubSubDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME))
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

		// Because of the root address is required for the PubSubServer, it is required to run the PubSubServer the multicasting framework is started. 03/23/2020, Bing Li
		this.rootAddress = this.getAddress(Tools.getHash(MulticastConfig.CLUSTER_SERVER_ROOT_NAME));
	}
	
	public void subscribe(SubscribeNotification notification) throws IOException, InterruptedException
	{
		this.peer.asyncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public void unsubscribe(UnsubscribeNotification notification) throws IOException, InterruptedException
	{
		this.peer.asyncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
	
	public IPAddress getAddress(String nodeKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return ((PeerAddressResponse)this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS,  RegistryConfig.PEER_REGISTRY_PORT, new PeerAddressRequest(nodeKey))).getPeerAddress();
	}
}
