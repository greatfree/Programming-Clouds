package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 07/24/2018, Bing Li
public class CachePointingByIndexStream extends MessageStream<CachePointingByIndexRequest>
{

	public CachePointingByIndexStream(ObjectOutputStream out, Lock lock, CachePointingByIndexRequest message)
	{
		super(out, lock, message);
	}

}
