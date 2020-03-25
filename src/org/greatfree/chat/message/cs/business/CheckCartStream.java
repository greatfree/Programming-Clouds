package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 12/11/2017, Bing Li
public class CheckCartStream extends OutMessageStream<CheckCartRequest>
{

	public CheckCartStream(ObjectOutputStream out, Lock lock, CheckCartRequest message)
	{
		super(out, lock, message);
	}

}
