package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.message.multicast.RPMulticastResponse;

// Created: 10/22/2018, Bing Li
public class RootRPMulticastResponseThreadCreator implements NotificationQueueCreator<RPMulticastResponse, RootRPMulticastResponseThread>
{

	@Override
	public RootRPMulticastResponseThread createInstance(int taskSize)
	{
		return new RootRPMulticastResponseThread(taskSize);
	}

}
