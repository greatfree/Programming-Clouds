package org.greatfree.framework.cs.multinode.server;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.cs.multinode.message.AddPartnerNotification;

// Created: 04/23/2017, Bing Li
class AddPartnerThreadCreator implements NotificationQueueCreator<AddPartnerNotification, AddPartnerThread>
{

	@Override
	public AddPartnerThread createInstance(int taskSize)
	{
		return new AddPartnerThread(taskSize);
	}

}
