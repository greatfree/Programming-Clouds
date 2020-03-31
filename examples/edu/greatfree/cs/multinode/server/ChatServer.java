package edu.greatfree.cs.multinode.server;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cs.multinode.server.PrivateChatSessions;
import org.greatfree.dip.cs.twonode.server.AccountRegistry;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.util.TerminateSignal;

/*
 * This is the chatting server that is located at the center of the chatting system. Clients need to poll it periodically to interact with each other instantly. 04/30/2017, Bing Li
 */

// Created: 04/21/2017, Bing Li
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
		PrivateChatSessions.HUNGARY().dispose();
		
		// Stop the two servers. 04/30/2017, Bing Li
		this.chatServer.stop(timeout);
		this.manServer.stop(timeout);
	}

	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		// Initialize the private chatting sessions. 04/23/2017, Bing Li
		PrivateChatSessions.HUNGARY().init();
		
		// Initialize the chat server. 04/30/2017, Bing Li
		this.chatServer = new CSServer.CSServerBuilder<ChatServerDispatcher>()
				.port(ChatConfig.CHAT_SERVER_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new ChatServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME))
				.build();
		
		// Initialize the chat management server. 04/30/2017, Bing Li
		this.manServer = new CSServer.CSServerBuilder<ChatManServerDispatcher>()
				.port(ChatConfig.CHAT_ADMIN_PORT)
				.listenerCount(ServerConfig.SINGLE_THREAD_COUNT)
				.dispatcher(new ChatManServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME))
				.build();

		// Start up the two servers. 04/30/2017, Bing Li
		this.chatServer.start();
		this.manServer.start();
	}
}
