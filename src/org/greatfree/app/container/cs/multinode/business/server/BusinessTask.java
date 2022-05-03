package org.greatfree.app.container.cs.multinode.business.server;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.app.container.cs.multinode.business.message.ApplicationID;
import org.greatfree.app.container.cs.multinode.business.message.MerchandiseRequest;
import org.greatfree.app.container.cs.multinode.business.message.MerchandiseResponse;
import org.greatfree.app.container.cs.multinode.business.message.PostMerchandiseNotification;
import org.greatfree.data.ServerConfig;
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
				PostMerchandiseNotification n = (PostMerchandiseNotification)notification;
				MerchandiseDB.CS().saveMerchandise(n.getMerchanise());
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
				System.out.println("name = " + r.getMerchandiseName());
				return new MerchandiseResponse(MerchandiseDB.CS().getMerchandise(r.getMerchandiseName()));
		}
		return null;
	}
}
