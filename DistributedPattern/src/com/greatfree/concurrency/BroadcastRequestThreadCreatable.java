package com.greatfree.concurrency;

import com.greatfree.multicast.BroadcastRequest;
import com.greatfree.multicast.BroadcastResponse;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.remote.IPPort;

/*
 * The interface to define the method to create a thread for processing broadcast requests concurrently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public interface BroadcastRequestThreadCreatable<Request extends BroadcastRequest, Response extends BroadcastResponse, RequestThread extends BroadcastRequestQueue<Request, Response>>
{
	public RequestThread createRequestThreadInstance(IPPort ipPort, FreeClientPool pool, int taskSize);
}
