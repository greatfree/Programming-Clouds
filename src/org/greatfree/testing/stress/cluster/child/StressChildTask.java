package org.greatfree.testing.stress.cluster.child;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.testing.stress.cluster.message.StressAppID;
import org.greatfree.testing.stress.cluster.message.StressNotification;

// Created: 11/07/2021, Bing Li
class StressChildTask implements ChildTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.testing.stress.cluster.child");

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION:
				log.info("STOP_CHAT_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					StressChild.CHILD().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
				
			case StressAppID.STRESS_NOTIFICATION:
				log.info("STRESS_NOTIFICATION received @" + Calendar.getInstance().getTime());
				StressNotification sn = (StressNotification)notification;
				log.info("message = " + sn);
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
