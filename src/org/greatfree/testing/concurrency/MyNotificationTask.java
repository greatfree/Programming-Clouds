package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.reactive.NotificationTask;

// Created: 01/22/2019, Bing Li
class MyNotificationTask implements NotificationTask<MyTaskNotification>
{

	@Override
	public void processNotification(MyTaskNotification notification)
	{
		System.out.println("notification = " + notification.getKey() + ", Bye!");
	}

}
