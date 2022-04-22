package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 07/13/2018, Bing Li
public class LoadRangeMyPointingsStream extends MessageStream<LoadRangeMyPointingsRequest>
{

	public LoadRangeMyPointingsStream(ObjectOutputStream out, Lock lock, LoadRangeMyPointingsRequest message)
	{
		super(out, lock, message);
	}

}
