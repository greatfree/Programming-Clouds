package org.greatfree.framework.container.cps.threenode.coordinator;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cps.message.CPSApplicationID;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

// Created: 12/31/2018, Bing Li
public class ForwardTask implements ServerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case CPSApplicationID.FRONT_NOTIFICATION:
				System.out.println("FRONT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Coordinator.CPS_CONTAINER().notify(notification);
				}
				catch (IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case CPSApplicationID.STOP_TERMINAL_NOTIFICATION:
				System.out.println("FRONT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Coordinator.CPS_CONTAINER().notify(notification);
				}
				catch (IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
				break;

			case CPSApplicationID.STOP_COORDINATOR_NOTIFICATION:
				System.out.println("STOP_COORDINATOR_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					Coordinator.CPS_CONTAINER().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (IOException | InterruptedException | ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException e)
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
				System.out.println("FRONT_REQUEST received @" + Calendar.getInstance().getTime());
				try
				{
					return Coordinator.CPS_CONTAINER().read(request);
				}
				catch (ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
		}
		return null;
	}
}
