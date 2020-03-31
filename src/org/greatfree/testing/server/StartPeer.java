package org.greatfree.testing.server;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cs.multinode.message.AddPartnerNotification;
import org.greatfree.dip.cs.multinode.message.PollNewSessionsRequest;
import org.greatfree.dip.cs.multinode.message.PollNewSessionsResponse;
import org.greatfree.dip.cs.multinode.server.ChatServerDispatcher;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.Peer;
import org.greatfree.util.NodeID;
import org.greatfree.util.TerminateSignal;

// Created: 05/01/2017, Bing Li
class StartPeer
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException
	{
		// Initialize a command input console for users to interact with the system. 09/21/2014, Bing Li
		Scanner in = new Scanner(System.in);

		System.out.println("Chatting peer starting up ...");

//		Peer<ChatServerDispatcher> peer = new Peer<ChatServerDispatcher>("", ChatConfig.CHAT_SERVER_PORT, ServerConfig.LISTENING_THREAD_COUNT, SharedThreadPool.SHARED().getPool(), new ChatServerDispatcher(ChatConfig.DISPATCHER_POOL_SIZE, ChatConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME), ChatConfig.CLIENT_POOL_SIZE, ClientConfig.SERVER_CLIENT_POOL_SIZE, ChatConfig.CLIENT_IDLE_CHECK_DELAY, ChatConfig.CLIENT_IDLE_CHECK_PERIOD, ChatConfig.CLIENT_MAX_IDLE_TIME, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
//		Peer<ChatServerDispatcher> peer = new Peer<ChatServerDispatcher>("", ChatConfig.CHAT_SERVER_PORT, ServerConfig.LISTENING_THREAD_COUNT, ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, new ChatServerDispatcher(ChatConfig.DISPATCHER_THREAD_POOL_SIZE, ChatConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME), ChatConfig.CLIENT_POOL_SIZE, ClientConfig.CLIENT_READER_POOL_SIZE, ChatConfig.CLIENT_IDLE_CHECK_DELAY, ChatConfig.CLIENT_IDLE_CHECK_PERIOD, ChatConfig.CLIENT_MAX_IDLE_TIME, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		Peer<ChatServerDispatcher> peer = new Peer.PeerBuilder<ChatServerDispatcher>()
				.peerPort(ServerConfig.COORDINATOR_PORT)
				.peerName("")
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
				.dispatcher(new ChatServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
//				.dispatcher(new ChatServerDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
//				.clientThreadPoolSize(RegistryConfig.CLIENT_THREAD_POOL_SIZE)
//				.clientThreadKeepAliveTime(RegistryConfig.CLIENT_THREAD_KEEP_ALIVE_TIME)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.build();
		
		peer.start();
		
		System.out.println("Chatting peer started ...");

		peer.syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new AddPartnerNotification("001", "partnerA", "hello"));
		peer.asyncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new AddPartnerNotification("002", "partnerB", "yes"));
		PollNewSessionsResponse response = (PollNewSessionsResponse)peer.read(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new PollNewSessionsRequest(NodeID.DISTRIBUTED().getKey(), "Bing"));
		System.out.println(response.getNewSessionKeys().size() + " sessions are obtained!");

		in.nextLine();
		
		TerminateSignal.SIGNAL().setTerminated();

		// After the server is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// If the terminating flag is false, it is required to sleep for some time. Otherwise, it might cause the high CPU usage. 08/22/2014, Bing Li
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		peer.stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);

		in.close();
	}

}
