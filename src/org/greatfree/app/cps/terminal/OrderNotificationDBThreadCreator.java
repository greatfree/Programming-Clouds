package org.greatfree.app.cps.terminal;

import org.greatfree.app.cps.message.OrderNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 08/14/2018, Bing Li
public class OrderNotificationDBThreadCreator implements NotificationQueueCreator<OrderNotification, OrderNotificationDBThread>
{

	@Override
	public OrderNotificationDBThread createInstance(int taskSize)
	{
		return new OrderNotificationDBThread(taskSize);
	}

}
