package org.greatfree.concurrency.threading.message;

import org.greatfree.message.container.Notification;

// Created: 09/11/2019, Bing Li
public class TaskStateNotification extends Notification
{
	private static final long serialVersionUID = 8644217187715947563L;

	private String threadKey;
	private String taskKey;
	private String instructKey;
	private int instructType;
	private boolean isDone;

	public TaskStateNotification(String threadKey, String taskKey, int instructType, String instructKey, boolean isDone)
	{
		super(ThreadingMessageType.TASK_STATE_NOTIFICATION);
		this.threadKey = threadKey;
		this.taskKey = taskKey;
		this.instructType = instructType;
		this.instructKey = instructKey;
		this.isDone = isDone;
	}

	/*
	 * All tasks should have a key. So the constructor is not useful. 09/14/2019, Bing Li
	 * 
	 * If no taskKey, it indicates that all of threads in the Actor work on the same task. 09/13/2019, Bing Li
	 */
	/*
	public TaskStateNotification(String threadKey, int instructType, String instructKey, boolean isDone)
	{
		super(ThreadingMessageType.TASK_STATE_NOTIFICATION);
		this.threadKey = threadKey;
		this.taskKey = UtilConfig.EMPTY_STRING;
		this.instructType = instructType;
		this.instructKey = instructKey;
		this.isDone = isDone;
	}
	*/

	public String getThreadKey()
	{
		return this.threadKey;
	}
	
	public String getTaskKey()
	{
		return this.taskKey;
	}
	
	public int getInstructType()
	{
		return this.instructType;
	}
	
	public String getInstructKey()
	{
		return this.instructKey;
	}
	
	public boolean isDone()
	{
		return this.isDone;
	}
}
