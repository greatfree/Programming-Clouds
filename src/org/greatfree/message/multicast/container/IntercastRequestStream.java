package org.greatfree.message.multicast.container;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 01/26/2019, Bing Li
public class IntercastRequestStream extends MessageStream<IntercastRequest>
{

	public IntercastRequestStream(ObjectOutputStream out, Lock lock, IntercastRequest message)
	{
		super(out, lock, message);
	}

}
