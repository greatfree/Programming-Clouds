package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 07/24/2018, Bing Li
public class IsCacheEmptyInPointingStoreStream extends MessageStream<IsCacheEmptyInPointingStoreRequest>
{

	public IsCacheEmptyInPointingStoreStream(ObjectOutputStream out, Lock lock, IsCacheEmptyInPointingStoreRequest message)
	{
		super(out, lock, message);
	}

}
