package edu.greatfree.container.cps.coordinator;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.greatfree.container.cps.CPSConfig;

// Created: 12/31/2018, Bing Li
class StartCoordinator
{

	public static void main(String[] args)
	{
		System.out.println("Coordinator starting up ...");

		try
		{
			Coordinator.CPS_CONTAINER().start("Coordinator", CPSConfig.COORDINATOR_PORT, new ForwardTask(), false);
		}
		catch (ClassNotFoundException | IOException | RemoteReadException e)
		{
			e.printStackTrace();
		}

		System.out.println("Coordinator started ...");

		// After the server is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// If the terminating flag is false, it is required to sleep for some time. Otherwise, it might cause the high CPU usage. 08/22/2014, Bing Li
				Thread.sleep(CPSConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
