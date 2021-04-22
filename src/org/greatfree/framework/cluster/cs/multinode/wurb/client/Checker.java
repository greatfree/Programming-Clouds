package org.greatfree.framework.cluster.cs.multinode.wurb.client;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;

// Created: 02/04/2019, Bing Li
class Checker implements Runnable
{

	@Override
	public void run()
	{
		try
		{
//			System.out.println("Checker-run(): starting to checkNewSessions() ...");
			ChatMaintainer.CLUSTER_CONTAINER().checkNewSessions();
//			System.out.println("Checker-run(): checkNewSessions() is done ...");
//			System.out.println("Checker-run(): starting to checkNewChats() ...");
			ChatMaintainer.CLUSTER_CONTAINER().checkNewChats();
//			System.out.println("Checker-run(): checkNewChats() is done ...");
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		
	}

}
