package org.greatfree.dip.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/24/2018, Bing Li
public class CachePointingByIndexStream extends OutMessageStream<CachePointingByIndexRequest>
{

	public CachePointingByIndexStream(ObjectOutputStream out, Lock lock, CachePointingByIndexRequest message)
	{
		super(out, lock, message);
	}

}
