package org.greatfree.app.search.dip.container.cluster.storage;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.dsf.multicast.MulticastConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 01/14/2019, Bing Li
class StorageNode
{
	private ClusterChildContainer child;
	
	private StorageNode()
	{
	}
	
	private static StorageNode instance = new StorageNode();
	
	public static StorageNode CLUSTER()
	{
		if (instance == null)
		{
			instance = new StorageNode();
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
