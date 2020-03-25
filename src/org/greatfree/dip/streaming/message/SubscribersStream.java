package org.greatfree.dip.streaming.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 03/20/2020, Bing Li
public class SubscribersStream extends OutMessageStream<SubscribersRequest>
{

	public SubscribersStream(ObjectOutputStream out, Lock lock, SubscribersRequest message)
	{
		super(out, lock, message);
	}

}
