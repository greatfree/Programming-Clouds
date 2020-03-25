package org.greatfree.testing.memory;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.concurrency.reactive.AnycastRequestThreadCreatable;
import org.greatfree.testing.message.IsPublisherExistedAnycastRequest;
import org.greatfree.testing.message.IsPublisherExistedAnycastResponse;

/*
 * The creator initializes an instance of IsPublisherExistedThread. It is invoked by the AnycastRequestDispatcher to create new threads to process the requests concurrently and efficiently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedThreadCreator implements AnycastRequestThreadCreatable<IsPublisherExistedAnycastRequest, IsPublisherExistedAnycastResponse, IsPublisherExistedThread>
{
	@Override
	public IsPublisherExistedThread createRequestThreadInstance(IPResource ipPort, FreeClientPool pool, int taskSize)
	{
		return new IsPublisherExistedThread(ipPort, pool, taskSize);
	}
}
