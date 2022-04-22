package org.greatfree.demo.cluster.mncs.server;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.util.TerminateSignal;

// Created: 02/17/2019, Bing Li
class BusinessServer
{
	private ClusterServerContainer server;

	private BusinessServer()
	{
	}
	
	private static BusinessServer instance = new BusinessServer();
	
	public static BusinessServer MNCS_BUSINESS()
	{
		if (instance == null)
		{
			instance = new BusinessServer();
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
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
	}
	
	public void start(int port, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		this.server = new ClusterServerContainer(port, MulticastConfig.CLUSTER_SERVER_ROOT_NAME, task);
		this.server.start();
	}
}
