package org.greatfree.dsf.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/20/2018, Bing Li
public class LoadMyPointingMapStream extends OutMessageStream<LoadMyPointingMapRequest>
{

	public LoadMyPointingMapStream(ObjectOutputStream out, Lock lock, LoadMyPointingMapRequest message)
	{
		super(out, lock, message);
	}

}
