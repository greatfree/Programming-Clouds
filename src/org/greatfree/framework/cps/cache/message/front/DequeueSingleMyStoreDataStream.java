package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 08/13/2018, Bing Li
public class DequeueSingleMyStoreDataStream extends MessageStream<DequeueSingleMyStoreDataRequest>
{

	public DequeueSingleMyStoreDataStream(ObjectOutputStream out, Lock lock, DequeueSingleMyStoreDataRequest message)
	{
		super(out, lock, message);
	}

}
