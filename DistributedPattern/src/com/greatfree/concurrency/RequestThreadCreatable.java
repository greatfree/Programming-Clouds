package com.greatfree.concurrency;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;

/*
 * The interface defines a method signature that creates a thread to respond users' requests. 11/04/2014, Bing Li
 */

// Created: 11/04/2014, Bing Li
public interface RequestThreadCreatable<Request extends ServerMessage, Stream extends OutMessageStream<Request>, Response extends ServerMessage, RequestThread extends RequestQueue<Request, Stream, Response>>
{
	// The method signature to create an instance of RequestThread to respond users' requests. 11/04/2014, Bing Li
	public RequestThread createRequestThreadInstance(int taskSize);
}
