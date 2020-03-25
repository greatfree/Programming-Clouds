package org.greatfree.concurrency.reactive;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.message.abandoned.AnycastRequest;
import org.greatfree.message.abandoned.AnycastResponse;

/*
 * The interface to define the method to create a thread for processing anycast requests concurrently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public interface AnycastRequestThreadCreatable<Request extends AnycastRequest, Response extends AnycastResponse, RequestThread extends AnycastRequestQueue<Request, Response>>
{
	public RequestThread createRequestThreadInstance(IPResource ipPort, FreeClientPool pool, int taskSize);
}
