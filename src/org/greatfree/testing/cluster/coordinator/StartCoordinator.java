package org.greatfree.testing.cluster.coordinator;

import org.greatfree.data.ServerConfig;
import org.greatfree.util.TerminateSignal;

// Created: 11/19/2016, Bing Li
public class StartCoordinator
{
	public static void main(String[] args)
	{
		// Start the coordinator. 11/30/2016, Bing Li
		System.out.println("Coordinator starting up ...");
		Coordinator.COORDINATOR().start(ServerConfig.SERVER_PORT, ServerConfig.COORDINATOR_DN_PORT, ServerConfig.COORDINATOR_PORT_FOR_ADMIN);
		System.out.println("Coordinator started ...");
		
		// After the coordinator is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// If the terminating flag is false, it is required to sleep for some time. Otherwise, it might cause the high CPU usage. 11/25/2014, Bing Li
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
