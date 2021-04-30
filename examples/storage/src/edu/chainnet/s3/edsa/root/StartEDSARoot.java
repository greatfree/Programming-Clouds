package edu.chainnet.s3.edsa.root;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.s3.S3Config;
import edu.chainnet.s3.Setup;

/*
 * A 3-tier architecture is establish for the S3. The EDSA server is the backend tier, which is implemented with a cluster. This program is the root of the cluster. The EDSA server focuses on data encoding/decoding. 07/09/2020, Bing Li
 */

// Created: 07/09/2020, Bing Li
class StartEDSARoot
{
	public static void main(String[] args)
	{
		if (Setup.setupS3())
		{
			System.out.println("EDSA Root starting ...");
			try
			{
				EDSARoot.EDSA().start(S3Config.EDSA_ROOT_PORT, new EDSARootTask(), Setup.S3_CONFIG_XML);
			}
			catch (ClassNotFoundException | IOException | RemoteReadException | DistributedNodeFailedException e)
			{
				e.printStackTrace();
			}
			System.out.println("EDSA Root started ...");

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
			System.out.println("The edsa-root is not set up properly!");
		}
	}

}
