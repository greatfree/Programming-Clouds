package org.greatfree.dsf.cluster.scalable.pool;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cluster.scalable.message.ScalableApplicationID;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

// Created: 09/05/2020, Bing Li
class PoolRootTask implements RootTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ScalableApplicationID.STOP_POOL_CLUSTER_NOTIFICATION:
					try
					{
						PoolRoot.POOL().stopCluster();
					}
					catch (IOException | DistributedNodeFailedException e)
					{
						e.printStackTrace();
					}
				break;
				
			case ScalableApplicationID.STOP_POOL_ROOT_NOTIFICATION:
					try
					{
						PoolRoot.POOL().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
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

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
