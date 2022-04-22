package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 08/09/2018, Bing Li
public class PopSingleMyStoreDataStream extends MessageStream<PopSingleMyStoreDataRequest>
{

	public PopSingleMyStoreDataStream(ObjectOutputStream out, Lock lock, PopSingleMyStoreDataRequest message)
	{
		super(out, lock, message);
	}

}
