package org.greatfree.testing.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 12/10/2016, Bing Li
public class TestStream extends MessageStream<TestRequest>
{

	public TestStream(ObjectOutputStream out, Lock lock, TestRequest message)
	{
		super(out, lock, message);
	}

}
