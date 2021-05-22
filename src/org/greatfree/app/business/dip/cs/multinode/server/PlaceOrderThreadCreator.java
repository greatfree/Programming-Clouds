package org.greatfree.app.business.dip.cs.multinode.server;

import org.greatfree.chat.message.cs.business.PlaceOrderNotification;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 12/09/2017, Bing Li
public class PlaceOrderThreadCreator implements NotificationQueueCreator<PlaceOrderNotification, PlaceOrderThread>
{

	@Override
	public PlaceOrderThread createInstance(int taskSize)
	{
		return new PlaceOrderThread(taskSize);
	}

}
