package org.greatfree.app.business.cs.multinode.server;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.server.OldServer;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.TerminateSignal;

/*
 * The class encloses one instance of ServerInstance to implement a singleton server for chatting. 04/17/2016, Bing Li
 */

// Created: 04/17/2017, Bing Li
public class OldChatServer
{
	private OldServer<ChatServerDispatcher, ChatManServerDispatcher> server;

	private OldChatServer()
	{
	}
	
	private static OldChatServer instance = new OldChatServer();
	
	public static OldChatServer CHAT()
	{
		if (instance == null)
		{
			instance = new OldChatServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws IOException, InterruptedException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
	}
	
	public void start(int chatServer, int adminPort, ChatServerDispatcher dispatcher, ChatManServerDispatcher manDispatcher)
	{
		ServerStatus.FREE().addServerID(ChatConfig.CHAT_SERVER_KEY);
		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
//		System.out.println("Chat server id = " + ChatConfig.CHAT_SERVER_ID);
		this.server = new OldServer<ChatServerDispatcher, ChatManServerDispatcher>();
		this.server.start(chatServer, adminPort, dispatcher, manDispatcher);
	}
}
