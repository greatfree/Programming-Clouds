package org.greatfree.app.search.container.cluster.entry;

import java.io.IOException;

import org.greatfree.app.search.container.cluster.message.SearchApplicationID;
import org.greatfree.cluster.RootTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

// Created: 01/14/2019, Bing Li
class SearchTask implements RootTask
{

	@Override
	public void processNotification(ClusterNotification notification)
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
				catch (ClassNotFoundException | InterruptedException | RemoteReadException | IOException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				break;
		}
		
	}

	@Override
	public CollectedClusterResponse processRequest(ClusterRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
