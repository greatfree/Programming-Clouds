package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 07/20/2018, Bing Li
public class LoadMinMyPointingStream extends MessageStream<LoadMinMyPointingRequest>
{

	public LoadMinMyPointingStream(ObjectOutputStream out, Lock lock, LoadMinMyPointingRequest message)
	{
		super(out, lock, message);
	}

}
