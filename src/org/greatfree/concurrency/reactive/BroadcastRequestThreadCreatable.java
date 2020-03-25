package org.greatfree.concurrency.reactive;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.message.abandoned.BroadcastRequest;
import org.greatfree.message.abandoned.BroadcastResponse;

/*
 * The interface to define the method to create a thread for processing broadcast requests concurrently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public interface BroadcastRequestThreadCreatable<Request extends BroadcastRequest, Response extends BroadcastResponse, RequestThread extends BroadcastRequestQueue<Request, Response>>
{
	public RequestThread createRequestThreadInstance(IPResource ipPort, FreeClientPool pool, int taskSize);
}
