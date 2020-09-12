package org.greatfree.dsf.cluster.replication.root;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.dsf.cluster.replication.ReplicationConfig;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;

/*
 * This is the root of the cluster that is able to replicate data within its nodes to avoid possible loss of data. 09/07/2020, Bing Li
 */

// Created: 09/07/2020, Bing Li
class ReplicationRoot
{
	private ClusterServerContainer server;

	private ReplicationRoot()
	{
	}
	
	private static ReplicationRoot instance = new ReplicationRoot();
	
	public static ReplicationRoot REPLICATED()
	{
		if (instance == null)
		{
			instance = new ReplicationRoot();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void stopCluster() throws IOException, DistributedNodeFailedException
	{
		this.server.stopCluster();
	}

	public void stopServer(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		this.server.stop(timeout);
	}
	
	public void start(int port, RootTask task, int replicas) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		this.server = new ClusterServerContainer(port, ReplicationConfig.REPLICATION_ROOT, RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, task, replicas);
		this.server.start();
	}
}
