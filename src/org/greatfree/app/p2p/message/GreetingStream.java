package org.greatfree.app.p2p.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 08/19/2018, Bing Li
public class GreetingStream extends MessageStream<GreetingRequest>
{

	public GreetingStream(ObjectOutputStream out, Lock lock, GreetingRequest message)
	{
		super(out, lock, message);
	}

}
