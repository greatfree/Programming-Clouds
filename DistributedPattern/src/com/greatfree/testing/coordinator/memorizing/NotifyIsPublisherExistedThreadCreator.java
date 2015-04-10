package com.greatfree.testing.coordinator.memorizing;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.IsPublisherExistedAnycastResponse;

/*
 * The creator here attempts to create instances of NotifyIsPublisherExistedThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/28/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class NotifyIsPublisherExistedThreadCreator implements NotificationThreadCreatable<IsPublisherExistedAnycastResponse, NotifyIsPublisherExistedThread>
{
	@Override
	public NotifyIsPublisherExistedThread createNotificationThreadInstance(int taskSize)
	{
		return new NotifyIsPublisherExistedThread(taskSize);
	}
}
