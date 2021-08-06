package org.greatfree.framework.cluster.replication.child;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.cluster.ChildTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.replication.child.db.ChildDB;
import org.greatfree.framework.cluster.replication.message.ReplicationApplicationID;
import org.greatfree.framework.cluster.replication.message.ReplicationTaskNotification;
import org.greatfree.framework.cluster.replication.message.ReplicationTaskRequest;
import org.greatfree.framework.cluster.replication.message.ReplicationTaskResponse;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.util.Tools;

/*
 * The program defines the task accomplished by the child of the replication cluster. It has no differences from others from programmers' point of view. 09/07/2020, Bing Li
 */

// Created: 09/07/2020, Bing Li
class ReplicationChildTask implements ChildTask
{

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case ReplicationApplicationID.REPLICATION_TASK_NOTIFICATION:
				System.out.println("REPLICATION_TASK_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ReplicationTaskNotification rtn = (ReplicationTaskNotification)notification;
				System.out.println(rtn.getMessage());
				ChildDB.DB().add(rtn.getMsgKey(), rtn.getMessage());
				break;
				
			case ReplicationApplicationID.STOP_REPLICATION_CLUSTER_NOTIFICATION:
				System.out.println("STOP_REPLICATION_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					ReplicationChild.REPLICATED().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public MulticastResponse processRequest(ClusterRequest request)
	{
		switch (request.getApplicationID())
		{
			case ReplicationApplicationID.REPLICATION_TASK_REQUEST:
				System.out.println("REPLICATION_TASK_REQUEST received @" + Calendar.getInstance().getTime());
				ReplicationTaskRequest rtr = (ReplicationTaskRequest)request;
				return new ReplicationTaskResponse("Child" + Tools.generateUniqueKey(), ChildDB.DB().getData(rtr.getMsgKey()), rtr.getCollaboratorKey());
		}
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
