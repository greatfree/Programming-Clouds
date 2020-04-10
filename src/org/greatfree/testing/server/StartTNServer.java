package org.greatfree.testing.server;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/*
 * The code is used to test the number updating of threads when notifications are sent periodically. 04/10/2020, Bing Li
 */

// Created: 04/10/2020, Bing Li
class StartTNServer
{

	public static void main(String[] args)
	{
		System.out.println("TNServer starting up ...");
		
		try
		{
			TNServer.CS().start();
		}
		catch (IOException | ClassNotFoundException | RemoteReadException e)
		{
			e.printStackTrace();
		}

		System.out.println("TNServer started ...");

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
