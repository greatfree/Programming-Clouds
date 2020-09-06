package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.SaveMuchUKsNotification;

// Created: 03/01/2019, Bing Li
public class SaveMuchUKsThreadCreator implements NotificationThreadCreatable<SaveMuchUKsNotification, SaveMuchUKsThread>
{

	@Override
	public SaveMuchUKsThread createNotificationThreadInstance(int taskSize)
	{
		return new SaveMuchUKsThread(taskSize);
	}

}
