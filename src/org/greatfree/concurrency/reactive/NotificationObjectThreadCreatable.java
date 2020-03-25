package org.greatfree.concurrency.reactive;

// Created: 02/07/2016, Bing Li
public interface NotificationObjectThreadCreatable<Notification, NotificationThread extends NotificationObjectQueue<Notification>>
{
	public NotificationThread createNotificationThreadInstance(int taskSize);
}
