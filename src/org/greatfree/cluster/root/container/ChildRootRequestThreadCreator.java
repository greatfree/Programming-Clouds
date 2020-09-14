package org.greatfree.cluster.root.container;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ChildRootStream;

// Created: 09/14/2020, Bing Li
class ChildRootRequestThreadCreator implements RequestThreadCreatable<ChildRootRequest, ChildRootStream, ChildRootResponse, ChildRootRequestThread>
{

	@Override
	public ChildRootRequestThread createRequestThreadInstance(int taskSize)
	{
		return new ChildRootRequestThread(taskSize);
	}

}
