package org.greatfree.dip.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.cps.cache.message.front.SaveMyPointingMapNotification;

// Created: 07/20/2018, Bing Li
public class SaveMyPointingMapThreadCreator implements NotificationThreadCreatable<SaveMyPointingMapNotification, SaveMyPointingMapThread>
{

	@Override
	public SaveMyPointingMapThread createNotificationThreadInstance(int taskSize)
	{
		return new SaveMyPointingMapThread(taskSize);
	}

}
