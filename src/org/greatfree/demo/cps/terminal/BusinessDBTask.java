package org.greatfree.demo.cps.terminal;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.data.ServerConfig;
import org.greatfree.demo.cps.message.ApplicationID;
import org.greatfree.demo.cps.message.MerchandiseRequest;
import org.greatfree.demo.cps.message.MerchandiseResponse;
import org.greatfree.demo.cps.message.OrderNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.cps.message.CPSApplicationID;
import org.greatfree.framework.container.cps.threenode.terminal.TerminalServer;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

// Created: 01/28/2019, Bing Li
class BusinessDBTask implements ServerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ApplicationID.ORDER_NOTIFICATION:
				System.out.println("ORDER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				OrderNotification n = (OrderNotification)notification;
				System.out.println("The coordinator notification: " + n.getMerchandise() + ", " + n.getCount());
				break;
				
			case CPSApplicationID.STOP_TERMINAL_NOTIFICATION:
				System.out.println("STOP_TERMINAL_NOTIFICATION received @" + Calendar.getInstance().getTime());
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
			case ApplicationID.MERCHANDISE_REQUEST:
				System.out.println("MERCHANDISE_REQUEST received @" + Calendar.getInstance().getTime());
				MerchandiseRequest r = (MerchandiseRequest)request;
				System.out.println("Received request: " + r.getQuery());
				return new MerchandiseResponse("iPhonX", 100);
		}
		return null;
	}
}
