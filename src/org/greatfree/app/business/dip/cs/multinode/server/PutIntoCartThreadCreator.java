package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.PutIntoCartNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 12/07/2017, Bing Li
public class PutIntoCartThreadCreator implements NotificationThreadCreatable<PutIntoCartNotification, PutIntoCartThread>
{

	@Override
	public PutIntoCartThread createNotificationThreadInstance(int taskSize)
	{
		return new PutIntoCartThread(taskSize);
	}
}
