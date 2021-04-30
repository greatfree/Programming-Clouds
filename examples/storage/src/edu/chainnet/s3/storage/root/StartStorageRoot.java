package edu.chainnet.s3.storage.root;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.Setup;

/*
 * The cluster is used to persist encoded blocks. 07/11/2020, Bing Li
 */

// Created: 07/11/2020, Bing Li
class StartStorageRoot
{
	public static void main(String[] args)
	{
		if (Setup.setupStorage())
		{
			System.out.println("Storage Root starting ...");
			try
			{
				StorageRoot.STORE().start(S3Config.STORAGE_ROOT_PORT, new StorageRootTask(), Setup.S3_CONFIG_XML, Setup.STORAGE_CONFIG_XML);
			}
			catch (ClassNotFoundException | IOException | RemoteReadException | DistributedNodeFailedException e)
			{
				e.printStackTrace();
			}
			System.out.println("Storage Root started ...");

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
		else
		{
			System.out.println("The storage-root is not set up properly!");
		}
	}

}
