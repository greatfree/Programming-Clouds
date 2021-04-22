package org.greatfree.framework.cluster.scalable.pool.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.scalable.ScalableConfig;
import org.greatfree.util.TerminateSignal;

/*
 * The program defines the child node in the pool cluster to support the task cluster which lacks for computing resources. 09/05/2020, Bing Li
 */

// Created: 09/05/2020, Bing Li
class PoolChild
{
	private ClusterChildContainer child;
	
	private PoolChild()
	{
	}
	
	private static PoolChild instance = new PoolChild();
	
	public static PoolChild POOL()
	{
		if (instance == null)
		{
			instance = new PoolChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();
		this.child.stop(timeout);
	}
	
	public void start(ChildTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.child = new ClusterChildContainer(task);
		this.child.start(ScalableConfig.POOL_CLUSTER_ROOT_KEY);
	}
}
