package org.greatfree.dsf.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 02/28/2019, Bing Li
public class LoadMyUKStream extends OutMessageStream<LoadMyUKRequest>
{

	public LoadMyUKStream(ObjectOutputStream out, Lock lock, LoadMyUKRequest message)
	{
		super(out, lock, message);
	}

}
