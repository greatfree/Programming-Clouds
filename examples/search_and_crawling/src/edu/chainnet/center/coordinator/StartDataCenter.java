package edu.chainnet.center.coordinator;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.center.CenterConfig;

// Created: 04/27/2021, Bing Li
class StartDataCenter
{

	public static void main(String[] args)
	{
		System.out.println("Data Center Coordinator starting up ...");
		
		try
		{
			DataCoordinator.CENTER().start(CenterConfig.CENTER_COORDINATOR_PORT, new CoordinatorTask());
		}
		catch (ClassNotFoundException | IOException | RemoteReadException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}

		System.out.println("Data Center Coordinator started ...");

		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}

