package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/09/2018, Bing Li
public class PeekSingleMyStoreDataStream extends OutMessageStream<PeekSingleMyStoreDataRequest>
{

	public PeekSingleMyStoreDataStream(ObjectOutputStream out, Lock lock, PeekSingleMyStoreDataRequest message)
	{
		super(out, lock, message);
	}

}
