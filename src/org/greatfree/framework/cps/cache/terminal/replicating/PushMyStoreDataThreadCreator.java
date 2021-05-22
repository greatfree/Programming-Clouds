package org.greatfree.framework.cps.cache.terminal.replicating;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cps.cache.message.replicate.PushMyStoreDataNotification;

// Created: 08/09/2018, Bing Li
public class PushMyStoreDataThreadCreator implements NotificationQueueCreator<PushMyStoreDataNotification, PushMyStoreDataThread>
{

	@Override
	public PushMyStoreDataThread createInstance(int taskSize)
	{
		return new PushMyStoreDataThread(taskSize);
	}

}
