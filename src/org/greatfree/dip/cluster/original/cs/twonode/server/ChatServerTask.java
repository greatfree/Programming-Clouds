package org.greatfree.dip.cluster.original.cs.twonode.server;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

// Created: 10/30/2018, Bing Li
class ChatServerTask implements RootTask
{

	@Override
	public void processNotification(Notification notification)
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
	public Response processRequest(Request request)
	{
		return null;
	}

}
