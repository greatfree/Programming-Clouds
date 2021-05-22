package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 08/25/2018, Bing Li
public class LoadMapStoreDataKeysStream extends MessageStream<LoadMapStoreDataKeysRequest>
{

	public LoadMapStoreDataKeysStream(ObjectOutputStream out, Lock lock, LoadMapStoreDataKeysRequest message)
	{
		super(out, lock, message);
	}

}
