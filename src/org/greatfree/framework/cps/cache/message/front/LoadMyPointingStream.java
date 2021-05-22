package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 07/13/2018, Bing Li
public class LoadMyPointingStream extends MessageStream<LoadMyPointingRequest>
{

	public LoadMyPointingStream(ObjectOutputStream out, Lock lock, LoadMyPointingRequest message)
	{
		super(out, lock, message);
	}

}
