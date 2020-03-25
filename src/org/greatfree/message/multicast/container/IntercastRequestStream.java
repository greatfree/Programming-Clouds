package org.greatfree.message.multicast.container;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 01/26/2019, Bing Li
public class IntercastRequestStream extends OutMessageStream<IntercastRequest>
{

	public IntercastRequestStream(ObjectOutputStream out, Lock lock, IntercastRequest message)
	{
		super(out, lock, message);
	}

}
