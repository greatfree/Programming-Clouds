package org.greatfree.framework.multicast.rp.child;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.RPMulticastResponse;

// Created: 10/22/2018, Bing Li
public class ChildRPMulticastResponseThreadCreator implements NotificationQueueCreator<RPMulticastResponse, ChildRPMulticastResponseThread>
{

	@Override
	public ChildRPMulticastResponseThread createInstance(int taskSize)
	{
		return new ChildRPMulticastResponseThread(taskSize);
	}

}
