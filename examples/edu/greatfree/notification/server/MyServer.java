package edu.greatfree.notification.server;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.util.TerminateSignal;

//Created: 04/01/2019, Bing Li
class MyServer
{

	private CSServer<MyServerDispatcher> server;

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

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();
		this.server.stop(timeout);
	}

	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.server = new CSServer.CSServerBuilder<MyServerDispatcher>().port(ChatConfig.CHAT_SERVER_PORT)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new MyServerDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE,
						RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME,
						RegistryConfig.SCHEDULER_THREAD_POOL_SIZE,
						RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();
		
		// This line is missed in the class. 04/07/2019, Bing Li
		this.server.start();
	}

}
