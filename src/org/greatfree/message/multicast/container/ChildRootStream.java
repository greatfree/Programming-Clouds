package org.greatfree.message.multicast.container;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 09/14/2020, Bing Li
public class ChildRootStream extends MessageStream<ChildRootRequest>
{

	public ChildRootStream(ObjectOutputStream out, Lock lock, ChildRootRequest message)
	{
		super(out, lock, message);
	}

}
