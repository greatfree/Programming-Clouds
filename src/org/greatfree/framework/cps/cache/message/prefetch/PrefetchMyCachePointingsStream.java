package org.greatfree.framework.cps.cache.message.prefetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 07/24/2018, Bing Li
public class PrefetchMyCachePointingsStream extends MessageStream<PrefetchMyCachePointingsRequest>
{

	public PrefetchMyCachePointingsStream(ObjectOutputStream out, Lock lock, PrefetchMyCachePointingsRequest message)
	{
		super(out, lock, message);
	}

}
