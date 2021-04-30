package edu.chainnet.s3.storage.child;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.Setup;

/*
 * The cluster is used to persist encoded blocks. 07/11/2020, Bing Li
 */

// Created: 07/11/2020, Bing Li
class StartStorageChild
{
	public static void main(String[] args)
	{
		if (Setup.setupStorage())
		{
			System.out.println("Storage child starting up ...");
			try
			{
				StorageChild.STORE().start(S3Config.STORAGE_SERVER_KEY, new StorageChildTask(), Setup.S3_CONFIG_XML, Setup.STORAGE_CONFIG_XML);
			}
			catch (ClassNotFoundException | IOException | RemoteReadException | InterruptedException e)
			{
				e.printStackTrace();
			}
			System.out.println("Storage child started ...");

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
			System.out.println("The storage-child is not set up properly!");
		}
	}

}
