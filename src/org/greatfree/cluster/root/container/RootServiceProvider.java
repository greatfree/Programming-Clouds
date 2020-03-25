package org.greatfree.cluster.root.container;

import org.greatfree.cluster.RootTask;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

// Created: 01/27/2019, Bing Li
public class RootServiceProvider
{
	private RootTask task;
	
	private RootServiceProvider()
	{
	}
	
	private static RootServiceProvider instance = new RootServiceProvider();
	
	public static RootServiceProvider ROOT()
	{
		if (instance == null)
		{
			instance = new RootServiceProvider();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void init(RootTask task)
	{
		this.task = task;
	}
	
	public void processNotification(Notification notification)
	{
		this.task.processNotification(notification);
	}

	public Response processRequest(Request request)
	{
		return this.task.processRequest(request);
	}
}
