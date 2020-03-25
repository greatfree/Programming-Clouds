package org.greatfree.dip.cs.twonode.server;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.util.TerminateSignal;

/*
 * The real code of the CSS
 */

// Created: 05/08/2018, Bing Li
class ChatServer
{
	private CSServer<ChatServerDispatcher> chatServer;
	private CSServer<ChatManServerDispatcher> manServer;
	
	private ChatServer()
	{
	}
	
	private static ChatServer instance = new ChatServer();
	
	public static ChatServer CS()
	{
		if (instance == null)
		{
			instance = new ChatServer();
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
		TerminateSignal.SIGNAL().setTerminated();

		// Dispose the account registry. 04/30/2017, Bing Li
		AccountRegistry.CS().dispose();

		// Dispose the chatting session resources. 04/30/2017, Bing Li
//		PrivateChatSessions.HUNGARY().dispose();
		
		// Stop the two servers. 04/30/2017, Bing Li
		this.chatServer.stop(timeout);
		this.manServer.stop(timeout);

		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();

		// Shutdown the SharedThreadPool. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().dispose();
	}

	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		// Initialize the private chatting sessions. 04/23/2017, Bing Li
//		PrivateChatSessions.HUNGARY().init();
		
		// Initialize the shared thread pool for server listeners. 02/27/2016, Bing Li
//		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);
		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

//		this.chatServer = new CSServer<ChatServerDispatcher>(ChatConfig.CHAT_SERVER_PORT, ServerConfig.LISTENING_THREAD_COUNT, SharedThreadPool.SHARED().getPool(), new ChatServerDispatcher(ChatConfig.DISPATCHER_POOL_SIZE, ChatConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME));
		
		// Initialize the chat server. 04/30/2017, Bing Li
		this.chatServer = new CSServer.CSServerBuilder<ChatServerDispatcher>()
				.port(ChatConfig.CHAT_SERVER_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.schedulerPoolSize(ChatConfig.SCHEDULER_POOL_SIZE)
//				.schedulerKeepAliveTime(ChatConfig.SCHEDULER_KEEP_ALIVE_TIME)
//				.listenerThreadPool(SharedThreadPool.SHARED().getPool())
				.dispatcher(new ChatServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
//				.dispatcher(new ChatServerDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		
//		this.manServer = new CSServer<ChatManServerDispatcher>(ChatConfig.CHAT_ADMIN_PORT, ServerConfig.SINGLE_THREAD_COUNT, SharedThreadPool.SHARED().getPool(), new ChatManServerDispatcher(ChatConfig.DISPATCHER_POOL_SIZE, ChatConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME));
		
		// Initialize the chat management server. 04/30/2017, Bing Li
		this.manServer = new CSServer.CSServerBuilder<ChatManServerDispatcher>()
				.port(ChatConfig.CHAT_ADMIN_PORT)
				.listenerCount(ServerConfig.SINGLE_THREAD_COUNT)
//				.listenerThreadPool(SharedThreadPool.SHARED().getPool())
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.schedulerPoolSize(ChatConfig.SCHEDULER_POOL_SIZE)
//				.schedulerKeepAliveTime(ChatConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.dispatcher(new ChatManServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
//				.dispatcher(new ChatManServerDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();

		// Start up the two servers. 04/30/2017, Bing Li
		this.chatServer.start();
		this.manServer.start();
	}
}
