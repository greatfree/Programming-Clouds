package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 12/07/2017, Bing Li
public class PlaceOrderStream extends MessageStream<PlaceOrderNotification>
{

	public PlaceOrderStream(ObjectOutputStream out, Lock lock, PlaceOrderNotification message)
	{
		super(out, lock, message);
	}

}
