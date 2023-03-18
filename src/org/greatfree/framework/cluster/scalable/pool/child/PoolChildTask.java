package org.greatfree.framework.cluster.scalable.pool.child;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.cluster.ChildTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.scalable.message.ScalableApplicationID;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;

/*
 * The program defines the tasks to be accomplished by the child of the pool cluster. The primary task is to leave the pool cluster and join the task cluster. 09/05/2020, Bing Li
 */

// Created: 09/05/2020, Bing Li
class PoolChildTask implements ChildTask
{

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case ScalableApplicationID.STOP_POOL_CLUSTER_NOTIFICATION:
				System.out.println("STOP_POOL_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					PoolChild.POOL().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				break;
		
				/*
				 * If the child leaves the pool cluster and joins the task cluster, it must shutdown if it receives the notification from the root of the task cluster. 09/06/2020, Bing Li
				 */
			case ScalableApplicationID.STOP_TASK_CLUSTER_NOTIFICATION:
				System.out.println("STOP_TASK_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					PoolChild.POOL().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public MulticastResponse processRequest(ClusterRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification notification)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InterChildrenRequest prepareRequest(IntercastRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MulticastResponse processRequest(InterChildrenRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processResponse(CollectedClusterResponse response)
	{
		// TODO Auto-generated method stub
		
	}

}
