package edu.chainnet.s3.pool.root;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.Setup;

/*
 * This is the root of the pool which is a cluster as well. 09/10/2020, Bing Li
 * 
 * The pool is the computing resources that retain distributed nodes. When the storage cluster cannot afford the pressure of the workload, it sends a request to the pool to get additional nodes. If the storage cluster is idle, the scale of its cluster become smaller. The nodes that leave the cluster join the pool. 09/10/2020, Bing Li
 */

// Created: 09/10/2020, Bing Li
class StartPoolRoot
{
	public static void main(String[] args)
	{
		if (Setup.setupStorage())
		{
			System.out.println("Pool Root starting ...");
			try
			{
				PoolRoot.STORE().start(S3Config.POOL_ROOT_PORT, new PoolRootTask(), Setup.S3_CONFIG_XML);
			}
			catch (ClassNotFoundException | IOException | RemoteReadException | DistributedNodeFailedException e)
			{
				e.printStackTrace();
			}
			System.out.println("Pool Root started ...");

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

}
