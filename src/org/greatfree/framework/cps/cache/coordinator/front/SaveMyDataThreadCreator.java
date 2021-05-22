package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.front.SaveMyDataNotification;

// Created: 07/09/2018, Bing Li
public class SaveMyDataThreadCreator implements NotificationQueueCreator<SaveMyDataNotification, SaveMyDataThread>
{

	@Override
	public SaveMyDataThread createInstance(int taskSize)
	{
		return new SaveMyDataThread(taskSize);
	}

}
