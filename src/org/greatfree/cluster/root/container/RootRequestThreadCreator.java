package org.greatfree.cluster.root.container;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.RequestStream;
import org.greatfree.message.multicast.container.Response;

// Created: 01/13/2019, Bing Li
class RootRequestThreadCreator implements RequestQueueCreator<Request, RequestStream, Response, RootRequestThread>
{

	@Override
	public RootRequestThread createInstance(int taskSize)
	{
		return new RootRequestThread(taskSize);
	}

}
