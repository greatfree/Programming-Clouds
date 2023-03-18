package org.greatfree.app.search.multicast.child.storage;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.TerminateSignal;

// Created: 09/28/2018, Bing Li
class StartSearchStorage
{

	public static void main(String[] args) throws IOException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Search storage node starting up ...");

		// Start up the cluster root. 06/11/2017, Bing Li
		try
		{
			StorageNode.STORAGE().start();
		}
		catch (ClassNotFoundException | RemoteReadException e)
		{
			e.printStackTrace();
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
		}

		System.out.println("Search storage node started ...");
		
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
