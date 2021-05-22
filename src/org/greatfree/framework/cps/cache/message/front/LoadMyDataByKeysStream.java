package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 07/21/2018, Bing Li
public class LoadMyDataByKeysStream extends MessageStream<LoadMyDataByKeysRequest>
{

	public LoadMyDataByKeysStream(ObjectOutputStream out, Lock lock, LoadMyDataByKeysRequest message)
	{
		super(out, lock, message);
	}

}
