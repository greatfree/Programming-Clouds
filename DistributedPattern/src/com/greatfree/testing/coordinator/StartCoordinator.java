package com.greatfree.testing.coordinator;

import com.greatfree.testing.data.ServerConfig;
import com.greatfree.util.TerminateSignal;

/*
 * This is the entry and the exit of the coordinator process. 11/25/2014, Bing Li
 */

// Created: 11/10/2014, Bing Li
public class StartCoordinator
{
	public static void main(String[] args)
	{
		// Start the coordinator. 11/25/2014, Bing Li
		Coordinator.COORDINATOR().start(ServerConfig.COORDINATOR_PORT_FOR_CRAWLER, ServerConfig.COORDINATOR_PORT_FOR_MEMORY, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, ServerConfig.COORDINATOR_PORT_FOR_SEARCH);
		
		// After the coordinator is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 11/25/2014, Bing Li
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
