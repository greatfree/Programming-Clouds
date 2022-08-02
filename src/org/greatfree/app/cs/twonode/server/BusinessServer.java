package org.greatfree.app.cs.twonode.server;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.server.CSServer;
import org.greatfree.util.TerminateSignal;

// Created: 07/25/2018, Bing Li
class BusinessServer
{
	private CSServer<BusinessServerDispatcher> bsServer;
	private CSServer<BusinessManServerDispatcher> bmServer;

	private BusinessServer()
	{
	}
	
	private static BusinessServer instance = new BusinessServer();
	
	public static BusinessServer CS()
	{
		if (instance == null)
		{
			instance = new BusinessServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();

		this.bsServer.stop(timeout);
		this.bmServer.stop(timeout);
	}
	
	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.bsServer = new CSServer.CSServerBuilder<BusinessServerDispatcher>()
				.port(ChatConfig.CHAT_SERVER_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.dispatcher(new BusinessServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(new BusinessServerDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();

		this.bmServer = new CSServer.CSServerBuilder<BusinessManServerDispatcher>()
				.port(ChatConfig.CHAT_ADMIN_PORT)
				.listenerCount(ServerConfig.SINGLE_THREAD_COUNT)
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.dispatcher(new BusinessManServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(new BusinessManServerDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();

		this.bsServer.start();
		this.bmServer.start();
	}
}
