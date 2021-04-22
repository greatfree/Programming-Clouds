package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/24/2018, Bing Li
public class CachePointingByKeyStream extends OutMessageStream<CachePointingByKeyRequest>
{

	public CachePointingByKeyStream(ObjectOutputStream out, Lock lock, CachePointingByKeyRequest message)
	{
		super(out, lock, message);
	}

}
