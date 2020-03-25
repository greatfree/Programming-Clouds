package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.testing.message.RegisterClientNotification;

/*
 * The creator here attempts to create instances of RegisterDNThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/23/2016, Bing Li
public class RegisterDNThreadCreator implements NotificationThreadCreatable<RegisterClientNotification, RegisterDNThread>
{

	@Override
	public RegisterDNThread createNotificationThreadInstance(int taskSize)
	{
		return new RegisterDNThread(taskSize);
	}

}
