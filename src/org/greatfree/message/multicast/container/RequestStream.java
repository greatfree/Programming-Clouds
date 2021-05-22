package org.greatfree.message.multicast.container;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 09/23/2018, Bing Li
public class RequestStream extends MessageStream<Request>
{

	public RequestStream(ObjectOutputStream out, Lock lock, Request message)
	{
		super(out, lock, message);
	}

}
