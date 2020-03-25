package org.greatfree.app.cs.twonode.server;

import org.greatfree.app.cs.twonode.message.OrderDecisionNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 07/27/2018, Bing Li
class OrderDecisionNotificationThreadCreator implements NotificationThreadCreatable<OrderDecisionNotification, OrderDecisionNotificationThread>
{

	@Override
	public OrderDecisionNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new OrderDecisionNotificationThread(taskSize);
	}

}
