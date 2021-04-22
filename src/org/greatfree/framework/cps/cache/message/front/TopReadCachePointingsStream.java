package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/05/2018, Bing Li
public class TopReadCachePointingsStream extends OutMessageStream<TopReadCachePointingsRequest>
{

	public TopReadCachePointingsStream(ObjectOutputStream out, Lock lock, TopReadCachePointingsRequest message)
	{
		super(out, lock, message);
	}

}
