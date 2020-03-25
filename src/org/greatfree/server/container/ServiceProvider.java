package org.greatfree.server.container;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

// Created: 12/18/2018, Bing Li
class ServiceProvider
{
	private ServerTask task;
	
	private ServiceProvider()
	{
	}
	
	private static ServiceProvider instance = new ServiceProvider();
	
	public static ServiceProvider CS()
	{
		if (instance == null)
		{
			instance = new ServiceProvider();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void init(ServerTask task)
	{
		this.task = task;
	}
	
	public void processNotification(Notification notification)
	{
		this.task.processNotification(notification);
	}
	
	public ServerMessage processRequest(Request request)
	{
		return this.task.processRequest(request);
	}
}
