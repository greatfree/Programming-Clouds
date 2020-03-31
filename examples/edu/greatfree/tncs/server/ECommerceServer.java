package edu.greatfree.tncs.server;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.util.TerminateSignal;

// Created: 05/01/2019, Bing Li
class ECommerceServer
{
	private CSServer<ECommerceDispatcher> server;
	
	private ECommerceServer()
	{
	}
	
	private static ECommerceServer instance = new ECommerceServer();
	
	public static ECommerceServer CS()
	{
		if (instance == null)
		{
			instance = new ECommerceServer();
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
		this.server = new CSServer.CSServerBuilder<ECommerceDispatcher>()
				.port(ServerConfig.ECOMMERCE_SERVER_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new ECommerceDispatcher(ServerConfig.DISPATCHER_THREAD_POOL_SIZE, ServerConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, ServerConfig.SCHEDULER_THREAD_POOL_SIZE, ServerConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		
		this.server.start();
	}
}
