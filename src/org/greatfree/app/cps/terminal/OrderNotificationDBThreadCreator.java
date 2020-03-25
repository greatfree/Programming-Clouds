package org.greatfree.app.cps.terminal;

import org.greatfree.app.cps.message.OrderNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 08/14/2018, Bing Li
public class OrderNotificationDBThreadCreator implements NotificationThreadCreatable<OrderNotification, OrderNotificationDBThread>
{

	@Override
	public OrderNotificationDBThread createNotificationThreadInstance(int taskSize)
	{
		return new OrderNotificationDBThread(taskSize);
	}

}
