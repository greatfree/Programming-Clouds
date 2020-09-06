package org.greatfree.dsf.cps.cache.coordinator.front;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dsf.cps.cache.message.front.SaveMyPointingsMapNotification;

// Created: 08/01/2018, Bing Li
public class SaveMyPointingsMapThreadCreator implements NotificationThreadCreatable<SaveMyPointingsMapNotification, SaveMyPointingsMapThread>
{

	@Override
	public SaveMyPointingsMapThread createNotificationThreadInstance(int taskSize)
	{
		return new SaveMyPointingsMapThread(taskSize);
	}

}
