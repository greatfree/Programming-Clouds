package org.greatfree.framework.streaming.broadcast.pubsub;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.streaming.StreamConfig;
import org.greatfree.server.Peer;
import org.greatfree.util.TerminateSignal;

// Created: 03/18/2020, Bing Li
class PubSubServer
{
	private Peer<PubSubDispatcher> peer;

	private PubSubServer()
	{
	}
	
	private static PubSubServer instance = new PubSubServer();
	
	public static PubSubServer BROAD_STREAM()
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
//		TerminateSignal.SIGNAL().setTerminated();
//		PeerRegistry.SYSTEM().dispose();
		TerminateSignal.SIGNAL().notifyAllTermination();

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
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
				.dispatcher(new PubSubDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME))
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
				.build();

		/*
		String localIP = Tools.getLocalIP();
		PeerRegistry.SYSTEM().register(ChatConfig.CHAT_REGISTRY_SERVER_KEY, ChatConfig.CHAT_REGISTRY_NAME, localIP, RegistryConfig.PEER_REGISTRY_PORT);
		PeerRegistry.SYSTEM().registeOthers(ChatConfig.CHAT_REGISTRY_SERVER_KEY, ChatConfig.CHAT_REGISTRY_PORT_KEY, localIP, ChatConfig.CHAT_REGISTRY_PORT);
		PeerRegistry.SYSTEM().registeOthers(ChatConfig.CHAT_REGISTRY_SERVER_KEY, RegistryConfig.REGISTRY_ADMIN_PORT_KEY, localIP, ChatConfig.CHAT_ADMIN_PORT);
		*/

		this.peer.start();
	}
}
