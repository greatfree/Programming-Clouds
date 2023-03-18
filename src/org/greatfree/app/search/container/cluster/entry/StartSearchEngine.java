package org.greatfree.app.search.container.cluster.entry;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.util.TerminateSignal;

// Created: 01/14/2019, Bing Li
class StartSearchEngine
{

	public static void main(String[] args) throws RemoteIPNotExistedException, IOException, ServerPortConflictedException
	{
		System.out.println("Search entry starting up ...");

		try
		{
			SearchEntry.CLUSTER().start(ChatConfig.CHAT_SERVER_PORT, new SearchTask());
		}
		catch (ClassNotFoundException | RemoteReadException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
		}

		System.out.println("Search entry started ...");
		
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
