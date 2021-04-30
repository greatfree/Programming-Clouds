package edu.chainnet.s3.pool.child;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.Setup;
import edu.chainnet.s3.storage.child.StorageChild;
import edu.chainnet.s3.storage.child.StorageChildTask;

/*
 * This is the child of the pool which is a cluster as well. 09/10/2020, Bing Li
 * 
 * The pool is the computing resources that retain distributed nodes. When the storage cluster cannot afford the pressure of the workload, it sends a request to the pool to get additional nodes. If the storage cluster is idle, the scale of its cluster become smaller. The nodes that leave the cluster join the pool. 09/10/2020, Bing Li
 */

// Created: 09/10/2020, Bing Li
class StartPoolChild
{
	public static void main(String[] args)
	{
		if (Setup.setupStorage())
		{
			System.out.println("Pool child starting up ...");
			try
			{
				/*
				 * The task should be identical to that of the child of the pool cluster. 09/10/2020, Bing Li
				 */
				StorageChild.STORE().startWithoutInit(S3Config.POOL_SERVER_KEY, new StorageChildTask(), Setup.S3_CONFIG_XML, Setup.STORAGE_CONFIG_XML);
			}
			catch (ClassNotFoundException | IOException | RemoteReadException | InterruptedException e)
			{
				e.printStackTrace();
			}
			System.out.println("Pool child started ...");

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
			/*
			try
			{
				StorageChild.STORE().dispose();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			*/
		}
		else
		{
			System.out.println("The pool-child is not set up properly!");
		}
	}

}
