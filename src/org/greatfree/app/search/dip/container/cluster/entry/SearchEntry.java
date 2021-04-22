package org.greatfree.app.search.dip.container.cluster.entry;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;

// Created: 01/14/2019, Bing Li
class SearchEntry
{
	private ClusterServerContainer server;

	private SearchEntry()
	{
	}
	
	private static SearchEntry instance = new SearchEntry();
	
	public static SearchEntry CLUSTER()
	{
		if (instance == null)
		{
			instance = new SearchEntry();
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
		/*
		if (!this.server.isChildrenEmpty())
		{
			TerminateSignal.SIGNAL().waitTermination(timeout);
		}
		// Set the terminating signal. 11/25/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();
		*/
		this.server.stop(timeout);
	}
	
	public void start(int port, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException
	{
		this.server = new ClusterServerContainer(port, MulticastConfig.CLUSTER_SERVER_ROOT_NAME, task);
		this.server.start();
	}
}
