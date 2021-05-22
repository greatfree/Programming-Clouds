package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 07/20/2018, Bing Li
public class LoadMyPointingMapStream extends MessageStream<LoadMyPointingMapRequest>
{

	public LoadMyPointingMapStream(ObjectOutputStream out, Lock lock, LoadMyPointingMapRequest message)
	{
		super(out, lock, message);
	}

}
