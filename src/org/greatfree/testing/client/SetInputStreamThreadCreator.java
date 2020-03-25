package org.greatfree.testing.client;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.InitReadFeedbackNotification;

/*
 * The code here attempts to create instances of SetInputStreamThread. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public class SetInputStreamThreadCreator implements NotificationThreadCreatable<InitReadFeedbackNotification, SetInputStreamThread>
{
	// Create the instance of SetInputStreamThread. 11/09/2014, Bing Li
	@Override
	public SetInputStreamThread createNotificationThreadInstance(int taskSize)
	{
		return new SetInputStreamThread(taskSize);
	}
}
