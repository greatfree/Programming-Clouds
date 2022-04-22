package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

// Created: 12/07/2017, Bing Li
public class RemoveFromCartStream extends MessageStream<RemoveFromCartNotification>
{

	public RemoveFromCartStream(ObjectOutputStream out, Lock lock, RemoveFromCartNotification message)
	{
		super(out, lock, message);
	}

}
