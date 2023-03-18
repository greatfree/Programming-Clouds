package org.greatfree.framework.cluster.scalable.task;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cluster.scalable.ScalableConfig;
import org.greatfree.util.TerminateSignal;

/*
 * The code starts a cluster to accomplish distributed tasks. Once if a simulated heavy workload message is received, it needs to apply for a new child from the pool cluster to increase its computing resources. 09/06/2020, Bing Li
 */

// Created: 09/06/2020, Bing Li
class StartTaskRoot
{

	public static void main(String[] args) throws RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Task root starting up ...");

		try
		{
			TaskRoot.TASK().start(ScalableConfig.POOL_CLUSTER_PORT, new TaskRootTask());
		}
		catch (ClassNotFoundException | IOException | RemoteReadException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
		}

		System.out.println("Task root started ...");

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
