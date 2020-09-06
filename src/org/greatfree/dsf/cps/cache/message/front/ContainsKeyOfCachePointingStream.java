package org.greatfree.dsf.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/24/2018, Bing Li
public class ContainsKeyOfCachePointingStream extends OutMessageStream<ContainsKeyOfCachePointingRequest>
{

	public ContainsKeyOfCachePointingStream(ObjectOutputStream out, Lock lock, ContainsKeyOfCachePointingRequest message)
	{
		super(out, lock, message);
	}

}
