package org.greatfree.framework.cluster.scalable.pool;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cluster.scalable.ScalableConfig;
import org.greatfree.util.TerminateSignal;

// Created: 09/05/2020, Bing Li
class StartPoolRoot
{

	public static void main(String[] args) throws RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Pool root starting up ...");

		try
		{
			PoolRoot.POOL().start(ScalableConfig.POOL_CLUSTER_PORT, new PoolRootTask());
		}
		catch (ClassNotFoundException | IOException | RemoteReadException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
		catch (DuplicatePeerNameException e)
		{
			System.out.println(e);
		}

		System.out.println("Pool root started ...");

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
