package org.greatfree.app.search.dip.multicast.root.entry;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 12/10/2018, Bing Li
class ShutdownSearchEntryThreadCreator implements NotificationQueueCreator<ShutdownServerNotification, ShutdownSearchEntryThread>
{

	@Override
	public ShutdownSearchEntryThread createInstance(int taskSize)
	{
		return new ShutdownSearchEntryThread(taskSize);
	}

}
