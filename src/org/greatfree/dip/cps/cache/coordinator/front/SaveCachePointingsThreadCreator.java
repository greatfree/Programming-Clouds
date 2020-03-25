package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.SaveCachePointingsNotification;

// Created: 07/24/2018, Bing Li
public class SaveCachePointingsThreadCreator implements NotificationThreadCreatable<SaveCachePointingsNotification, SaveCachePointingsThread>
{

	@Override
	public SaveCachePointingsThread createNotificationThreadInstance(int taskSize)
	{
		return new SaveCachePointingsThread(taskSize);
	}

}
