package org.greatfree.dip.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/13/2018, Bing Li
public class LoadRangeMyPointingsStream extends OutMessageStream<LoadRangeMyPointingsRequest>
{

	public LoadRangeMyPointingsStream(ObjectOutputStream out, Lock lock, LoadRangeMyPointingsRequest message)
	{
		super(out, lock, message);
	}

}
