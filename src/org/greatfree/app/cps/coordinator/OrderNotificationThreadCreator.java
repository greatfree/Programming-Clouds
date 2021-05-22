package org.greatfree.app.cps.coordinator;

import org.greatfree.app.cps.message.OrderNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 08/14/2018, Bing Li
public class OrderNotificationThreadCreator implements NotificationQueueCreator<OrderNotification, OrderNotificationThread>
{

	@Override
	public OrderNotificationThread createInstance(int taskSize)
	{
		return new OrderNotificationThread(taskSize);
	}

}
