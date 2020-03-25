package org.greatfree.concurrency.threading;

import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.concurrency.threading.message.TaskResponse;

// Created: 09/13/2019, Bing Li
public interface ThreadTask
{
	public String getKey();
//	public void processNotification(InstructNotification notification);
//	public void processNotification(TaskNotification notification);
	public void processNotification(String threadKey, TaskNotification notification);
	public void processNotification(String threadKey, TaskInvokeNotification notification);
	public void processNotification(String threadKey, InteractNotification notification);
//	public Response processRequest(String threadKey, )
	public TaskResponse processRequest(String threadKey, TaskRequest request);
	public TaskResponse processRequest(String threadKey, TaskInvokeRequest request);
	public TaskResponse processRequest(String threadKey, InteractRequest request);
}
