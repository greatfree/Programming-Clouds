package com.greatfree.testing.memory;

import com.greatfree.testing.data.ServerConfig;
import com.greatfree.util.TerminateSignal;

/*
 * This is the entry and exit for the memory server. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StartMemoryServer
{
	public static void main(String[] args)
	{
		// Start the memory server. 11/28/2014, Bing Li
		MemoryServer.STORE().start(ServerConfig.MEMORY_SERVER_PORT);
		// Detect whether the process is shutdown. 11/28/2014, Bing Li
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// Sleep for some time if the process is not shutdown. 11/28/2014, Bing Li
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
