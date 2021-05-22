package org.greatfree.framework.p2p.peer;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.p2p.message.AddPartnerNotification;

// Created: 05/02/2017, Bing Li
class AddPartnerThreadCreator implements NotificationQueueCreator<AddPartnerNotification, AddPartnerThread>
{

	@Override
	public AddPartnerThread createInstance(int taskSize)
	{
		// TODO Auto-generated method stub
		return new AddPartnerThread(taskSize);
	}

}
