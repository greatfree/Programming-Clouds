package org.greatfree.dsf.cps.enterprise.db;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.util.TerminateSignal;

// Created: 04/23/2020, Bing Li
class EnterpriseDBServer
{
	private CSServer<DBDispatcher> server;

	public EnterpriseDBServer()
	{
	}
	
	private static EnterpriseDBServer instance = new EnterpriseDBServer();
	
	public static EnterpriseDBServer CPS()
	{
		if (instance == null)
		{
			instance = new EnterpriseDBServer();
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
		this.server = new CSServer.CSServerBuilder<DBDispatcher>()
				.port(ServerConfig.TERMINAL_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new DBDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		
		this.server.start();
	}
}
