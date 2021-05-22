package org.greatfree.app.cs.twonode.server;

import org.greatfree.app.cs.twonode.message.OrderDecisionNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 07/27/2018, Bing Li
class OrderDecisionNotificationThreadCreator implements NotificationQueueCreator<OrderDecisionNotification, OrderDecisionNotificationThread>
{

	@Override
	public OrderDecisionNotificationThread createInstance(int taskSize)
	{
		return new OrderDecisionNotificationThread(taskSize);
	}

}
