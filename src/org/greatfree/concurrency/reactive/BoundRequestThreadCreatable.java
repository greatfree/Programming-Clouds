package org.greatfree.concurrency.reactive;

import org.greatfree.concurrency.MessageBindable;
import org.greatfree.message.ServerMessage;

/*
 * The interface defines the method to create the bound broadcast request thread. 11/29/2014, Bing Li
 */

// Created: 05/20/2017, Bing Li
public interface BoundRequestThreadCreatable<Request extends ServerMessage, Response extends ServerMessage, RequestBinder extends MessageBindable<Request>, RequestThread extends BoundRequestQueue<Request, Response, RequestBinder>>
{
	public RequestThread createRequestThreadInstance(int taskSize, String dispatcherKey, RequestBinder reqBinder);
}
