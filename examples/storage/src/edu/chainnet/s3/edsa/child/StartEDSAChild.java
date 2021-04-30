package edu.chainnet.s3.edsa.child;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.s3.Setup;

/*
 * A 3-tier architecture is establish for the S3. The EDSA server is the backend tier, which is implemented with a cluster. This program is the child of the cluster. The EDSA server focuses on data encoding/decoding. 07/09/2020, Bing Li
 */

// Created: 07/09/2020, Bing Li
class StartEDSAChild
{
	public static void main(String[] args)
	{
		if (Setup.setupS3())
		{
			System.out.println("EDSA child starting up ...");
			try
			{
//				EDSAChild.EDSA().start(new EDSAChildTask(), S3Config.S3_CONFIG_FILE, S3Config.EDSA_CONFIG_FILE);
				EDSAChild.EDSA().start(new EDSAChildTask(), Setup.S3_CONFIG_XML);
			}
			catch (ClassNotFoundException | IOException | RemoteReadException | InterruptedException e)
			{
				e.printStackTrace();
			}
			System.out.println("EDSA child started ...");

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
			System.out.println("The edsa-child is not set up properly!");
		}
	}

}
