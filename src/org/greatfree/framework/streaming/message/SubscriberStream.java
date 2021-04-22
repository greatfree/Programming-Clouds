package org.greatfree.framework.streaming.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 03/23/2020, Bing Li
public class SubscriberStream extends OutMessageStream<SubscriberRequest>
{

	public SubscriberStream(ObjectOutputStream out, Lock lock, SubscriberRequest message)
	{
		super(out, lock, message);
	}

}
