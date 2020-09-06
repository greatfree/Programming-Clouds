package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.SaveMyUKNotification;

// Created: 03/01/2019, Bing Li
public class SaveMyUKThreadCreator implements NotificationThreadCreatable<SaveMyUKNotification, SaveMyUKThread>
{

	@Override
	public SaveMyUKThread createNotificationThreadInstance(int taskSize)
	{
		return new SaveMyUKThread(taskSize);
	}

}
