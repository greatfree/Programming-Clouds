package org.greatfree.dip.cps.cache.message.prefetch;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/09/2018, Bing Li
public class PopMyStoreDataStream extends OutMessageStream<PopMyStoreDataRequest>
{

	public PopMyStoreDataStream(ObjectOutputStream out, Lock lock, PopMyStoreDataRequest message)
	{
		super(out, lock, message);
	}

}
