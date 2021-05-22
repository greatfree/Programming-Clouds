package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.TestNotification;

// Created: 12/10/2016, Bing Li
class TestNotificationThreadCreator implements NotificationQueueCreator<TestNotification, TestNotificationThread>
{

	@Override
	public TestNotificationThread createInstance(int taskSize)
	{
		// TODO Auto-generated method stub
		return new TestNotificationThread(taskSize);
	}

}
