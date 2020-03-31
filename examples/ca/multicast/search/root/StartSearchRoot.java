package ca.multicast.search.root;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 03/14/2020, Bing Li
class StartSearchRoot
{

	public static void main(String[] args)
	{
		System.out.println("Search root starting up ...");

		try
		{
			SearchPeer.ROOT().start();
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException | RemoteReadException | InterruptedException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}

		System.out.println("Search root started ...");
		
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
