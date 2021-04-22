package org.greatfree.framework.cluster.cs.twonode.clusterserver.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cs.twonode.server.AccountRegistry;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.util.TerminateSignal;

// Created: 01/13/2019, Bing Li
public class ChatChild
{
	private ClusterChildContainer child;
	
	private ChatChild()
	{
	}
	
	private static ChatChild instance = new ChatChild();
	
	public static ChatChild CONTAINER()
	{
		if (instance == null)
		{
			instance = new ChatChild();
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

		// Dispose the account registry. 04/30/2017, Bing Li
		AccountRegistry.CS().dispose();

		this.child.stop(timeout);
	}

	public void start(ChildTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.child = new ClusterChildContainer(task);
		this.child.start(MulticastConfig.CLUSTER_SERVER_ROOT_KEY);
	}
}
