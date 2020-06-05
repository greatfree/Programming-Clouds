package ca.streaming.news.root;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 03/31/2020, Bing Li
class StartNewsRoot
{

	public static void main(String[] args)
	{
		System.out.println("News root starting up ...");

		try
		{
			NewsRootPeer.UNICAST().start();
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException | RemoteReadException | InterruptedException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}

		System.out.println("News root started ...");
		
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
