package ca.threetier.ecom.businesslogic;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.dp.tncs.message.PlaceOrderNotification;

// Created: 03/09/2020, Bing Li
class PlaceOrderNotificationThreadCreator implements NotificationThreadCreatable<PlaceOrderNotification, PlaceOrderNotificationThread>
{

	@Override
	public PlaceOrderNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new PlaceOrderNotificationThread(taskSize);
	}

}
