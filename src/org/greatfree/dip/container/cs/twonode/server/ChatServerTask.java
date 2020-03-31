package org.greatfree.dip.container.cs.twonode.server;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.app.business.dip.cs.multinode.server.AccountRegistry;
import org.greatfree.app.business.dip.cs.multinode.server.CSAccount;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.container.cs.twonode.message.ApplicationID;
import org.greatfree.dip.container.cs.twonode.message.ChatNotification;
import org.greatfree.dip.container.cs.twonode.message.ChatRegistryRequest;
import org.greatfree.dip.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageConfig;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;

// Created: 12/18/2018, Bing Li
class ChatServerTask implements ServerTask
{
	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ApplicationID.CHAT_NOTIFICATION:
				System.out.println("CHAT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ChatNotification cn = (ChatNotification)notification;
				System.out.println(cn.getSenderName() + " says " + cn.getMessage());
				break;
				
			case ApplicationID.SHUTDOWN_CHAT_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_CHAT_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					ChatServer.CS().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
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
			case ApplicationID.CHAT_REGISTRY_REQUEST:
				System.out.println("CHAT_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				ChatRegistryRequest crr = (ChatRegistryRequest)request;
				
				System.out.println(crr.getUserKey() + ", " + crr.getUserName() + ", " + crr.getDescription());

				AccountRegistry.CS().add(new CSAccount(crr.getUserKey(), crr.getUserName(), crr.getDescription()));
				return new ChatRegistryResponse(true);
		}

		return SystemMessageConfig.NO_MESSAGE;
	}
}



