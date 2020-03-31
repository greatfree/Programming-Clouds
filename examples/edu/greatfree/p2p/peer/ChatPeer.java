package edu.greatfree.p2p.peer;

import java.io.IOException;

import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.PortRequest;
import org.greatfree.message.PortResponse;
import org.greatfree.server.CSServer;
import org.greatfree.server.Peer;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.cs.multinode.ChatConfig;
import edu.greatfree.p2p.RegistryConfig;
import edu.greatfree.p2p.message.AddPartnerNotification;
import edu.greatfree.p2p.message.ChatNotification;
import edu.greatfree.p2p.message.ChatPartnerRequest;
import edu.greatfree.p2p.message.ChatPartnerResponse;
import edu.greatfree.p2p.message.ChatRegistryRequest;
import edu.greatfree.p2p.message.ChatRegistryResponse;

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
		TerminateSignal.SIGNAL().setTerminated();

		// Stop the peer. 04/30/2017, Bing Li
		this.chatPeer.stop(timeout);
		// Stop the management server. 04/30/2017, Bing Li
		this.manServer.stop(timeout);
	}

	/*
	 * Start the chatting peer. 05/12/2017, Bing Li
	 */
	public void start(String username) throws IOException, ClassNotFoundException, RemoteReadException
	{
		System.out.println("0) ChatPeerSingleton-start() ...");

		// Initialize the peer. 06/05/2017, Bing Li
		this.chatPeer = new Peer.PeerBuilder<ChatServerDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(username)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new ChatServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME))
				.freeClientPoolSize(ClientConfig.CLIENT_POOL_SIZE)
				.readerClientSize(ClientConfig.CLIENT_READER_POOL_SIZE)
				.syncEventerIdleCheckDelay(ClientConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.syncEventerIdleCheckPeriod(ClientConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.syncEventerMaxIdleTime(ClientConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventerWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
				.asyncEventerWaitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.build();

		System.out.println("1) ChatPeerSingleton-start() ...");
		
		// Start the peer. 04/30/2017, Bing Li
		this.chatPeer.start();

		System.out.println("2) ChatPeerSingleton-start() ...");

		// Get the port for the management server. It is required when multiple peers run on the same node. 05/12/2017, Bing Li
		PortResponse response = (PortResponse)this.chatPeer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new PortRequest(ChatMaintainer.PEER().getLocalUserKey(), RegistryConfig.PEER_ADMIN_PORT_KEY, this.chatPeer.getPeerIP(), ChatConfig.CHAT_ADMIN_PORT));

		// Initialize the chat management server. 04/30/2017, Bing Li
		this.manServer = new CSServer.CSServerBuilder<ChatManServerDispatcher>()
				.port(response.getPort())
				.listenerCount(ServerConfig.SINGLE_THREAD_COUNT)
				.dispatcher(new ChatManServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
