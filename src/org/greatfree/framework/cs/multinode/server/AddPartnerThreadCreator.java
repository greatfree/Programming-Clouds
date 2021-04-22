package org.greatfree.framework.cs.multinode.server;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.cs.multinode.message.AddPartnerNotification;

// Created: 04/23/2017, Bing Li
class AddPartnerThreadCreator implements NotificationThreadCreatable<AddPartnerNotification, AddPartnerThread>
{

	@Override
	public AddPartnerThread createNotificationThreadInstance(int taskSize)
	{
		return new AddPartnerThread(taskSize);
	}

}
