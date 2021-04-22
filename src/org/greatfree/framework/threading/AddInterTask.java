package org.greatfree.framework.threading;

import org.greatfree.concurrency.threading.ThreadTask;
import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.framework.threading.message.AddInterNotification;
import org.greatfree.framework.threading.message.AddInterRequest;
import org.greatfree.framework.threading.message.AddInterResponse;

// Created: 10/05/2019, Bing Li
public class AddInterTask implements ThreadTask
{

	@Override
	public String getKey()
	{
		// TODO Auto-generated method stub
		return TaskConfig.ADD_TASK_KEY;
	}

	@Override
	public void processNotification(String threadKey, TaskNotification notification)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processNotification(String threadKey, TaskInvokeNotification notification)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processNotification(String threadKey, InteractNotification notification)
	{
		AddInterNotification an = (AddInterNotification)notification;
		System.out.println("AddInterTask-processNotification(): " + an.getX() + " + " + an.getY() + " = " + (an.getX() + an.getY()));
	}

	@Override
	public TaskResponse processRequest(String threadKey, TaskRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaskResponse processRequest(String threadKey, TaskInvokeRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaskResponse processRequest(String threadKey, InteractRequest request)
	{
		AddInterRequest ar = (AddInterRequest)request;
		System.out.println("AddInterTask-processRequest(): " + ar.getX() + " + " + ar.getY() + " = " + (ar.getX() + ar.getY()));
		return new AddInterResponse(ar.getX() + ar.getY(), ar.getCollaboratorKey());
	}

}
