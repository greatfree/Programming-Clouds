package org.greatfree.testing.server;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 03/30/2020, Bing Li
class StartDoubleServers
{

	public static void main(String[] args)
	{
		System.out.println("D_Server starting up ...");

		try
		{
			DoubleServers.CS().start(ServerConfig.PORT_1, ServerConfig.PORT_2, new ServerTask1(), new ServerTask2());
		}
		catch (ClassNotFoundException | IOException | RemoteReadException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("D_Server started ...");

		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				Thread.sleep(ServerConfig.SLEEP_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
