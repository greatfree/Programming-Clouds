package org.greatfree.framework.cluster.cs.multinode.intercast.group.clusterserver.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cs.twonode.server.AccountRegistry;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.util.TerminateSignal;

// Created: 04/07/2019, Bing Li
class ChatChild
{
	private ClusterChildContainer child;

	private ChatChild()
	{
	}
	
	private static ChatChild instance = new ChatChild();
	
	public static ChatChild GROUP()
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

	public void stop(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, IOException, RemoteIPNotExistedException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
		
		// Dispose the account registry. 04/30/2017, Bing Li
		AccountRegistry.CS().dispose();
		// Dispose the group registry. 04/30/2017, Bing Li
		GroupRegistry.CS().dispose();
		
		PublicChatSessions.HUNGARY().dispose();

		this.child.stop(timeout);
	}
	
	public void start(ChildTask task) throws ClassNotFoundException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, IOException, ServerPortConflictedException
	{
		PublicChatSessions.HUNGARY().init();
		
		this.child = new ClusterChildContainer(task);
		this.child.start(MulticastConfig.CLUSTER_SERVER_ROOT_KEY);
	}
}
