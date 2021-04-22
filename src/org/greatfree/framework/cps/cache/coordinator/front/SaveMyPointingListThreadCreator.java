package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.SaveMyPointingListNotification;

// Created: 07/13/2018, Bing Li
public class SaveMyPointingListThreadCreator implements NotificationThreadCreatable<SaveMyPointingListNotification, SaveMyPointingListThread>
{

	@Override
	public SaveMyPointingListThread createNotificationThreadInstance(int taskSize)
	{
		return new SaveMyPointingListThread(taskSize);
	}

}
