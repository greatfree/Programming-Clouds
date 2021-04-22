package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.message.multicast.RPMulticastResponse;

// Created: 10/22/2018, Bing Li
public class ChildRPMulticastResponseThreadCreator implements NotificationThreadCreatable<RPMulticastResponse, ChildRPMulticastResponseThread>
{

	@Override
	public ChildRPMulticastResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new ChildRPMulticastResponseThread(taskSize);
	}

}
