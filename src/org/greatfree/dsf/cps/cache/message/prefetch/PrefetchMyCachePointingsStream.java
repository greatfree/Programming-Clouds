package org.greatfree.dsf.cps.cache.message.prefetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/24/2018, Bing Li
public class PrefetchMyCachePointingsStream extends OutMessageStream<PrefetchMyCachePointingsRequest>
{

	public PrefetchMyCachePointingsStream(ObjectOutputStream out, Lock lock, PrefetchMyCachePointingsRequest message)
	{
		super(out, lock, message);
	}

}
