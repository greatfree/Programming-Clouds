package org.greatfree.framework.cps.cache.message.prefetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 07/11/2018, Bing Li
public class PrefetchMyPointingsStream extends MessageStream<PrefetchMyPointingsRequest>
{

	public PrefetchMyPointingsStream(ObjectOutputStream out, Lock lock, PrefetchMyPointingsRequest message)
	{
		super(out, lock, message);
	}

}
