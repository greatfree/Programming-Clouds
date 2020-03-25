package org.greatfree.server.container;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Request;
import org.greatfree.message.container.RequestStream;

// Created: 12/18/2018, Bing Li
class RequestThreadCreator implements RequestThreadCreatable<Request, RequestStream, ServerMessage, RequestThread>
{

	@Override
	public RequestThread createRequestThreadInstance(int taskSize)
	{
		return new RequestThread(taskSize);
	}

}
