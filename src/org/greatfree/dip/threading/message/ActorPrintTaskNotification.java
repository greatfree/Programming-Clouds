package org.greatfree.dip.threading.message;

import org.greatfree.concurrency.threading.message.TaskNotification;

// Created: 09/14/2019, Bing Li
public class ActorPrintTaskNotification extends TaskNotification
{
	private static final long serialVersionUID = -157607195750173322L;

	public ActorPrintTaskNotification(String threadKey, String taskKey)
	{
		super(threadKey, taskKey);
	}

}
