package org.greatfree.dsf.threading;

import org.greatfree.concurrency.threading.ThreadTask;
import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.dsf.threading.message.AddRequest;
import org.greatfree.dsf.threading.message.AddResponse;

// Created: 09/28/2019, Bing Li
public class AddTask implements ThreadTask
{

	@Override
	public String getKey()
	{
		return TaskConfig.ADD_TASK_KEY;
	}

	@Override
	public void processNotification(String threadKey, TaskNotification notification)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public TaskResponse processRequest(String threadKey, TaskRequest request)
	{
		AddRequest ar = (AddRequest)request;
		System.out.println("AddTask-processRequest(): " + ar.getX() + " + " + ar.getY());
		return new AddResponse(ar.getX() + ar.getY(), ar.getCollaboratorKey());
	}

	@Override
	public void processNotification(String threadKey, TaskInvokeNotification notification)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public TaskResponse processRequest(String threadKey, TaskInvokeRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processNotification(String threadKey, InteractNotification notification)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public TaskResponse processRequest(String threadKey, InteractRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}

