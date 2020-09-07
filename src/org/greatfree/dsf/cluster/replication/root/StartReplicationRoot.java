package org.greatfree.dsf.cluster.replication.root;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cluster.replication.ReplicationConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

/*
 * The program intends to replicate data on the predefined partitions of the cluster. It aims to avoid possible loss of data. 09/07/2020, Bing Li 
 */

// Created: 09/07/2020, Bing Li
class StartReplicationRoot
{

	public static void main(String[] args)
	{
		System.out.println("Replication root starting up ...");

		try
		{
			ReplicationRoot.REPLICATED().start(ReplicationConfig.REPLICATION_ROOT_PORT, new ReplicationRootTask(), ReplicationConfig.REPLICAS);
		}
		catch (ClassNotFoundException | IOException | RemoteReadException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}

		System.out.println("Replication root started ...");

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
