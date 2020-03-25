package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.reactive.NotificationTask;
import org.greatfree.concurrency.reactive.NotificationTaskThreadCreator;

// Created: 01/22/2019, Bing Li
class MyTaskNotificationThreadCreator implements NotificationTaskThreadCreator<MyTaskNotification, MyTaskNotificationThread>
{

	@Override
	public MyTaskNotificationThread createThreadInstance(int taskSize, NotificationTask<MyTaskNotification> task)
	{
		return new MyTaskNotificationThread(taskSize, task);
	}

}
