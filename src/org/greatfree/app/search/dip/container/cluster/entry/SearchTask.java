package org.greatfree.app.search.dip.container.cluster.entry;

import java.io.IOException;

import org.greatfree.app.search.dip.container.cluster.message.SearchApplicationID;
import org.greatfree.cluster.RootTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

// Created: 01/14/2019, Bing Li
class SearchTask implements RootTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case SearchApplicationID.SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION:
				try
				{
					SearchEntry.CLUSTER().stopCluster();
				}
				catch (IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case SearchApplicationID.SHUTDOWN_SERVER_NOTIFICATION:
				try
				{
					SearchEntry.CLUSTER().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
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
		// TODO Auto-generated method stub
		return null;
	}

}
