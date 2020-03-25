package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.PlaceOrderNotification;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 12/09/2017, Bing Li
public class PlaceOrderThreadCreator implements NotificationThreadCreatable<PlaceOrderNotification, PlaceOrderThread>
{

	@Override
	public PlaceOrderThread createNotificationThreadInstance(int taskSize)
	{
		return new PlaceOrderThread(taskSize);
	}

}
