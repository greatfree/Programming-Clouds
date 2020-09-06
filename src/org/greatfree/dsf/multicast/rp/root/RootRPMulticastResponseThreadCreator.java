package org.greatfree.dsf.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.multicast.RPMulticastResponse;

// Created: 10/22/2018, Bing Li
public class RootRPMulticastResponseThreadCreator implements NotificationThreadCreatable<RPMulticastResponse, RootRPMulticastResponseThread>
{

	@Override
	public RootRPMulticastResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new RootRPMulticastResponseThread(taskSize);
	}

}
