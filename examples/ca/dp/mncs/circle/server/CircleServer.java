package ca.dp.mncs.circle.server;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.util.TerminateSignal;

import ca.dp.mncs.circle.message.CircleConfig;

// Created: 02/25/2020, Bing Li
class CircleServer
{
	private CSServer<CircleDispatcher> circleServer;
	
	private CircleServer()
	{
	}
	
	private static CircleServer instance = new CircleServer();
	
	public static CircleServer CS()
	{
		if (instance == null)
		{
			instance = new CircleServer();
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
		this.circleServer.stop(timeout);
	}

	public void start() throws ClassNotFoundException, RemoteReadException, IOException
	{
		this.circleServer = new CSServer.CSServerBuilder<CircleDispatcher>()
				.port(CircleConfig.CIRCLE_SERVER_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new CircleDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		this.circleServer.start();
	}
	
	
}
