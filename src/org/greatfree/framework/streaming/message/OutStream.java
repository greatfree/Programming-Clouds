package org.greatfree.framework.streaming.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 03/21/2020, Bing Li
public class OutStream extends MessageStream<StreamRequest>
{

	public OutStream(ObjectOutputStream out, Lock lock, StreamRequest message)
	{
		super(out, lock, message);
	}

}
