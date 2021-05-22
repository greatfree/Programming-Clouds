package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.front.SaveMyPointingsListNotification;

// Created: 07/28/2018, Bing Li
public class SaveMyPointingsListThreadCreator implements NotificationQueueCreator<SaveMyPointingsListNotification, SaveMyPointingsListThread>
{

	@Override
	public SaveMyPointingsListThread createInstance(int taskSize)
	{
		return new SaveMyPointingsListThread(taskSize);
	}

}
