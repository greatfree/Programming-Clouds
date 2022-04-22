package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 02/28/2019, Bing Li
public class LoadRangeMyUKsStream extends MessageStream<LoadRangeMyUKsRequest>
{

	public LoadRangeMyUKsStream(ObjectOutputStream out, Lock lock, LoadRangeMyUKsRequest message)
	{
		super(out, lock, message);
	}

}
