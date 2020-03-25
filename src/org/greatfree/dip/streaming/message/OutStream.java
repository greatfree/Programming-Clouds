package org.greatfree.dip.streaming.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 03/21/2020, Bing Li
public class OutStream extends OutMessageStream<StreamRequest>
{

	public OutStream(ObjectOutputStream out, Lock lock, StreamRequest message)
	{
		super(out, lock, message);
	}

}
