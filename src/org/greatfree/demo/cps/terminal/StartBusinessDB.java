package org.greatfree.demo.cps.terminal;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dip.container.cps.threenode.terminal.TerminalServer;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 01/28/2019, Bing Li
class StartBusinessDB
{

	public static void main(String[] args)
	{
		System.out.println("Terminal starting up ...");
		try
		{
			TerminalServer.CPS_CONTAINER().start(ServerConfig.TERMINAL_PORT, new BusinessDBTask());
		}
		catch (ClassNotFoundException | IOException | RemoteReadException e)
		{
			e.printStackTrace();
		}

		System.out.println("Terminal started ...");

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
