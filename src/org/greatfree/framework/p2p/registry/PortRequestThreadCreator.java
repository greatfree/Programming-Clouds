package org.greatfree.framework.p2p.registry;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.message.PortRequest;
import org.greatfree.message.PortResponse;
import org.greatfree.message.PortStream;

// Created: 05/02/2017, Bing Li
class PortRequestThreadCreator implements RequestQueueCreator<PortRequest, PortStream, PortResponse, PortRequestThread>
{

	@Override
	public PortRequestThread createInstance(int taskSize)
	{
		return new PortRequestThread(taskSize);
	}

}
