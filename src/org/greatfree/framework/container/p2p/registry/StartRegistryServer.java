package org.greatfree.framework.container.p2p.registry;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.TerminateSignal;

// Created: 01/12/2019, Bing Li
class StartRegistryServer
{

	public static void main(String[] args)
	{
		System.out.println("Chatting registry server starting up ...");

		try
		{
			RegisterServer.CS().start(RegistryConfig.PEER_REGISTRY_PORT, new RegistryTask());
		}
		catch (ClassNotFoundException | IOException | RemoteReadException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Chatting registry server started ...");

		// After the server is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
		/*
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// If the terminating flag is false, it is required to sleep for some time. Otherwise, it might cause the high CPU usage. 08/22/2014, Bing Li
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		*/
		TerminateSignal.SIGNAL().waitTermination();
	}

}
