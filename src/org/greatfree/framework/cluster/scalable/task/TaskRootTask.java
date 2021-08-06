package org.greatfree.framework.cluster.scalable.task;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.scalable.message.ScalableApplicationID;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

/*
 * The program defines the tasks of the task cluster to be accomplished. 09/06/2020, Bing Li
 */

// Created: 09/06/2020, Bing Li
class TaskRootTask implements RootTask
{

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case ScalableApplicationID.STOP_TASK_CLUSTER_NOTIFICATION:
					try
					{
						TaskRoot.TASK().stopCluster();
					}
					catch (IOException | DistributedNodeFailedException e)
					{
						e.printStackTrace();
					}
				break;
				
			case ScalableApplicationID.STOP_TASK_ROOT_NOTIFICATION:
					try
					{
						TaskRoot.TASK().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
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
