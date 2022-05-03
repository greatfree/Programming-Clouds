package org.greatfree.demo.cps.coordinator;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.data.ServerConfig;
import org.greatfree.demo.cps.message.ApplicationID;
import org.greatfree.demo.cps.message.MerchandiseRequest;
import org.greatfree.demo.cps.message.OrderNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cps.message.CPSApplicationID;
import org.greatfree.framework.container.cps.message.StopTerminalNotification;
import org.greatfree.framework.container.cps.threenode.coordinator.Coordinator;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

// Created: 01/28/2019, Bing Li
class BusinessForwardTask implements ServerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ApplicationID.ORDER_NOTIFICATION:
				System.out.println("ORDER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				OrderNotification n = (OrderNotification)notification;
				try
				{
					Coordinator.CPS_CONTAINER().notify(n);
				}
				catch (IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case CPSApplicationID.STOP_TERMINAL_NOTIFICATION:
				System.out.println("STOP_TERMINAL_NOTIFICATION received @" + Calendar.getInstance().getTime());
				StopTerminalNotification stn = (StopTerminalNotification)notification;
				try
				{
					Coordinator.CPS_CONTAINER().notify(stn);
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
				try
				{
					return Coordinator.CPS_CONTAINER().read(r);
				}
				catch (ClassNotFoundException | RemoteReadException | IOException e)
				{
					e.printStackTrace();
				}
				break;
		}
		
		return null;
	}
}
