package org.greatfree.dsf.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/05/2018, Bing Li
public class RangeReadCachePointingsStream extends OutMessageStream<RangeReadCachePointingsRequest>
{

	public RangeReadCachePointingsStream(ObjectOutputStream out, Lock lock, RangeReadCachePointingsRequest message)
	{
		super(out, lock, message);
	}

}