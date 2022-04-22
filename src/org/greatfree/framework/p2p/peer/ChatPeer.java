package org.greatfree.framework.p2p.peer;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.p2p.message.AddPartnerNotification;
import org.greatfree.framework.p2p.message.ChatNotification;
import org.greatfree.framework.p2p.message.ChatPartnerRequest;
import org.greatfree.framework.p2p.message.ChatPartnerResponse;
import org.greatfree.framework.p2p.message.ChatRegistryRequest;
import org.greatfree.framework.p2p.message.ChatRegistryResponse;
import org.greatfree.message.PortRequest;
import org.greatfree.message.PortResponse;
import org.greatfree.server.CSServer;
import org.greatfree.server.Peer;
import org.greatfree.util.TerminateSignal;

/*
 * This is the chatting peer. As an independent distributed node, each peer is able to interact with each other without a centralized polling server. That is, they can send messages instantly. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
class ChatPeer
{
	private Peer<ChatServerDispatcher> chatPeer;
	private CSServer<ChatManServerDispatcher> manServer;
	
	private ChatPeer()
	{
	}
	
	private static ChatPeer instance = new ChatPeer();
	
	public static ChatPeer PEER()
	{
		if (instance == null)
		{
			instance = new ChatPeer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Stop the chatting peer. 05/12/2017, Bing Li
	 */
	public void stop(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();

		// Dispose the account registry. 04/30/2017, Bing Li
//		PeerRegistry.SYSTEM().dispose();
//		AccountRegistry.APPLICATION().dispose();
		
		// Dispose the chatting session resources. 04/30/2017, Bing Li
//		PrivateChatSessions.HUNGARY().dispose();

		// Stop the peer. 04/30/2017, Bing Li
		this.chatPeer.stop(timeout);
		// Stop the management server. 04/30/2017, Bing Li
		this.manServer.stop(timeout);
	
		// The scheduler is defined inside the peer. So it is fine to remove the below line. 05/10/2017, Bing Li
		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();

		// The scheduler is defined inside the peer. So it is fine to remove the below line. 05/10/2017, Bing Li
		// Shutdown the SharedThreadPool. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().dispose();
	}

	/*
	 * Start the chatting peer. 05/12/2017, Bing Li
	 */
	public void start(String username) throws IOException, ClassNotFoundException, RemoteReadException
	{
		// Initialize the private chatting sessions. 04/23/2017, Bing Li
//		PrivateChatSessions.HUNGARY().init();

		// The below line is moved into the peer's constructor. 05/11/2017, Bing Li
		// Initialize the shared thread pool for server listeners. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);

//		System.out.println("0) ChatPeerSingleton-start() ...");

		// The scheduler is defined inside the peer. So it is fine to remove the below line. 05/10/2017, Bing Li
		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		// Initialize the peer. 06/05/2017, Bing Li
		this.chatPeer = new Peer.PeerBuilder<ChatServerDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(username)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.listenerThreadPool(SharedThreadPool.SHARED().getPool())
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.schedulerPoolSize(ChatConfig.SCHEDULER_POOL_SIZE)
//				.schedulerKeepAliveTime(ChatConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.dispatcher(new ChatServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME))
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

//		System.out.println("1) ChatPeerSingleton-start() ...");
		
		// Start the peer. 04/30/2017, Bing Li
		this.chatPeer.start();

//		System.out.println("2) ChatPeerSingleton-start() ...");

		// Get the port for the management server. It is required when multiple peers run on the same node. 05/12/2017, Bing Li
		PortResponse response = (PortResponse)this.chatPeer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new PortRequest(ChatMaintainer.PEER().getLocalUserKey(), RegistryConfig.PEER_ADMIN_PORT_KEY, this.chatPeer.getPeerIP(), ChatConfig.CHAT_ADMIN_PORT));

//		System.out.println("admin port = " + response.getPort());
		
		// Initialize the chat management server. 04/30/2017, Bing Li
		this.manServer = new CSServer.CSServerBuilder<ChatManServerDispatcher>()
//				.port(ChatConfig.CHAT_ADMIN_PORT)
//				.port(this.chatPeer.getAdminPort())
				.port(response.getPort())
				.listenerCount(ServerConfig.SINGLE_THREAD_COUNT)
//				.listenerThreadPool(SharedThreadPool.SHARED().getPool())
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.schedulerPoolSize(ChatConfig.SCHEDULER_POOL_SIZE)
//				.schedulerKeepAliveTime(ChatConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.dispatcher(new ChatManServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
//				.dispatcher(new ChatManServerDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();

		// Start the management server. 04/30/2017, Bing Li
		this.manServer.start();
	}

	/*
	 * Register the chatting user. 06/05/2017, Bing Li
	 */
	public ChatRegistryResponse registerChat(String localUserKey, String localUserName, String description, String preference) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (ChatRegistryResponse)this.chatPeer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(localUserKey, localUserName, description, preference));
	}

	/*
	 * Search one potential chatting partner. 06/05/2017, Bing Li
	 */
	public ChatPartnerResponse searchUser(String userKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (ChatPartnerResponse)this.chatPeer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatPartnerRequest(userKey));
	}

	/*
	 * Notify the potential partner that the local user wants to chat. 06/05/2017, Bing Li
	 */
	public void notifyAddFriend(String ip, int port)
	{
		this.chatPeer.asyncNotify(ip, port, new AddPartnerNotification(ChatMaintainer.PEER().getLocalUsername(), ChatMaintainer.PEER().getPartner(), "Hello, I want to chat with you!"));
	}

	/*
	 * Send the chatting message to the chatting partner. 06/05/2017, Bing Li
	 */
	public void notifyChat(String ip, int port, String message)
	{
		this.chatPeer.asyncNotify(ip, port, new ChatNotification(message, ChatMaintainer.PEER().getLocalUsername()));
	}
}
