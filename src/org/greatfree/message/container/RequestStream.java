package org.greatfree.message.container;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 12/18/2018, Bing Li
public class RequestStream extends MessageStream<Request>
{

	public RequestStream(ObjectOutputStream out, Lock lock, Request message)
	{
		super(out, lock, message);
	}

}
