package org.greatfree.framework.container.cps.threenode.terminal;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cps.message.CPSApplicationID;
import org.greatfree.framework.container.cps.message.FrontNotification;
import org.greatfree.framework.container.cps.message.FrontRequest;
import org.greatfree.framework.cps.threetier.message.FrontResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

// Created: 12/31/2018, Bing Li
public class TerminalTask implements ServerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case CPSApplicationID.FRONT_NOTIFICATION:
				FrontNotification fn = (FrontNotification)notification;
				System.out.println("The notification received is: " + fn.getNotification());
				break;
				
			case CPSApplicationID.STOP_TERMINAL_NOTIFICATION:
				try
				{
					TerminalServer.CPS_CONTAINER().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
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
			case CPSApplicationID.FRONT_REQUEST:
				FrontRequest req = (FrontRequest)request;
				System.out.println("The request received is: " + req.getQuery());
				return new FrontResponse("My Response");
		}
		return null;
	}
}
