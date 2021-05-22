package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.front.SaveMyPointingMapNotification;

// Created: 07/20/2018, Bing Li
public class SaveMyPointingMapThreadCreator implements NotificationQueueCreator<SaveMyPointingMapNotification, SaveMyPointingMapThread>
{

	@Override
	public SaveMyPointingMapThread createInstance(int taskSize)
	{
		return new SaveMyPointingMapThread(taskSize);
	}

}
