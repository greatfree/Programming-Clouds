package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 08/05/2018, Bing Li
public class IsCacheReadExistedInPointingStoreStream extends OutMessageStream<IsCacheReadExistedInPointingStoreRequest>
{

	public IsCacheReadExistedInPointingStoreStream(ObjectOutputStream out, Lock lock, IsCacheReadExistedInPointingStoreRequest message)
	{
		super(out, lock, message);
	}

}
