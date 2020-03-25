package org.greatfree.app.search.dip.multicast.root.entry;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 12/10/2018, Bing Li
class ShutdownSearchEntryThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownSearchEntryThread>
{

	@Override
	public ShutdownSearchEntryThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownSearchEntryThread(taskSize);
	}

}
