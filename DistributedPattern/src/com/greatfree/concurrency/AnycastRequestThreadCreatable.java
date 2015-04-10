package com.greatfree.concurrency;

import com.greatfree.multicast.AnycastRequest;
import com.greatfree.multicast.AnycastResponse;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.remote.IPPort;

/*
 * The interface to define the method to create a thread for processing anycast requests concurrently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public interface AnycastRequestThreadCreatable<Request extends AnycastRequest, Response extends AnycastResponse, RequestThread extends AnycastRequestQueue<Request, Response>>
{
	public RequestThread createRequestThreadInstance(IPPort ipPort, FreeClientPool pool, int taskSize);
}
