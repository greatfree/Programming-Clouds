package ca.threetier.ecom.db;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.util.TerminateSignal;

import ca.threetier.ecom.message.ThreeTierConfig;

// Created: 02/22/2020, Bing Li
class BusinessServer
{
	private CSServer<BusinessServerDispatcher> server;

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
		TerminateSignal.SIGNAL().setTerminated();
		this.server.stop(timeout);
	}

	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.server = new CSServer.CSServerBuilder<BusinessServerDispatcher>()
				.port(ThreeTierConfig.BACKEND_TIER_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new BusinessServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		
		this.server.start();
	}
}
