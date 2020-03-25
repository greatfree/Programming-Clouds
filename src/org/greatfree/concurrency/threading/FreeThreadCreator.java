package org.greatfree.concurrency.threading;

import org.greatfree.concurrency.threading.message.InstructNotification;

// Created: 09/13/2019, Bing Li
class FreeThreadCreator implements NotificationThreadCreatable<InstructNotification, FreeThread>
{

	@Override
	public FreeThread createNotificationThreadInstance(int taskSize)
	{
		return new FreeThread(taskSize);
	}

}
