package org.greatfree.dsf.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/13/2018, Bing Li
public class PeekMyStoreDataQueueStream extends OutMessageStream<PeekMyStoreDataQueueRequest>
{

	public PeekMyStoreDataQueueStream(ObjectOutputStream out, Lock lock, PeekMyStoreDataQueueRequest message)
	{
		super(out, lock, message);
	}

}
