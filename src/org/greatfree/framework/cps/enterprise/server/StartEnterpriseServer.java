package org.greatfree.framework.cps.enterprise.server;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cps.enterprise.EnterpriseConfig;
import org.greatfree.util.TerminateSignal;

// Created: 04/23/2020, Bing Li
class StartEnterpriseServer
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException
	{
		System.out.println("Enterprise Server starting up ...");
		
		EnterpriseServer.CPS().start(EnterpriseConfig.ENTERPRISE);

		System.out.println("Enterprise Server started ...");

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
