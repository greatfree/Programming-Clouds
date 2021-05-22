package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 12/11/2017, Bing Li
public class CheckCartStream extends MessageStream<CheckCartRequest>
{

	public CheckCartStream(ObjectOutputStream out, Lock lock, CheckCartRequest message)
	{
		super(out, lock, message);
	}

}
