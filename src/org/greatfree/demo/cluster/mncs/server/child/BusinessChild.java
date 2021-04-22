package org.greatfree.demo.cluster.mncs.server.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.util.TerminateSignal;

// Created: 02/17/2019, Bing Li
class BusinessChild
{
	private ClusterChildContainer child;
	
	private BusinessChild()
	{
	}
	
	private static BusinessChild instance = new BusinessChild();
	
	public static BusinessChild BUSINESS_CLUSTER_CHILD()
	{
		if (instance == null)
		{
			instance = new BusinessChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();
		
		this.child.stop(timeout);
	}

	public void start(ChildTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.child = new ClusterChildContainer(task);
		this.child.start(MulticastConfig.CLUSTER_SERVER_ROOT_KEY);
	}
	
}
