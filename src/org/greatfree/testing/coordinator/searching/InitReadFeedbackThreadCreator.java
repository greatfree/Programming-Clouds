package org.greatfree.testing.coordinator.searching;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.InitReadNotification;

/*
 * The code here attempts to create instances of SetInputStreamThread. 11/07/2014, Bing Li
 */

// Created: 11/09/2014, Bing Li
public class InitReadFeedbackThreadCreator implements NotificationQueueCreator<InitReadNotification, InitReadFeedbackThread>
{
	@Override
	public InitReadFeedbackThread createInstance(int taskSize)
	{
		return new InitReadFeedbackThread(taskSize);
	}
}
