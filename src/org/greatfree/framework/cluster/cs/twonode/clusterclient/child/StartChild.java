package org.greatfree.framework.cluster.cs.twonode.clusterclient.child;

import java.io.IOException;

import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.util.TerminateSignal;

// Created: 01/15/2019, Bing Li
class StartChild
{

	public static void main(String[] args)
	{
		System.out.println("Chatting child starting up ...");

		try
		{
			ChatChild.CCC().start(MulticastConfig.CLUSTER_CLIENT_ROOT_KEY, new ChatTask());
		}
		catch (ClassNotFoundException | IOException | RemoteReadException | InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
		}
		catch (RemoteIPNotExistedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ServerPortConflictedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Chatting child started ...");

		// After the server is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
		/*
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
		*/
		TerminateSignal.SIGNAL().waitTermination();
	}
}
