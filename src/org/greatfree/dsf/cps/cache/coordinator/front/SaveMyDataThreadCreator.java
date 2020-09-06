package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.SaveMyDataNotification;

// Created: 07/09/2018, Bing Li
public class SaveMyDataThreadCreator implements NotificationThreadCreatable<SaveMyDataNotification, SaveMyDataThread>
{

	@Override
	public SaveMyDataThread createNotificationThreadInstance(int taskSize)
	{
		return new SaveMyDataThread(taskSize);
	}

}
