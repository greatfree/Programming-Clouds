package edu.greatfree.threetier.terminal;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.threetier.admin.AdminConfig;

// Created: 07/06/2018, Bing Li
class TerminalServer
{
	private CSServer<TerminalDispatcher> server;
	private CSServer<ManTerminalDispatcher> manServer;

	private TerminalServer()
	{
	}
	
	private static TerminalServer instance = new TerminalServer();
	
	public static TerminalServer CPS()
	{
		if (instance == null)
		{
			instance = new TerminalServer();
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
		this.manServer.stop(timeout);
	}
	
	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.server = new CSServer.CSServerBuilder<TerminalDispatcher>()
				.port(AdminConfig.TERMINAL_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new TerminalDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		
		this.server.start();
		
		this.manServer = new CSServer.CSServerBuilder<ManTerminalDispatcher>()
				.port(AdminConfig.TERMINAL_ADMIN_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new ManTerminalDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		
		this.manServer.start();
	}
}
