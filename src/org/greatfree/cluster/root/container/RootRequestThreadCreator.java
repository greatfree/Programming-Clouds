package org.greatfree.cluster.root.container;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.RequestStream;
import org.greatfree.message.multicast.container.Response;

// Created: 01/13/2019, Bing Li
class RootRequestThreadCreator implements RequestThreadCreatable<Request, RequestStream, Response, RootRequestThread>
{

	@Override
	public RootRequestThread createRequestThreadInstance(int taskSize)
	{
		return new RootRequestThread(taskSize);
	}

}
