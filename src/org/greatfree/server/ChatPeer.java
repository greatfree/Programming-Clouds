package org.greatfree.server;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.dsf.p2p.message.AddPartnerNotification;
import org.greatfree.dsf.p2p.message.ChatNotification;
import org.greatfree.dsf.p2p.message.ChatPartnerRequest;
import org.greatfree.dsf.p2p.message.ChatPartnerResponse;
import org.greatfree.dsf.p2p.message.ChatRegistryRequest;
import org.greatfree.dsf.p2p.message.ChatRegistryResponse;
import org.greatfree.dsf.p2p.peer.ChatMaintainer;
import org.greatfree.dsf.p2p.peer.ChatManServerDispatcher;
import org.greatfree.dsf.p2p.peer.ChatServerDispatcher;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.PortRequest;
import org.greatfree.message.PortResponse;

/*
 * This is an initializable chatting peer. It can send messages to any other arbitrary peers. 05/08/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
public class ChatPeer
{
	// Peer ID. 05/08/2017, Bing Li
	private final String peerID;
	// Declare a peer with the chatting dispatcher. 05/08/2017, Bing Li
	private Peer<ChatServerDispatcher> peer;
	// Declare a CS server for managment. 05/08/2017, Bing Li
	private CSServer<ChatManServerDispatcher> manServer;

	/*
	 * Initialize the chatting peer. 05/08/2017, Bing Li
	 */
	public ChatPeer(String peerID)
	{
		this.peerID = peerID;
	}

	/*
	 * Stop the peer. 05/08/2017, Bing Li
	 */
	public void stop(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		// Stop the peer. 04/30/2017, Bing Li
		this.peer.stop(timeout);
		// Stop the server. 04/30/2017, Bing Li
		this.manServer.stop(timeout);

		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();

		// Shutdown the SharedThreadPool. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().dispose();
	}

	/*
	 * Start the chatting peer. 05/08/2017, Bing Li
	 */
	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		// Initialize the shared thread pool for server listeners. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);
		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Initialize the peer. 05/08/2017, Bing Li
		this.peer = new Peer.PeerBuilder<ChatServerDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(this.peerID)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.listenerThreadPool(SharedThreadPool.SHARED().getPool())
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.schedulerPoolSize(ChatConfig.SCHEDULER_POOL_SIZE)
//				.schedulerKeepAliveTime(ChatConfig.SCHEDULER_KEEP_ALIVE_TIME)
//				.dispatcher(new ChatServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(new ChatServerDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
//				.asyncEventerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.build();

		// Start the chatting peer. 04/30/2017, Bing Li
		this.peer.start();

		// Get the proper port for the chatting peer. It takes effect only in the case multiple peers running on the same node. 05/08/2017, Bing Li
		PortResponse response = (PortResponse)this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new PortRequest(ChatMaintainer.PEER().getLocalUserKey(), RegistryConfig.PEER_ADMIN_PORT_KEY, this.peer.getPeerIP(), ChatConfig.CHAT_ADMIN_PORT));
		
		// Initialize the chat management server. 04/30/2017, Bing Li
		this.manServer = new CSServer.CSServerBuilder<ChatManServerDispatcher>()
				.port(response.getPort())
				.listenerCount(ServerConfig.SINGLE_THREAD_COUNT)
//				.listenerThreadPool(SharedThreadPool.SHARED().getPool())
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.schedulerPoolSize(ChatConfig.SCHEDULER_POOL_SIZE)
//				.schedulerKeepAliveTime(ChatConfig.SCHEDULER_KEEP_ALIVE_TIME)
//				.dispatcher(new ChatManServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(new ChatManServerDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();

		// Start the management server. 04/30/2017, Bing Li
		this.manServer.start();
	}

	/*
	 * Register the chatting peer. 05/08/2017, Bing Li
	 */
	public ChatRegistryResponse registerChat(String localUserKey, String localUserName, String description, String preference) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (ChatRegistryResponse)this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(localUserKey, localUserName, description, preference));
	}

	/*
	 * Retrieve one chatting partner. 05/08/2017, Bing Li
	 */
	public ChatPartnerResponse searchUser(String partnerKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (ChatPartnerResponse)this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatPartnerRequest(partnerKey));
	}

	/*
	 * Add a chatting friend. 05/08/2017, Bing Li
	 */
	public void notifyAddFriend(String ip, int port, String localPeerName, String partnerName)
	{
		this.peer.asyncNotify(ip, port, new AddPartnerNotification(localPeerName, partnerName, "Hello, I want to chat with you!"));
	}

	/*
	 * Send the chatting message to one peer. 05/08/2017, Bing Li
	 */
	public void notifyChat(String ip, int port, String message)
	{
		this.peer.asyncNotify(ip, port, new ChatNotification(message));
	}

	/*
	 * Send the chatting message to one peer. 05/08/2017, Bing Li
	 */
	public void notifyChat(String ip, int port, String message, String localPeerName)
	{
		this.peer.asyncNotify(ip, port, new ChatNotification(message, localPeerName));
	}
}
