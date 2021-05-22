package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 08/09/2018, Bing Li
public class PeekMyStoreDataStream extends MessageStream<PeekMyStoreDataRequest>
{

	public PeekMyStoreDataStream(ObjectOutputStream out, Lock lock, PeekMyStoreDataRequest message)
	{
		super(out, lock, message);
	}

}
