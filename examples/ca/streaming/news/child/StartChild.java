package ca.streaming.news.child;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 04/01/2020, Bing Li
class StartChild
{

	public static void main(String[] args)
	{
		System.out.println("News child starting up ...");

		try
		{
			NewsChildPeer.UNICAST().start();
		}
		catch (ClassNotFoundException | IOException | RemoteReadException e)
		{
			e.printStackTrace();
		}

		System.out.println("News child started ...");
		
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
