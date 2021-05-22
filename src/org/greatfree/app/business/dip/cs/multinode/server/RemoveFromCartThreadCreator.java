package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.RemoveFromCartNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 12/07/2017, Bing Li
public class RemoveFromCartThreadCreator implements NotificationQueueCreator<RemoveFromCartNotification, RemoveFromCartThread>
{

	@Override
	public RemoveFromCartThread createInstance(int taskSize)
	{
		return new RemoveFromCartThread(taskSize);
	}

}
