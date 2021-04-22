package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/24/2018, Bing Li
public class TopCachePointingsStream extends OutMessageStream<TopCachePointingsRequest>
{

	public TopCachePointingsStream(ObjectOutputStream out, Lock lock, TopCachePointingsRequest message)
	{
		super(out, lock, message);
	}

}
