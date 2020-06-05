package org.greatfree.dip.cps.enterprise.db;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 04/23/2020, Bing Li
class StartDBServer
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException
	{
		System.out.println("Enterprise DB starting up ...");
		
		EnterpriseDBServer.CPS().start();

		System.out.println("Enterprise DB started ...");

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
