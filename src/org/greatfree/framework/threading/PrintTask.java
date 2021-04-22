package org.greatfree.framework.threading;

import org.greatfree.concurrency.threading.ThreadTask;
import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.framework.threading.message.PrintTaskNotification;

// Created: 09/13/2019, Bing Li
public class PrintTask implements ThreadTask
{

	@Override
	public String getKey()
	{
		return TaskConfig.PRINT_TASK_KEY;
	}

	@Override
//	public void processNotification(InstructNotification notification)
	public void processNotification(String threadKey, TaskNotification notification)
	{
//		if (notification.getApplicationID() == TaskMessageType.PRINT_TASK_NOTIFICATION)
//		if (notification.getApplicationID() == ThreadingMessageType.TASK_NOTIFICATION)
		PrintTaskNotification ptn = (PrintTaskNotification)notification;
		System.out.println(ptn.getMessage());
		try
		{
			Thread.sleep(ptn.getSleepTime());
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public TaskResponse processRequest(String threadKey, TaskRequest request)
	{
		// TODO Auto-generated method stub
		return null;
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
