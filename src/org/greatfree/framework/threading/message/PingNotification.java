package org.greatfree.framework.threading.message;

import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.framework.threading.TaskConfig;

// Created: 09/13/2019, Bing Li
// public class PingNotification extends InstructNotification
public class PingNotification extends TaskNotification
{
	private static final long serialVersionUID = 4583233624574466138L;
	
	private String message;
	private long sleepTime;

	public PingNotification(String threadKey, String message, long sleepTime)
	{
//		super(threadKey, TaskMessageType.PING_TASK_NOTIFICATION);
		super(threadKey, TaskConfig.PING_TASK_KEY);
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

