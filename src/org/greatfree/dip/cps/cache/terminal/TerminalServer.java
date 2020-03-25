package org.greatfree.dip.cps.cache.terminal;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.UtilConfig;

// Created: 07/06/2018, Bing Li
public class TerminalServer
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
		
		MyTerminalList.BACKEND().dispose();
		MyTerminalMap.BACKEND().dispose();
		MySortedTerminalList.BACKEND().dispose();
		MySortedTerminalMapStore.BACKEND().dispose();
		MyTerminalStackStore.BACKEND().dispose();
		MyTerminalQueueStore.BACKEND().dispose();
		MyTimingTerminalMapStore.BACKEND().dispose();
		MyTerminalMapStore.BACKEND().dispose();
		MySortedTerminalListStore.BACKEND().dispose();

		this.server.stop(timeout);
		this.manServer.stop(timeout);
	}
	
	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		// I am not sure whether the below line solves the exception or not, i.e.,  Comparison method violates its general contract!. 08/12/2018, Bing Li
		System.setProperty(UtilConfig.MERGE_SORT, UtilConfig.TRUE);

		this.server = new CSServer.CSServerBuilder<TerminalDispatcher>()
				.port(ServerConfig.TERMINAL_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.dispatcher(new TerminalDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(new TerminalDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		
		this.server.start();
		
		this.manServer = new CSServer.CSServerBuilder<ManTerminalDispatcher>()
				.port(ServerConfig.TERMINAL_ADMIN_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.dispatcher(new ManTerminalDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(new ManTerminalDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		
		this.manServer.start();
		
		MyTerminalList.BACKEND().init();
		MyTerminalMap.BACKEND().init();
		MySortedTerminalList.BACKEND().init();
		MySortedTerminalMapStore.BACKEND().init();
		MyTerminalStackStore.BACKEND().init();
		MyTerminalQueueStore.BACKEND().init();
		MyTimingTerminalMapStore.BACKEND().init();
		MyTerminalMapStore.BACKEND().init();
		MySortedTerminalListStore.BACKEND().init();
	}
}
