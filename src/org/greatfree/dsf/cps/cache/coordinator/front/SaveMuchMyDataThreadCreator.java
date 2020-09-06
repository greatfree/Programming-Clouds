package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.SaveMuchMyDataNotification;

// Created: 07/28/2018, Bing Li
public class SaveMuchMyDataThreadCreator implements NotificationThreadCreatable<SaveMuchMyDataNotification, SaveMuchMyDataThread>
{

	@Override
	public SaveMuchMyDataThread createNotificationThreadInstance(int taskSize)
	{
		return new SaveMuchMyDataThread(taskSize);
	}

}
