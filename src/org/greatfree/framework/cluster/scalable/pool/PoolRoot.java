package org.greatfree.framework.cluster.scalable.pool;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cluster.scalable.ScalableConfig;
import org.greatfree.util.TerminateSignal;

/*
 * The program is the root of the pool cluster. 09/05/2020, Bing Li
 */

// Created: 09/05/2020, Bing Li
class PoolRoot
{
	private ClusterServerContainer server;

	private PoolRoot()
	{
	}
	
	private static PoolRoot instance = new PoolRoot();
	
	public static PoolRoot POOL()
	{
		if (instance == null)
		{
			instance = new PoolRoot();
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

	public void stopServer(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, RemoteIPNotExistedException
	{
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
	}
	
	public void start(int port, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.server = new ClusterServerContainer(port, ScalableConfig.POOL_CLUSTER_ROOT, task);
		this.server.start();
	}

}
