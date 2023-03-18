package org.greatfree.app.search.container.cluster.storage;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.multicast.MulticastConfig;
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

	public void stop(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, IOException, RemoteIPNotExistedException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.child.stop(timeout);
	}

	public void start(ChildTask task) throws ClassNotFoundException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, IOException, ServerPortConflictedException
	{
		this.child = new ClusterChildContainer(task);
		this.child.start(MulticastConfig.CLUSTER_SERVER_ROOT_KEY);
	}
}
