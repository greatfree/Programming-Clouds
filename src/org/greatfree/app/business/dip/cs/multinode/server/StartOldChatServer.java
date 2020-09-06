package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.util.TerminateSignal;

/*
 * The class is the entry to the server process. 08/22/2014, Bing Li
 */

// Created: 04/15/2017, Bing Li
public class StartOldChatServer
{

	public static void main(String[] args)
	{
		// Start the server. 08/22/2014, Bing Li
		System.out.println("Chatting server starting up ...");
//		Server.FREE().start(ChatConfig.CHAT_SERVER_PORT, ChatConfig.CHAT_ADMIN_PORT);

		OldChatServer.CHAT().start(ChatConfig.CHAT_SERVER_PORT, ChatConfig.CHAT_ADMIN_PORT, new ChatServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME), new ChatManServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME));
//		OldChatServer.CHAT().start(ChatConfig.CHAT_SERVER_PORT, ChatConfig.CHAT_ADMIN_PORT, new ChatServerDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME), new ChatManServerDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME));
		
		System.out.println("Chatting server started ...");

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
	}

}
