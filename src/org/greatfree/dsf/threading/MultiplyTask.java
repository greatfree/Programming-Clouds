package org.greatfree.dsf.threading;

import org.greatfree.concurrency.threading.ThreadTask;
import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.dsf.threading.message.MultiplyRequest;
import org.greatfree.dsf.threading.message.MultiplyResponse;

// Created: 10/05/2019, Bing Li
public class MultiplyTask implements ThreadTask
{

	@Override
	public String getKey()
	{
		return TaskConfig.MULTIPLY_TASK_KEY;
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
		// TODO Auto-generated method stub
		
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
		MultiplyRequest mr = (MultiplyRequest)request;
		System.out.println("MultiplyTask-processRequest(): " + mr.getX() + " * " + mr.getY());
		return new MultiplyResponse(mr.getX() * mr.getY(), mr.getCollaboratorKey());
	}

	@Override
	public TaskResponse processRequest(String threadKey, InteractRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
