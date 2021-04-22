package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/24/2018, Bing Li
public class IsCacheEmptyInPointingStoreStream extends OutMessageStream<IsCacheEmptyInPointingStoreRequest>
{

	public IsCacheEmptyInPointingStoreStream(ObjectOutputStream out, Lock lock, IsCacheEmptyInPointingStoreRequest message)
	{
		super(out, lock, message);
	}

}
