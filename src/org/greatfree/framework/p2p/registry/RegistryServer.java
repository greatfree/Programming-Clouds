package org.greatfree.framework.p2p.registry;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.server.CSServer;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

/*
 * The server is a CSServer rather than a Peer since it is not necessary to interact with any distributed nodes actively but passively. Chatting peers register to the server for them to access to form the chatting sessions. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
public class RegistryServer
{
	private CSServer<PeerRegistryDispatcher> peerRegistryServer;
	private CSServer<ChatRegistryDispatcher> chatRegistryServer;
	private CSServer<ChatManDispatcher> manServer;

	private RegistryServer()
	{
	}
	
	private static RegistryServer instance = new RegistryServer();
	
	public static RegistryServer PEER()
	{
		if (instance == null)
		{
			instance = new RegistryServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();

		// Dispose the system-level registry. 05/01/2017, Bing Li
		PeerRegistry.SYSTEM().dispose();
		// Dispose the account registry. 04/30/2017, Bing Li
		AccountRegistry.APPLICATION().dispose();

		// Stop the two servers. 04/30/2017, Bing Li
		this.peerRegistryServer.stop(timeout);
		this.chatRegistryServer.stop(timeout);
		this.manServer.stop(timeout);
		
		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();

		// Shutdown the SharedThreadPool. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().dispose();
	}
	
	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		// Initialize the shared thread pool for server listeners. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);
		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		// Initialize the peer registry server. 04/30/2017, Bing Li
		this.peerRegistryServer = new CSServer.CSServerBuilder<PeerRegistryDispatcher>()
				.port(RegistryConfig.PEER_REGISTRY_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.listenerThreadPool(SharedThreadPool.SHARED().getPool())
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.schedulerPoolSize(ServerConfig.SCHEDULER_POOL_SIZE)
//				.schedulerKeepAliveTime(ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.dispatcher(new PeerRegistryDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME))
//				.dispatcher(new PeerRegistryDispatcher(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME))
				.build();
		
		// Initialize the chat registry server. 04/30/2017, Bing Li
		this.chatRegistryServer = new CSServer.CSServerBuilder<ChatRegistryDispatcher>()
				.port(ChatConfig.CHAT_REGISTRY_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.schedulerPoolSize(ChatConfig.SCHEDULER_POOL_SIZE)
//				.schedulerKeepAliveTime(ChatConfig.SCHEDULER_KEEP_ALIVE_TIME)
//				.listenerThreadPool(SharedThreadPool.SHARED().getPool())
				.dispatcher(new ChatRegistryDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
//				.dispatcher(new ChatRegistryDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		
		// Initialize the chat management server. 04/30/2017, Bing Li
		this.manServer = new CSServer.CSServerBuilder<ChatManDispatcher>()
				.port(ChatConfig.CHAT_ADMIN_PORT)
				.listenerCount(ServerConfig.SINGLE_THREAD_COUNT)
//				.listenerThreadPool(SharedThreadPool.SHARED().getPool())
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.schedulerPoolSize(ChatConfig.SCHEDULER_POOL_SIZE)
//				.schedulerKeepAliveTime(ChatConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.dispatcher(new ChatManDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
//				.dispatcher(new ChatManDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();

		// Register the ports to avoid potential conflicts. 05/02/2017, Bing Li
		String localIP = Tools.getLocalIP();
		// Register the main port. 05/02/2017, Bing Li
		PeerRegistry.SYSTEM().register(ChatConfig.CHAT_REGISTRY_SERVER_KEY, ChatConfig.CHAT_REGISTRY_NAME, localIP, RegistryConfig.PEER_REGISTRY_PORT);
		// Register the chat registry port. 05/02/2017, Bing Li
		PeerRegistry.SYSTEM().registeOthers(ChatConfig.CHAT_REGISTRY_SERVER_KEY, ChatConfig.CHAT_REGISTRY_PORT_KEY, localIP, ChatConfig.CHAT_REGISTRY_PORT);
		// Register the registry administration port. 05/02/2017, Bing Li
		PeerRegistry.SYSTEM().registeOthers(ChatConfig.CHAT_REGISTRY_SERVER_KEY, RegistryConfig.REGISTRY_ADMIN_PORT_KEY, localIP, ChatConfig.CHAT_ADMIN_PORT);
		

		// Start up the two servers. 04/30/2017, Bing Li
		this.peerRegistryServer.start();
		this.chatRegistryServer.start();
		this.manServer.start();
	}
}
