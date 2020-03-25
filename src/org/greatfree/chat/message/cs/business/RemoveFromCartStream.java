package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 12/07/2017, Bing Li
public class RemoveFromCartStream extends OutMessageStream<RemoveFromCartNotification>
{

	public RemoveFromCartStream(ObjectOutputStream out, Lock lock, RemoveFromCartNotification message)
	{
		super(out, lock, message);
	}

}
