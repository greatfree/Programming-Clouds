package org.greatfree.demo.server;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.data.ServerConfig;
import org.greatfree.demo.message.ApplicationID;
import org.greatfree.demo.message.MerchandiseRequest;
import org.greatfree.demo.message.MerchandiseResponse;
import org.greatfree.demo.message.PostMerchandiseNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

// Created: 01/24/2019, Bing Li
class BusinessTask implements ServerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ApplicationID.POST_MERCHANDISE_NOTIFICATION:
				System.out.println("POST_MERCHANDISE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				PostMerchandiseNotification pmn = (PostMerchandiseNotification)notification;
				MerchandiseDB.CS().save(pmn.getMerchandise());
				break;
				
			case ApplicationID.SHUTDOWN_BUSINESS_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_BUSINESS_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					BusinessServer.CS().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
		
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case ApplicationID.MERCHANDISE_REQUEST:
				System.out.println("MERCHANDISE_REQUEST received @" + Calendar.getInstance().getTime());
				MerchandiseRequest r = (MerchandiseRequest)request;
				return new MerchandiseResponse(MerchandiseDB.CS().getMerchandise(r.getName()));
		}
		return null;
	}
}
