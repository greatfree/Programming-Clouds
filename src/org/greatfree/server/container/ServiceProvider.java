package org.greatfree.server.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

/*
 * A map is used to keep multiple server tasks to deal with the case when multiple server containers are created within a single process. 03/30/2020, Bing Li
 * 
 * The key of the map is the server key from CSServer. 03/30/2020, Bing Li
 * 
 * 	The key is used to identify server tasks if multiple servers instances exist within a single process. In the previous versions, only one server tasks are allowed. It is a defect if multiple instances of servers exist in a process since they are overwritten one another. 03/30/2020, Bing Li
 */

// Created: 12/18/2018, Bing Li
class ServiceProvider
{
//	private ServerTask task;
	private Map<String, ServerTask> tasks;
	
	private ServiceProvider()
	{
		this.tasks = new ConcurrentHashMap<String, ServerTask>();
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

	public void init(String serverKey, ServerTask task)
	{
		this.tasks.put(serverKey, task);
	}
	
	public void processNotification(String serverKey, Notification notification)
	{
		this.tasks.get(serverKey).processNotification(notification);
	}
	
	public ServerMessage processRequest(String serverKey, Request request)
	{
		return this.tasks.get(serverKey).processRequest(request);
	}
}
