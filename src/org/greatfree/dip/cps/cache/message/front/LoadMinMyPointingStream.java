package org.greatfree.dip.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/20/2018, Bing Li
public class LoadMinMyPointingStream extends OutMessageStream<LoadMinMyPointingRequest>
{

	public LoadMinMyPointingStream(ObjectOutputStream out, Lock lock, LoadMinMyPointingRequest message)
	{
		super(out, lock, message);
	}

}
