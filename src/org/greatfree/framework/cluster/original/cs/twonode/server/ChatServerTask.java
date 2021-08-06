package org.greatfree.framework.cluster.original.cs.twonode.server;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

// Created: 10/30/2018, Bing Li
class ChatServerTask implements RootTask
{

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION:
					try
					{
						ChatServer.CSCLUSTER().stopCluster();
					}
					catch (IOException | DistributedNodeFailedException e)
					{
						e.printStackTrace();
					}
				break;
				
			case ClusterApplicationID.STOP_SERVER_ON_CLUSTER:
					try
					{
						ChatServer.CSCLUSTER().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
					}
					catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
					{
						e.printStackTrace();
					}
				break;
		}
	}

	@Override
	public CollectedClusterResponse processRequest(ClusterRequest request)
	{
		return null;
	}

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
