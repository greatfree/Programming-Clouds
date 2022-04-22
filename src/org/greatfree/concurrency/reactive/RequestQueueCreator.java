package org.greatfree.concurrency.reactive;

import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;

/*
 * The interface defines a method signature that creates a thread to respond users' requests. 11/04/2014, Bing Li
 */

// Created: 11/04/2014, Bing Li
public interface RequestQueueCreator<Request extends ServerMessage, Stream extends MessageStream<Request>, Response extends ServerMessage, RequestThread extends RequestQueue<Request, Stream, Response>>
{
	// The method signature to create an instance of RequestThread to respond users' requests. 11/04/2014, Bing Li
	public RequestThread createInstance(int taskSize);
}
