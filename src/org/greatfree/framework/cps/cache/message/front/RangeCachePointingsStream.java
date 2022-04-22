package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 07/24/2018, Bing Li
public class RangeCachePointingsStream extends MessageStream<RangeCachePointingsRequest>
{

	public RangeCachePointingsStream(ObjectOutputStream out, Lock lock, RangeCachePointingsRequest message)
	{
		super(out, lock, message);
	}

}
