package org.greatfree.testing.server;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.util.TerminateSignal;

/*
 * The code is used to test the number updating of threads when notifications are sent periodically. 04/10/2020, Bing Li
 */

// Created: 04/10/2020, Bing Li
class TNServer
{
	private CSServer<TNServerDispatcher> server;

	private TNServer()
	{
	}
	
	private static TNServer instance = new TNServer();
	
	public static TNServer CS()
	{
		if (instance == null)
		{
			instance = new TNServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();
		this.server.stop(timeout);
	}
	
	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.server = new CSServer.CSServerBuilder<TNServerDispatcher>()
				.port(ChatConfig.CHAT_SERVER_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new TNServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		
		this.server.start();
	}
}
