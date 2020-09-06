package org.greatfree.dsf.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/13/2018, Bing Li
public class LoadTopMyPointingsStream extends OutMessageStream<LoadTopMyPointingsRequest>
{

	public LoadTopMyPointingsStream(ObjectOutputStream out, Lock lock, LoadTopMyPointingsRequest message)
	{
		super(out, lock, message);
	}

}
