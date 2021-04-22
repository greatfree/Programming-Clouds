package org.greatfree.framework.cluster.cs.twonode.clusterclient.child;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.original.cs.twonode.message.AreYouReadyRequest;
import org.greatfree.framework.cluster.original.cs.twonode.message.AreYouReadyResponse;
import org.greatfree.framework.cluster.original.cs.twonode.message.ChatApplicationID;
import org.greatfree.framework.cluster.original.cs.twonode.message.ChatRegistryResultNotification;
import org.greatfree.message.SystemMessageConfig;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

// Created: 01/15/2019, Bing Li
class ChatTask implements ChildTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ChatApplicationID.CHAT_REGISTRY_RESULT_NOTIFICATION:
				System.out.println("CHAT_REGISTRY_RESULT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ChatRegistryResultNotification crrn = (ChatRegistryResultNotification)notification;
				System.out.println("Registry result is succeeded? " + crrn.isSucceeded());
				break;

			case ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION:
				System.out.println("STOP_CHAT_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					ChatChild.CCC().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
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
			case ChatApplicationID.ARE_YOU_READY_REQUEST:
				System.out.println("ARE_YOU_READY_REQUEST received @" + Calendar.getInstance().getTime());
				AreYouReadyRequest ayrr = (AreYouReadyRequest)request;
				return new AreYouReadyResponse(ayrr.getCollaboratorKey(), ayrr.getResourceKey(), true);
		}
		return SystemMessageConfig.NO_MULTICAST_RESPONSE;
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
