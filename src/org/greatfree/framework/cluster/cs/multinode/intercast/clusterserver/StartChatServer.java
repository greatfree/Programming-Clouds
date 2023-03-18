package org.greatfree.framework.cluster.cs.multinode.intercast.clusterserver;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cluster.cs.twonode.clusterserver.ChatServer;
import org.greatfree.framework.cluster.cs.twonode.clusterserver.ChatServerTask;
import org.greatfree.util.TerminateSignal;

// Created: 02/26/2019, Bing Li
class StartChatServer
{

	public static void main(String[] args) throws RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Chatting server starting up ...");

		try
		{
			ChatServer.CONTAINER().start(ServerConfig.COORDINATOR_PORT, new ChatServerTask());
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
		}
		catch (ClassNotFoundException | IOException | RemoteReadException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}

		System.out.println("Chatting server started ...");

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
