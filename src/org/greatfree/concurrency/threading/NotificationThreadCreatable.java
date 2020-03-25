package org.greatfree.concurrency.threading;

import org.greatfree.message.ServerMessage;

// Created: 09/10/2019, Bing Li
public interface NotificationThreadCreatable<Notification extends ServerMessage, NotificationThread extends NotificationQueue<Notification>>
{
	public NotificationThread createNotificationThreadInstance(int taskSize);
}
