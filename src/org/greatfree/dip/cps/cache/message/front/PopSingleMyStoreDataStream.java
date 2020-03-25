package org.greatfree.dip.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/09/2018, Bing Li
public class PopSingleMyStoreDataStream extends OutMessageStream<PopSingleMyStoreDataRequest>
{

	public PopSingleMyStoreDataStream(ObjectOutputStream out, Lock lock, PopSingleMyStoreDataRequest message)
	{
		super(out, lock, message);
	}

}
