package org.greatfree.framework.cs.nio.server;

import java.io.IOException;

import org.greatfree.framework.cs.nio.Config;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.server.nio.CSServer;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
class MyServer
{
	private CSServer<MyDispatcher> server;

	private MyServer()
	{
	}
	
	private static MyServer instance = new MyServer();
	
	public static MyServer CS()
	{
		if (instance == null)
		{
			instance = new MyServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws InterruptedException, IOException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
	}
	
	public void start(int port) throws IOException
	{
		this.server = new CSServer.CSServerBuilder<MyDispatcher>()
				.port(port)
				.bufferSize(Config.BUFFER_SIZE)
				.dispatcher(new MyDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		
		this.server.start();
	}
}
