package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 07/09/2018, Bing Li
public class LoadMyDataStream extends MessageStream<LoadMyDataRequest>
{

	public LoadMyDataStream(ObjectOutputStream out, Lock lock, LoadMyDataRequest message)
	{
		super(out, lock, message);
	}

}
