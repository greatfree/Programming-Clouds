package com.greatfree.concurrency;

import com.greatfree.multicast.BroadcastRequest;
import com.greatfree.multicast.BroadcastResponse;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.remote.IPPort;

/*
 * The interface defines the method to create the bound broadcast request thread. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public interface BoundBroadcastRequestThreadCreatable<Request extends BroadcastRequest, Response extends BroadcastResponse, RequestBinder extends MessageBindable<Request>, RequestThread extends BoundBroadcastRequestQueue<Request, Response, RequestBinder>>
{
	public RequestThread createRequestThreadInstance(IPPort ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, RequestBinder reqBinder);
}
