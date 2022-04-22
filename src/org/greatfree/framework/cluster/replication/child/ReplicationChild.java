package org.greatfree.framework.cluster.replication.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.replication.ReplicationConfig;
import org.greatfree.util.TerminateSignal;

/*
 * This is the child of the cluster that is able to replicate data within its nodes to avoid possible loss of data. 09/07/2020, Bing Li
 */

// Created: 09/07/2020, Bing Li
class ReplicationChild
{
	private ClusterChildContainer child;
	
	private ReplicationChild()
	{
	}
	
	private static ReplicationChild instance = new ReplicationChild();
	
	public static ReplicationChild REPLICATED()
	{
		if (instance == null)
		{
			instance = new ReplicationChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.child.stop(timeout);
	}
	
	public void start(ChildTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.child = new ClusterChildContainer(task);
		this.child.start(ReplicationConfig.REPLICATION_ROOT_KEY);
	}

}
