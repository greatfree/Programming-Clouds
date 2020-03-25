package org.greatfree.demo.cluster.mncs.server.child;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.app.container.cs.multinode.business.server.MerchandiseDB;
import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.data.ServerConfig;
import org.greatfree.demo.cluster.mncs.message.BusinessApplicationID;
import org.greatfree.demo.cluster.mncs.message.MerchandiseRequest;
import org.greatfree.demo.cluster.mncs.message.MerchandiseResponse;
import org.greatfree.demo.cluster.mncs.message.PostMerchandiseNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

// Created: 02/17/2019, Bing Li
class BusinessChildTask implements ChildTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case BusinessApplicationID.POST_MERCHANDISE_NOTIFICATION:
				System.out.println("POST_MERCHANDISE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				PostMerchandiseNotification pn = (PostMerchandiseNotification)notification;
				MerchandiseDB.CS().saveMerchandise(pn.getMerchandise());
				break;

			case ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION:
				System.out.println("STOP_CHAT_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					BusinessChild.BUSINESS_CLUSTER_CHILD().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;

			case ClusterApplicationID.STOP_ONE_CHILD_ON_CLUSTER_NOTIFICATION:
				System.out.println("STOP_ONE_CHILD_ON_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					BusinessChild.BUSINESS_CLUSTER_CHILD().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
		
	}

	@Override
	public MulticastResponse processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case BusinessApplicationID.MERCHANDISE_REQUEST:
				System.out.println("MERCHANDISE_REQUEST received @" + Calendar.getInstance().getTime());
				MerchandiseRequest mr = (MerchandiseRequest)request;
				return new MerchandiseResponse(MerchandiseDB.CS().getMerchandise(mr.getMerchandiseName()), mr.getCollaboratorKey());
		}
		return null;
	}

	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification notification)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	@Override
	public void processDestinationNotification(InterChildrenNotification notification)
	{
		// TODO Auto-generated method stub
		
	}
	*/

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
	public void processResponse(Response response)
	{
		// TODO Auto-generated method stub
		
	}
}
