package org.greatfree.framework.streaming.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 03/18/2020, Bing Li
public class SubscribeOutStream extends MessageStream<SubscribeStreamRequest>
{

	public SubscribeOutStream(ObjectOutputStream out, Lock lock, SubscribeStreamRequest message)
	{
		super(out, lock, message);
	}

}
