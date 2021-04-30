package edu.chainnet.s3.meta;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.Setup;

/*
 * A 3-tier architecture is establish for the S3. The meta server is the middle tier. Only a single node is implemented with the Peer. 07/09/2020, Bing Li
 */

// Created: 07/09/2020, Bing Li
class StartMetaServer
{
	public static void main(String[] args)
	{
		if (Setup.setupMeta())
		{
			System.out.println("Meta server starting up ...");
			
			try
			{
				MetaServer.META().start(S3Config.META_SERVER_NAME, S3Config.META_SERVER_PORT, new MetaTask(), Setup.S3_CONFIG_XML, Setup.META_CONFIG_XML);
			}
			catch (ClassNotFoundException | IOException | RemoteReadException e)
			{
				e.printStackTrace();
			}

			System.out.println("Meta server started ...");

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
			System.out.println("The meta-server is not set up properly!");
		}
	}

}
