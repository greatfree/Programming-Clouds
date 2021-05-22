package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 07/20/2018, Bing Li
public class LoadMaxMyPointingStream extends MessageStream<LoadMaxMyPointingRequest>
{

	public LoadMaxMyPointingStream(ObjectOutputStream out, Lock lock, LoadMaxMyPointingRequest message)
	{
		super(out, lock, message);
	}

}
