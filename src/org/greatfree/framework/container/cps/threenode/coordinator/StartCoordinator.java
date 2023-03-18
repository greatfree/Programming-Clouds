package org.greatfree.framework.container.cps.threenode.coordinator;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.util.TerminateSignal;

// Created: 12/31/2018, Bing Li
public class StartCoordinator
{

	public static void main(String[] args) throws RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Coordinator starting up ...");

		try
		{
			Coordinator.CPS_CONTAINER().start("Coordinator", RegistryConfig.PEER_REGISTRY_PORT, new ForwardTask(), false);
		}
		catch (ClassNotFoundException | IOException | RemoteReadException e)
		{
			e.printStackTrace();
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
		}

		System.out.println("Coordinator started ...");

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
