package edu.greatfree.notification.server;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import edu.greatfree.notification.message.MyNotification;

class MyNotificationThreadCreator implements NotificationThreadCreatable<MyNotification, MyNotificationThread>
{

	@Override
	public MyNotificationThread createNotificationThreadInstance(int size)
	{
		return new MyNotificationThread(size);
	}

}
