package org.greatfree.concurrency.threading.message;

import java.util.Set;

import org.greatfree.util.Tools;

// Created: 10/01/2019, Bing Li
public abstract class TaskInvokeRequest extends InstructNotification
{
	private static final long serialVersionUID = 7373558250318366504L;
	
	private String taskKey;
	private String collaboratorKey;

	/*
	public TaskInvokeRequest(String taskKey)
	{
		super(ThreadingMessageType.TASK_INVOKE_REQUEST);
		this.taskKey = taskKey;
		this.collaboratorKey = Tools.generateUniqueKey();
	}
	*/

	public TaskInvokeRequest(String threadKey, String taskKey)
	{
		super(threadKey, ThreadingMessageType.TASK_INVOKE_REQUEST);
		this.taskKey = taskKey;
		this.collaboratorKey = Tools.generateUniqueKey();
	}

	public TaskInvokeRequest(Set<String> threadKeys, String taskKey)
	{
		super(threadKeys, ThreadingMessageType.TASK_INVOKE_REQUEST);
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
