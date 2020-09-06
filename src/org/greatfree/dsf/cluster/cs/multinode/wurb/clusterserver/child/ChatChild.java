package org.greatfree.dsf.cluster.cs.multinode.wurb.clusterserver.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.dsf.cs.multinode.server.PrivateChatSessions;
import org.greatfree.dsf.cs.twonode.server.AccountRegistry;
import org.greatfree.dsf.multicast.MulticastConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 02/04/2019, Bing Li
public class ChatChild
{
	private ClusterChildContainer child;
	
	private ChatChild()
	{
	}
	
	private static ChatChild instance = new ChatChild();
	
	public static ChatChild CLUSTER_CONTAINER()
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

		// Dispose the chatting session resources. 04/30/2017, Bing Li
		PrivateChatSessions.HUNGARY().dispose();
		
		this.child.stop(timeout);
	}
	
	public void start(ChildTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		// Initialize the private chatting sessions. 04/23/2017, Bing Li
		PrivateChatSessions.HUNGARY().init();

		this.child = new ClusterChildContainer(task);
		this.child.start(MulticastConfig.CLUSTER_SERVER_ROOT_KEY);
	}
}
