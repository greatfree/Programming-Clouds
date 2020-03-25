package org.greatfree.cluster.root.container;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.IntercastRequestStream;
import org.greatfree.message.multicast.container.Response;

// Created: 03/04/2019, Bing Li
class IntercastRequestThreadCreator implements RequestThreadCreatable<IntercastRequest, IntercastRequestStream, Response, IntercastRequestThread>
{

	@Override
	public IntercastRequestThread createRequestThreadInstance(int taskSize)
	{
		return new IntercastRequestThread(taskSize);
	}

}

