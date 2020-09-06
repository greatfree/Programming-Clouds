package org.greatfree.dsf.cluster.scalable.task;

import java.io.IOException;

import org.greatfree.cluster.RootTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cluster.scalable.message.ScalableApplicationID;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

/*
 * The program defines the tasks of the task cluster to be accomplished. 09/06/2020, Bing Li
 */

// Created: 09/06/2020, Bing Li
class TaskRootTask implements RootTask
{

	@Override
	public void processNotification(Notification notification)
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
	public Response processRequest(Request request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
