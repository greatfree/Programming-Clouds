package org.greatfree.framework.cps.cache.message.prefetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/13/2018, Bing Li
public class DequeueMyStoreDataStream extends OutMessageStream<DequeueMyStoreDataRequest>
{

	public DequeueMyStoreDataStream(ObjectOutputStream out, Lock lock, DequeueMyStoreDataRequest message)
	{
		super(out, lock, message);
	}

}
