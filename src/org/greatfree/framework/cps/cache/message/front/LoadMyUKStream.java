package org.greatfree.framework.cps.cache.message.front;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 02/28/2019, Bing Li
public class LoadMyUKStream extends MessageStream<LoadMyUKRequest>
{

	public LoadMyUKStream(ObjectOutputStream out, Lock lock, LoadMyUKRequest message)
	{
		super(out, lock, message);
	}

}
