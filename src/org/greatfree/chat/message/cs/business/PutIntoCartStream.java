package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 12/06/2017, Bing Li
public class PutIntoCartStream extends MessageStream<PutIntoCartNotification>
{

	public PutIntoCartStream(ObjectOutputStream out, Lock lock, PutIntoCartNotification message)
	{
		super(out, lock, message);
	}

}
