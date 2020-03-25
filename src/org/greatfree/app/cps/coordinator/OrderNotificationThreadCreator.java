package org.greatfree.app.cps.coordinator;

import org.greatfree.app.cps.message.OrderNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 08/14/2018, Bing Li
public class OrderNotificationThreadCreator implements NotificationThreadCreatable<OrderNotification, OrderNotificationThread>
{

	@Override
	public OrderNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new OrderNotificationThread(taskSize);
	}

}
