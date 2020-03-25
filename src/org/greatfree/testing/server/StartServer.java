package org.greatfree.testing.server;

import org.greatfree.data.ServerConfig;
import org.greatfree.util.TerminateSignal;

/*
 * The class is the entry to the server process. 08/22/2014, Bing Li
 */

// Created: 07/17/2014, Bing Li
public class StartServer
{
	public static void main(String[] args)
	{
		// Start the server. 08/22/2014, Bing Li
		System.out.println("Server starting up ...");
		MyServer.FREE().start(ServerConfig.SERVER_PORT, ServerConfig.ADMIN_PORT);
		System.out.println("Server started ...");
		
		// After the server is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
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
	}
}