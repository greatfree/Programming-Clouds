package org.greatfree.app.business.cs.multinode.server;

import org.greatfree.chat.message.cs.business.PutIntoCartNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 12/07/2017, Bing Li
public class PutIntoCartThreadCreator implements NotificationQueueCreator<PutIntoCartNotification, PutIntoCartThread>
{

	@Override
	public PutIntoCartThread createInstance(int taskSize)
	{
		return new PutIntoCartThread(taskSize);
	}
}
