package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.testing.message.TestNotification;

// Created: 12/10/2016, Bing Li
public class TestNotificationThreadCreator implements NotificationThreadCreatable<TestNotification, TestNotificationThread>
{

	@Override
	public TestNotificationThread createNotificationThreadInstance(int taskSize)
	{
		// TODO Auto-generated method stub
		return new TestNotificationThread(taskSize);
	}

}
