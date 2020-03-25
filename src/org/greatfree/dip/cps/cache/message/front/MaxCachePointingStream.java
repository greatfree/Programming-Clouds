package org.greatfree.dip.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/24/2018, Bing Li
public class MaxCachePointingStream extends OutMessageStream<MaxCachePointingRequest>
{

	public MaxCachePointingStream(ObjectOutputStream out, Lock lock, MaxCachePointingRequest message)
	{
		super(out, lock, message);
	}

}
