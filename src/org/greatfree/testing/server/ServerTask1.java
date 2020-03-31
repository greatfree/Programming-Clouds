package org.greatfree.testing.server;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;
import org.greatfree.testing.message.ApplicationID;
import org.greatfree.testing.message.DResponse;

// Created: 03/30/2020, Bing Li
class ServerTask1 implements ServerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ApplicationID.SHUTDOWN_D_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_D_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					DoubleServers.CS().stop(ServerConfig.SLEEP_TIME);
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
			case ApplicationID.D_REQUEST:
				System.out.println("D_REQUEST received @" + Calendar.getInstance().getTime());
				return new DResponse(true);
		}
		return null;
	}

}
