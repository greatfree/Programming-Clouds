package org.greatfree.dip.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 07/09/2018, Bing Li
public class LoadMyDataStream extends OutMessageStream<LoadMyDataRequest>
{

	public LoadMyDataStream(ObjectOutputStream out, Lock lock, LoadMyDataRequest message)
	{
		super(out, lock, message);
	}

}
