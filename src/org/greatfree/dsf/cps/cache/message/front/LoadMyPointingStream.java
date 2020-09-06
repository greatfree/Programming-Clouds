package org.greatfree.dsf.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/13/2018, Bing Li
public class LoadMyPointingStream extends OutMessageStream<LoadMyPointingRequest>
{

	public LoadMyPointingStream(ObjectOutputStream out, Lock lock, LoadMyPointingRequest message)
	{
		super(out, lock, message);
	}

}
