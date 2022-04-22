package org.greatfree.server.container;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.server.CSServer;

// Created: 12/18/2018, Bing Li
public final class ServerContainer
{
	private CSServer<CSDispatcher> server;
	
	public ServerContainer(int port, ServerTask task) throws IOException
	{
		CSDispatcher csd = new CSDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);
		
		this.server = new CSServer.CSServerBuilder<CSDispatcher>()
				.port(port)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.dispatcher(new CSDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(csd)
				.build();

		// Assign the server key to the message dispatchers in the server dispatcher. 03/30/2020, Bing Li
		csd.init();
		
//		System.out.println("ServerContainer-Constructor(): server ID = " + this.server.getID());
		
		ServiceProvider.CS().init(this.server.getID(), task);
	}
	
//	public ServerContainer(int port, ServerTask task, String configXML) throws IOException
	public ServerContainer(ServerTask task, String configXML) throws IOException
	{
		CSDispatcher csd = new CSDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);

		ServerProfile.CS().init(configXML);
		this.server = new CSServer.CSServerBuilder<CSDispatcher>()
				.port(ServerProfile.CS().getPort())
				.listenerCount(ServerProfile.CS().getListeningThreadCount())
//				.serverThreadPoolSize(ServerProfile.CS().getServerThreadPoolSize())
//				.serverThreadKeepAliveTime(ServerProfile.CS().getServerThreadKeepAliveTime())
				.dispatcher(csd)
				.build();

		// Assign the server key to the message dispatchers in the server dispatcher. 03/30/2020, Bing Li
		csd.init();
		ServiceProvider.CS().init(this.server.getID(), task);
	}
	
	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
//		TerminateSignal.SIGNAL().setTerminated();
//		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
	}
	
	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.server.start();
	}
}

