package org.greatfree.testing.cluster.dn;

import org.greatfree.data.ServerConfig;
import org.greatfree.util.TerminateSignal;

/*
 * This is the unique entry and exit for the DN server. 11/28/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class StartDN
{

	public static void main(String[] args)
	{
		// Start the DN server. 11/28/2014, Bing Li
		System.out.println("DN starting up ...");
		DN.CLUSTER().start(ServerConfig.DN_PORT);
		System.out.println("DN started ...");
		
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
