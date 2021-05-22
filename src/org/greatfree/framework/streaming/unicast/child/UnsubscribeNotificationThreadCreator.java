package org.greatfree.framework.streaming.unicast.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.streaming.message.UnsubscribeNotification;

// Created: 03/23/2020, Bing Li
public class UnsubscribeNotificationThreadCreator implements NotificationQueueCreator<UnsubscribeNotification, UnsubscribeNotificationThread>
{

	@Override
	public UnsubscribeNotificationThread createInstance(int taskSize)
	{
		return new UnsubscribeNotificationThread(taskSize);
	}

}
