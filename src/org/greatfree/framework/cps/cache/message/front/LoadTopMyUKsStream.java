package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 02/28/2019, Bing Li
public class LoadTopMyUKsStream extends OutMessageStream<LoadTopMyUKsRequest>
{

	public LoadTopMyUKsStream(ObjectOutputStream out, Lock lock, LoadTopMyUKsRequest message)
	{
		super(out, lock, message);
	}

}
