package com.greatfree.concurrency;

import com.greatfree.multicast.ServerMessage;

/*
 * The interface defines the method signature to create the instances of NotificationQueue. 11/04/2014, Bing Li
 */

// Created: 11/04/2014, Bing Li
public interface NotificationThreadCreatable<Notification extends ServerMessage, NotificationThread extends NotificationQueue<Notification>>
{
	public NotificationThread createNotificationThreadInstance(int taskSize);
}
