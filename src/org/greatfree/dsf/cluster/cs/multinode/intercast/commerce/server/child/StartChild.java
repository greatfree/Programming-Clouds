package org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.server.child;

import org.greatfree.data.ServerConfig;
import org.greatfree.util.TerminateSignal;

// Created: 07/18/2019, Bing Li
class StartChild
{

	public static void main(String[] args)
	{
		System.out.println("Commerce child starting up ...");

		
		
		System.out.println("Commerce child started ...");

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
