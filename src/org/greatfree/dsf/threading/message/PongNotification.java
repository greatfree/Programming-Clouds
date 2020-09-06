package org.greatfree.dsf.threading.message;

import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.dsf.threading.TaskConfig;

// Created: 09/13/2019, Bing Li
// public class PongNotification extends InstructNotification
public class PongNotification extends TaskNotification
{
	private static final long serialVersionUID = -5107977967226717509L;

	private String message;
	private long sleepTime;

	public PongNotification(String threadKey, String message, long sleepTime)
	{
//		super(threadKey, TaskMessageType.PONG_TASK_NOTIFICATION);
		super(threadKey, TaskConfig.PONG_TASK_KEY);
		this.message = message;
		this.sleepTime = sleepTime;
	}

	public String getMessage()
	{
		return this.message;
	}
	
	public long getSleepTime()
	{
		return this.sleepTime;
	}
}
