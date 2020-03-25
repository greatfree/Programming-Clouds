package org.greatfree.dip.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/13/2018, Bing Li
public class PeekSingleMyStoreDataQueueStream extends OutMessageStream<PeekSingleMyStoreDataQueueRequest>
{

	public PeekSingleMyStoreDataQueueStream(ObjectOutputStream out, Lock lock, PeekSingleMyStoreDataQueueRequest message)
	{
		super(out, lock, message);
	}

}
