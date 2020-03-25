package org.greatfree.concurrency.threading.message;

import java.util.Set;

import org.greatfree.util.Tools;

// Created: 09/28/2019, Bing Li
public abstract class TaskRequest extends InstructNotification
{
	private static final long serialVersionUID = 8437517232659603283L;

	private String taskKey;
	private String collaboratorKey;

	/*
	public TaskRequest(String taskKey)
	{
		super(ThreadingMessageType.TASK_REQUEST);
		this.taskKey = taskKey;
		this.collaboratorKey = Tools.generateUniqueKey();
	}
	*/

	public TaskRequest(String threadKey, String taskKey)
	{
		super(threadKey, ThreadingMessageType.TASK_REQUEST);
		this.taskKey = taskKey;
		this.collaboratorKey = Tools.generateUniqueKey();
	}

	public TaskRequest(Set<String> threadKeys, String taskKey)
	{
		super(threadKeys, ThreadingMessageType.TASK_REQUEST);
		this.taskKey = taskKey;
		this.collaboratorKey = Tools.generateUniqueKey();
	}
	
	public String getTaskKey()
	{
		return this.taskKey;
	}
	
	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}
}
