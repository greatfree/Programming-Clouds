package org.greatfree.concurrency.threading.message;

import java.util.Set;

/*
 * Different from the TaskNotification, the notification is designed to execute one thread without detecting whether it is alive at the master-side, sending execution notification and assigning tasks remotely. All of the tasks can be performed at the slave-side. It must raise the performance of the system. 10/01/2019, Bing Li
 */

// Created: 10/01/2019, Bing Li
public abstract class TaskInvokeNotification extends ATMNotification
{
	private static final long serialVersionUID = -5591758250477591541L;
	
	private String taskKey;

	/*
	public TaskInvokeNotification(String taskKey)
	{
		super(ThreadingMessageType.TASK_INVOKE_NOTIFICATION);
		this.taskKey = taskKey;
	}
	*/

	public TaskInvokeNotification(String threadKey, String taskKey)
	{
		super(threadKey, ATMMessageType.TASK_INVOKE_NOTIFICATION);
		this.taskKey = taskKey;
	}
	
	public TaskInvokeNotification(Set<String> threadKeys, String taskKey)
	{
		super(threadKeys, ATMMessageType.TASK_INVOKE_NOTIFICATION);
		this.taskKey = taskKey;
	}

	public String getTaskKey()
	{
		return this.taskKey;
	}
}
