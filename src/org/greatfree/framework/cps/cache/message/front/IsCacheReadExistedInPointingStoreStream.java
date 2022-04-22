package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 08/05/2018, Bing Li
public class IsCacheReadExistedInPointingStoreStream extends MessageStream<IsCacheReadExistedInPointingStoreRequest>
{

	public IsCacheReadExistedInPointingStoreStream(ObjectOutputStream out, Lock lock, IsCacheReadExistedInPointingStoreRequest message)
	{
		super(out, lock, message);
	}

}
