package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 08/05/2018, Bing Li
public class RangeReadCachePointingsStream extends MessageStream<RangeReadCachePointingsRequest>
{

	public RangeReadCachePointingsStream(ObjectOutputStream out, Lock lock, RangeReadCachePointingsRequest message)
	{
		super(out, lock, message);
	}

}
