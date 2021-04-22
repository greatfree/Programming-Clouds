package org.greatfree.framework.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.cps.cache.message.front.SaveMyPointingsListNotification;

// Created: 07/28/2018, Bing Li
public class SaveMyPointingsListThreadCreator implements NotificationThreadCreatable<SaveMyPointingsListNotification, SaveMyPointingsListThread>
{

	@Override
	public SaveMyPointingsListThread createNotificationThreadInstance(int taskSize)
	{
		return new SaveMyPointingsListThread(taskSize);
	}

}
