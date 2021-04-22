package org.greatfree.framework.threading.message;

import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.framework.threading.TaskConfig;

// Created: 09/11/2019, Bing Li
// public class PrintTaskNotification extends InstructNotification
public class PrintTaskNotification extends TaskNotification
{
	private static final long serialVersionUID = 4349320297805514807L;
	
	private String message;
	private long sleepTime;

	public PrintTaskNotification(String threadKey, String message, long sleepTime)
	{
		super(threadKey, TaskConfig.PRINT_TASK_KEY);
		this.message = message;
		this.sleepTime = sleepTime;
	}

	/*
	public PrintTaskNotification(String message, long sleepTime)
	{
		super(TaskConfig.PRINT_TASK_KEY);
		this.message = message;
		this.sleepTime = sleepTime;
	}
	*/

	public String getMessage()
	{
		return this.message;
	}
	
	public long getSleepTime()
	{
		return this.sleepTime;
	}
}
