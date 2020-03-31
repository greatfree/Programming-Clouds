package ca.threetier.ecom.db;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.dp.tncs.message.PlaceOrderNotification;

// Created: 02/22/2020, Bing Lis
class PlaceOrderNotificationThreadCreator implements NotificationThreadCreatable<PlaceOrderNotification, PlaceOrderNotificationThread>
{

	@Override
	public PlaceOrderNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new PlaceOrderNotificationThread(taskSize);
	}

}
