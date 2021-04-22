package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.SaveCachePointingNotification;

// Created: 07/24/2018, Bing Li
public class SaveCachePointingThreadCreator implements NotificationThreadCreatable<SaveCachePointingNotification, SaveCachePointingThread>
{

	@Override
	public SaveCachePointingThread createNotificationThreadInstance(int taskSize)
	{
		return new SaveCachePointingThread(taskSize);
	}

}
