package org.greatfree.framework.cluster.cs.twonode.clusterserver;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.root.container.ClusterServerContainer;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.util.TerminateSignal;

// Created: 01/13/2019, Bing Li
public class ChatServer
{
	private ClusterServerContainer server;

	private ChatServer()
	{
	}
	
	private static ChatServer instance = new ChatServer();
	
	public static ChatServer CONTAINER()
	{
		if (instance == null)
		{
			instance = new ChatServer();
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

	public void stopServer(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, IOException, RemoteIPNotExistedException
	{
		/*
		if (!this.server.isChildrenEmpty())
		{
			TerminateSignal.SIGNAL().waitTermination(timeout);
		}
		// Set the terminating signal. 11/25/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();
		*/
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.server.stop(timeout);
	}
	
	public void start(int port, RootTask task) throws IOException, ClassNotFoundException, RemoteReadException, DistributedNodeFailedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.server = new ClusterServerContainer(port, MulticastConfig.CLUSTER_SERVER_ROOT_NAME, task);
		this.server.start();
	}
}


