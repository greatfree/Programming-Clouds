package org.greatfree.concurrency.reactive;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.concurrency.MessageBindable;
import org.greatfree.message.ServerMessage;

/*
 * The interface defines the method to create the bound broadcast request thread. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public interface BoundBroadcastRequestThreadCreatable<Request extends ServerMessage, Response extends ServerMessage, RequestBinder extends MessageBindable<Request>, RequestThread extends BoundRequestQueue<Request, Response, RequestBinder>>
{
	public RequestThread createRequestThreadInstance(IPResource ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, RequestBinder reqBinder);
}
