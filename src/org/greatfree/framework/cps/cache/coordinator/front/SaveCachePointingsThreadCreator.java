package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.front.SaveCachePointingsNotification;

// Created: 07/24/2018, Bing Li
public class SaveCachePointingsThreadCreator implements NotificationQueueCreator<SaveCachePointingsNotification, SaveCachePointingsThread>
{

	@Override
	public SaveCachePointingsThread createInstance(int taskSize)
	{
		return new SaveCachePointingsThread(taskSize);
	}

}
