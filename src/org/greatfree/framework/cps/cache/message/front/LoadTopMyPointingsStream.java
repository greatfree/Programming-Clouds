package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 07/13/2018, Bing Li
public class LoadTopMyPointingsStream extends MessageStream<LoadTopMyPointingsRequest>
{

	public LoadTopMyPointingsStream(ObjectOutputStream out, Lock lock, LoadTopMyPointingsRequest message)
	{
		super(out, lock, message);
	}

}
