package org.greatfree.testing.cluster.dn;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.NodeKeyNotification;

/*
 * The code here attempts to create instances of RegisterThread. It is used by the notification dispatcher. 11/25/2014, Bing Li
 */

// Created: 11/23/2016, Bing Li
public class RegisterThreadCreator implements NotificationQueueCreator<NodeKeyNotification, RegisterThread>
{
	// Create the instance of RegisterThread. 11/25/2014, Bing Li
	@Override
	public RegisterThread createInstance(int taskSize)
	{
		return new RegisterThread(taskSize);
	}

}
