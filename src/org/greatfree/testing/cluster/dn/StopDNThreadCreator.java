package org.greatfree.testing.cluster.dn;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.StopDNMultiNotification;

/*
 * The code here attempts to create instances of StopDNMultiNotificationThread. It works with the notification dispatcher. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNThreadCreator implements NotificationQueueCreator<StopDNMultiNotification, StopDNThread>
{

	@Override
	public StopDNThread createInstance(int taskSize)
	{
		return new StopDNThread(taskSize);
	}

}
