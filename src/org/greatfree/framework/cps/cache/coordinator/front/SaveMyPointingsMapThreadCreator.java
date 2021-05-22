package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.front.SaveMyPointingsMapNotification;

// Created: 08/01/2018, Bing Li
public class SaveMyPointingsMapThreadCreator implements NotificationQueueCreator<SaveMyPointingsMapNotification, SaveMyPointingsMapThread>
{

	@Override
	public SaveMyPointingsMapThread createInstance(int taskSize)
	{
		return new SaveMyPointingsMapThread(taskSize);
	}

}
