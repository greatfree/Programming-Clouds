package com.greatfree.testing.memory;

import com.greatfree.concurrency.AnycastRequestThreadCreatable;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.remote.IPPort;
import com.greatfree.testing.message.IsPublisherExistedAnycastRequest;
import com.greatfree.testing.message.IsPublisherExistedAnycastResponse;

/*
 * The creator initializes an instance of IsPublisherExistedThread. It is invoked by the AnycastRequestDispatcher to create new threads to process the requests concurrently and efficiently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedThreadCreator implements AnycastRequestThreadCreatable<IsPublisherExistedAnycastRequest, IsPublisherExistedAnycastResponse, IsPublisherExistedThread>
{
	@Override
	public IsPublisherExistedThread createRequestThreadInstance(IPPort ipPort, FreeClientPool pool, int taskSize)
	{
		return new IsPublisherExistedThread(ipPort, pool, taskSize);
	}
}
