package org.greatfree.concurrency.threading.message;

import java.util.Set;

// Created: 09/14/2019, Bing Li
public abstract class TaskNotification extends ATMNotification
{
	private static final long serialVersionUID = 7672681251512844760L;
	
	private String taskKey;

	/*
	public TaskNotification(String taskKey)
	{
		super(ThreadingMessageType.TASK_NOTIFICATION);
		this.taskKey = taskKey;
	}
	*/

	public TaskNotification(String threadKey, String taskKey)
	{
		super(threadKey, ATMMessageType.TASK_NOTIFICATION);
		this.taskKey = taskKey;
	}

	public TaskNotification(Set<String> threadKeys, String taskKey)
	{
		super(threadKeys, ATMMessageType.TASK_NOTIFICATION);
		this.taskKey = taskKey;
	}

	public String getTaskKey()
	{
		return this.taskKey;
	}
}
