package org.greatfree.cluster.root;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.RequestStream;
import org.greatfree.message.multicast.container.Response;

// Created: 09/23/2018, Bing Li
class RootRequestThreadCreator implements RequestQueueCreator<Request, RequestStream, Response, RootRequestThread>
{

	@Override
	public RootRequestThread createInstance(int taskSize)
	{
		return new RootRequestThread(taskSize);
	}

}
